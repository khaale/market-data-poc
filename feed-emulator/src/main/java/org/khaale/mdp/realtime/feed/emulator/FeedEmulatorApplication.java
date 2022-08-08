package org.khaale.mdp.realtime.feed.emulator;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.khaale.mdp.realtime.feed.emulator.configuration.ProducerConfiguration;
import org.khaale.mdp.realtime.feed.emulator.tradeload.MoexTradeLoader;
import org.khaale.mdp.realtime.common.entities.Trade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class FeedEmulatorApplication implements CommandLineRunner {

    private final MoexTradeLoader tradeLoader;
    private final KafkaTemplate<String, Trade> kafkaTemplate;

    private long tradesSent = 0L;
    private long batchStartTime = 0L;
    private final int statsBatchSize = 1000;
    @Value("${app.tradeLoader.batchDelay}")
    private int batchDelay = 10;

    public static void main(String[] args) {
        SpringApplication.run(FeedEmulatorApplication.class);
    }

    @Override
    public void run(String... args) {
        tradeLoader.onLoaded(this::produceTrades);
        batchStartTime = System.currentTimeMillis();

        int i = 0;
        // run until stopped externally
        while(true) {
            tradeLoader.load("samples/trades_AFLT.xml");
            log.info(
                    "Iteration #{}: iterationTradeNo={}, iterationTradeTime={}",
                    i,
                    tradeLoader.getIterationTradeNo(),
                    tradeLoader.getIterationTradeTime());
        }
    }

    @SneakyThrows
    private void produceTrades(List<Trade> trades) {

        trades.forEach(t -> {
            var partition = getPartition(t);
            kafkaTemplate.send(
                    "trades",
                    partition,
                    t.getTradeNo().toString(),
                    t);
                }
        );
        kafkaTemplate.flush();
        tradesSent += trades.size();
        if (batchDelay > 0) {
            Thread.sleep(batchDelay);
        }
        if (tradesSent % statsBatchSize == 0) {
            printStats(tradesSent);
            batchStartTime = System.currentTimeMillis();
            tradesSent = 0;
        }
    }

    private void printStats(long tradesSent) {
        var currentMs = System.currentTimeMillis();
        log.info("Sent {} trades in {}ms", tradesSent, currentMs - batchStartTime);
    }

    private Integer getPartition(Trade t) {
        var key = t.getSecId() + t.getTradeTime().getMinute();
        var hash = key.hashCode();
        return hash % ProducerConfiguration.PARTITION_COUNT;
    }
}
