package org.khaale.mdp.realtime.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableWebSocketMessageBroker
public class RealtimeGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealtimeGatewayApplication.class, args);
    }

}
