package org.khaale.mdp.realtime.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class IgniteStatusPublisher {

    private final Ignite ignite;
    private final SimpMessagingTemplate simpleMessagingTemplate;

    @Scheduled(fixedRate = 30_000)
    public void publishStats() {
        var cluster = ignite.cluster();
        var nodeStatuses = cluster.nodes().stream().map( node-> {
            var metrics = node.metrics();
            return String.format(
                    "%s: [ id=%s, consistentId=%s] " +
                            "Metrics: sentMsgsCnt=%d, sentBytesCnt=%d, rcvdMsgsCnt=%d, rcvdBytesCnt=%d, outMesQueueSize=%d",
                    (node.isClient() ? "CLIENT NODE" : "CACHE NODE"),
                    node.id(),
                    node.consistentId(),
                    metrics.getSentMessagesCount(),
                    metrics.getSentBytesCount(),
                    metrics.getReceivedMessagesCount(),
                    metrics.getReceivedBytesCount(),
                    metrics.getOutboundMessagesQueueSize()
            );
        }).toList();
        nodeStatuses.forEach(s -> log.info("{}", s));
        simpleMessagingTemplate.convertAndSend("/topic/system/ignite-node-stats",nodeStatuses);
    }
}
