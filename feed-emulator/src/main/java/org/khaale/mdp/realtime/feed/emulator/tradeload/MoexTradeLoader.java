package org.khaale.mdp.realtime.feed.emulator.tradeload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.khaale.mdp.realtime.common.entities.Trade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Service
public class MoexTradeLoader implements TradeLoader {

    @Value("${app.tradeLoader.suffix}")
    private int suffix = 0;


    @Value("${app.tradeLoader.batchSize}")
    private int batchSize = 10;

    private Consumer<List<Trade>> consumer;

    @Getter
    private LocalTime initialTradeTime;

    @Getter
    private LocalTime iterationTradeTime;

    @Getter
    private LocalTime lastTradeTime;

    @Getter
    private long initialTradeNo;

    @Getter
    private long iterationTradeNo;

    @Getter
    private long lastTradeNo;

    @SneakyThrows
    public int load(String sourcePath) {

        var factory = XMLInputFactory.newInstance();
        var tradesLoaded = 0;
        try (var input = getClass().getClassLoader().getResourceAsStream(sourcePath)) {
            var reader = factory.createXMLEventReader(input);
            boolean exit = false;
            var buffer = new ArrayList<Trade>(batchSize);
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    if ("row".equals(startElement.getName().getLocalPart())) {
                        Optional<Trade> tradeOpt = parseRow(startElement);
                        if (tradeOpt.isPresent()) {
                            tradesLoaded++;
                            buffer.add(tradeOpt.get());
                            if (buffer.size() == batchSize) {
                                consumer.accept(buffer);
                                buffer.clear();
                            }
                        }
                    }
                }
            }
        }
        iterationTradeNo = lastTradeNo;
        iterationTradeTime = lastTradeTime;
        return tradesLoaded;
    }

    private Optional<Trade> parseRow(StartElement startElement) {
        var tradeNoAttr = startElement.getAttributeByName(new QName("TRADENO"));
        if (Objects.isNull(tradeNoAttr)) {
            return Optional.empty();
        }

        var tradeNo = Long.parseLong(tradeNoAttr.getValue());
        var tradeTime = LocalTime.parse(startElement.getAttributeByName(new QName("TRADETIME")).getValue());
        if (initialTradeNo == 0) {
            initialTradeNo = tradeNo;
            initialTradeTime = tradeTime;
        }
        if (iterationTradeNo == 0) {
            iterationTradeNo = tradeNo;
            iterationTradeTime = tradeTime;
        }

        var trade = Trade.builder()
                .tradeNo(tradeNo + (iterationTradeNo - initialTradeNo))
                .secId(startElement.getAttributeByName(new QName("SECID")).getValue())
                .price(Double.parseDouble(startElement.getAttributeByName(new QName("PRICE")).getValue()))
                .tradeTime(tradeTime.plus(ChronoUnit.SECONDS.between(initialTradeTime, iterationTradeTime), ChronoUnit.SECONDS))
                .sysTime(Instant.now())
                .build();
        lastTradeNo = trade.getTradeNo();
        lastTradeTime = trade.getTradeTime();
        return Optional.of(trade);
    }



    @Override
    public void onLoaded(Consumer<List<Trade>> consumer) {
        this.consumer = consumer;
    }
}
