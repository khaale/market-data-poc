package org.khaale.mdp.realtime.processing.processing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import org.apache.ignite.transactions.TransactionOptimisticException;
import org.khaale.mdp.realtime.common.entities.Candle;
import org.khaale.mdp.realtime.common.entities.Trade;
import org.khaale.mdp.realtime.processing.services.CacheProvider;

import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Slf4j
public class CandleMakingProcessor implements TradeProcessor {

    private final Duration candleDuration;
    private final CacheProvider cacheProvider;
    private final Ignite ignite;
    private IgniteCache<String, Candle> candleCache;

    @Override
    public void process(Collection<Trade> trades) {
        log.info("Processing trades..");
        if (Objects.isNull(candleCache)) {
            candleCache = cacheProvider.getCandleCache(candleDuration);
        }

        while (true) {
            try (var tx = ignite.transactions().txStart(TransactionConcurrency.PESSIMISTIC, TransactionIsolation.SERIALIZABLE)) {
                var candles = new TreeMap<String, Candle>();
                var candleKeys = trades.stream()
                        .map(t -> Candle.getKey(
                                t.getSecId(),
                                Candle.getStartTime(candleDuration, t.getTradeTime()))
                        )
                        .collect(Collectors.toCollection(TreeSet::new));
                var existingCandles = candleCache.getAll(candleKeys);
                for (var trade : trades) {
                    var candle = make(
                            candleDuration,
                            trade,
                            existingCandles::get);
                    existingCandles.put(candle.getKey(), candle);
                    candles.put(candle.getKey(), candle);
                }
                candleCache.putAll(candles);
                tx.commit();
                break;
            } catch (TransactionOptimisticException e) {
                log.warn("Optimistic concurrency failed");
            }
        }
        log.info("Trades processed: {}", trades.size());
    }

    private Candle make(Duration candleDuration, Trade trade, Function<String,Candle> candleGetter) {
        var candleStartTime = Candle.getStartTime(candleDuration, trade.getTradeTime());
        var candleKey = Candle.getKey(trade.getSecId(), candleStartTime);

        var candle = candleGetter.apply(candleKey);
        var price = trade.getPrice();
        if (Objects.isNull(candle)) {
            candle = Candle.create(trade.getSecId(), candleStartTime, price, trade.getTradeNo(), trade.getSysTime());
        } else {
            candle.update(price, trade.getTradeNo(), trade.getSysTime());
        }
        return candle;
    }
}
