package org.khaale.mdp.realtime.processing.processing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteDataStreamer;
import org.khaale.mdp.realtime.common.entities.Trade;
import org.khaale.mdp.realtime.processing.services.CacheProvider;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class TradeCachingProcessor implements TradeProcessor {

    private final CacheProvider cacheProvider;
    private final Ignite ignite;

    private IgniteDataStreamer<Long, Trade> dataStreamer;

    @Override
    public void process(Collection<Trade> trades) {
        log.info("Processing trades..");
        if (Objects.isNull(dataStreamer)) {
            var tradesCache = cacheProvider.getTradesCache();
            dataStreamer = ignite.dataStreamer(tradesCache.getName());
            dataStreamer.allowOverwrite(true);
        }

        trades.forEach(trade -> dataStreamer.addData(trade.getTradeNo(), trade));
        dataStreamer.flush();
        log.info("Trades processed: {}", trades.size());
    }
}
