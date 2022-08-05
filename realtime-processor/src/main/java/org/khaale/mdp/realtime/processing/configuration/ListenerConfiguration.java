package org.khaale.mdp.realtime.processing.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.khaale.mdp.realtime.common.entities.Trade;
import org.khaale.mdp.realtime.processing.processing.TradeProcessorFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.BatchMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ListenerConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.consumer.concurrency}")
    private int concurrency;
    @Value("${app.kafka.topics.trades}")
    private String tradesTopic = "trades";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }

    private Map<String, Object> consumerConfigs(String consumerGroup) {
        Map<String, Object> prop = new HashMap<>();
        prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        prop.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        //prop.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        prop.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
        prop.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Trade.class.getName());
        return prop;
    }

    private ConsumerFactory<String, Trade> consumerFactory(String consumerGroup) {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(consumerGroup));
    }

    private ConcurrentKafkaListenerContainerFactory<String, Trade> kafkaListenerContainerFactory(String consumerGroup) {
        ConcurrentKafkaListenerContainerFactory<String, Trade> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(consumerGroup));
        factory.setBatchListener(true);
        factory.setConcurrency(concurrency);
        return factory;
    }

//    @Bean
//    public ConcurrentMessageListenerContainer<String, Trade> tradeCachingConsumer(TradeProcessorFactory factory) {
//        var container =
//                kafkaListenerContainerFactory("group-trades").createContainer(tradesTopic);
//        var processor = factory.createTradeCachingProcessor();
//        container.setupMessageListener((BatchMessageListener<String, Trade>) tradeRecords -> {
//            var trades = tradeRecords.stream().map(ConsumerRecord::value).toList();
//            processor.process(trades);
//        });
//        return container;
//    }

    @Bean
    public List<ConcurrentMessageListenerContainer<String, Trade>> candleMakingConsumers(TradeProcessorFactory factory, ApplicationContext ctx) {
        return ProcessingParameters.candleDurations.stream().map(d -> {
            var container =
                    kafkaListenerContainerFactory("group-candles-" + d).createContainer(tradesTopic);
            var processor = factory.createCandleProcessor(d);
            container.setupMessageListener((BatchMessageListener<String, Trade>) tradeRecords -> {
                var trades = tradeRecords.stream().map(ConsumerRecord::value).toList();
                processor.process(trades);
            });
            ((ConfigurableApplicationContext)ctx).getBeanFactory().registerSingleton("kafka-listener-candle-" + d, container);
            return container;
        }).collect(Collectors.toList());
    }
}