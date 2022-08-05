package org.khaale.mdp.realtime.processing.processing;

import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.khaale.mdp.realtime.processing.services.CacheProvider;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TradeProcessorFactory {

    private final CacheProvider cacheProvider;
    private final Ignite ignite;

    public CandleMakingProcessor createCandleProcessor(Duration candleDuration) {
        return new CandleMakingProcessor(candleDuration, cacheProvider, ignite);
    }

    public TradeCachingProcessor createTradeCachingProcessor() {
        return new TradeCachingProcessor(cacheProvider, ignite);
    }
}
