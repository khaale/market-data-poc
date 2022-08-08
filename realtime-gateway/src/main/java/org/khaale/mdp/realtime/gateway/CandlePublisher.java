package org.khaale.mdp.realtime.gateway;

import lombok.RequiredArgsConstructor;
import org.khaale.mdp.realtime.common.entities.Candle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CandlePublisher {

    private final SimpMessagingTemplate simpleMessagingTemplate;

    public void publish(Candle candle) {
        var destination = String.format("/topic/candles_PT1M/%s", candle.getSecId());
        simpleMessagingTemplate.convertAndSend(destination, candle);
    }
}
