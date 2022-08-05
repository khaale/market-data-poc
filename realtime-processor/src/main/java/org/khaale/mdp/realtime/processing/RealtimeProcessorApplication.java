package org.khaale.mdp.realtime.processing;

import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.zk.ZookeeperDiscoverySpi;
import org.apache.ignite.springframework.boot.autoconfigure.IgniteConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;

/** Example of Ignite auto configurer. */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableKafka
public class RealtimeProcessorApplication {

    @Value("${ignite.zkConnectionString}")
    private String zkConnectionString;
    /**
     * Main method of the application.
     * @param args Arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(RealtimeProcessorApplication.class, args);
    }

    /**
     * Providing configurer for the Ignite.
     * @return Ignite Configurer.
     */
    @Bean
    public IgniteConfigurer configurer() {
        return cfg -> {
            var discoverySpi = new ZookeeperDiscoverySpi();
            discoverySpi.setZkConnectionString(zkConnectionString);
            cfg.setDiscoverySpi(discoverySpi);
            cfg.setCommunicationSpi(new TcpCommunicationSpi());
        };
    }
}