package org.khaale.mdp.realtime.processing.services;

import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.khaale.mdp.realtime.common.entities.Candle;
import org.khaale.mdp.realtime.common.entities.Trade;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CacheProvider {

    private final Ignite ignite;

    public IgniteCache<Long, Trade> getTradesCache() {
        return ignite.getOrCreateCache("trades");
    }

    public IgniteCache<String, Candle> getCandleCache(Duration candleDuration) {
        CacheConfiguration<String, Candle> cfg = new CacheConfiguration<>();
        cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        cfg.setName("candles_" + candleDuration);

        return ignite.getOrCreateCache(cfg);
    }
}
