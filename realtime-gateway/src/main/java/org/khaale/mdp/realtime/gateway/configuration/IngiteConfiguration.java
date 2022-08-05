package org.khaale.mdp.realtime.gateway.configuration;

import org.khaale.mdp.realtime.gateway.CandlePublisher;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.query.ContinuousQuery;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.zk.ZookeeperDiscoverySpi;
import org.apache.ignite.springframework.boot.autoconfigure.IgniteConfigurer;
import org.khaale.mdp.realtime.common.entities.Candle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.event.CacheEntryEvent;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Configuration
@Slf4j
public class IngiteConfiguration {

    @Value("${ignite.zkConnectionString}")
    private String zkConnectionString;

    @Bean
    public IgniteConfigurer configurer() {
        return cfg -> {
            var discoverySpi = new ZookeeperDiscoverySpi();
            discoverySpi.setZkConnectionString(zkConnectionString);
            cfg.setDiscoverySpi(discoverySpi);
            cfg.setCommunicationSpi(new TcpCommunicationSpi());
            cfg.setClientMode(true);
        };
    }

    @Bean
    public Object listener(Ignite ignite, CandlePublisher publisher) {
        var candleCache = ignite.getOrCreateCache("candles_PT1M");
        log.info("Cache {} size = {}", candleCache.getName() ,candleCache.size());

        var query = new ContinuousQuery<String, Candle>();
        query.setInitialQuery(new ScanQuery<>());
        query.setLocalListener(events -> {
//            var eventsList =
//                    StreamSupport.stream(events.spliterator(), false).toList();
//            log.info(
//                    "Received {} candles, {} unique keys",
//                    eventsList.size(),
//                    eventsList.stream().map(e -> e.getValue().getKey()).collect(Collectors.toSet()).size());
            for (var event: events) {
                publisher.publish(event.getValue());
            }
        });

        candleCache.query(query);

        return candleCache;
    }
}
