package org.khaale.mdp.realtime.common.entities;

import lombok.*;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Candle {

    private String key;

    @AffinityKeyMapped
    private String secId;

    private LocalTime timeStart;
    private double hi;
    private double low;
    private double open;
    private long openTradeNo;
    private double close;
    private long closeTradeNo;
    private Instant createSysTime;
    private Instant createSourceSysTime;
    private Instant updateSysTime;
    private Instant updateSourceSysTime;
    private long numTrades = 0;

    public static String getKey(String secId, LocalTime timeStart) {
        return secId + "_" + timeStart;
    }

    public static LocalTime getStartTime(Duration candleDuration, LocalTime tradeTime) {
        var tradeTimeSeconds = tradeTime.toSecondOfDay();
        var candleDurationSeconds = (int) candleDuration.toSeconds();
        var candleStartTimeSeconds = (tradeTimeSeconds / candleDurationSeconds) * candleDurationSeconds;
        var candleStartTime = LocalTime.ofSecondOfDay(candleStartTimeSeconds);

        return candleStartTime;
    }

    public static Candle create(String secId, LocalTime timeStart, double price, long tradeNo, Instant sourceSysTime) {
        var key = getKey(secId, timeStart);
        var createTime = Instant.now();
        return Candle.builder()
                .key(key)
                .secId(secId)
                .timeStart(timeStart)
                .open(price)
                .openTradeNo(tradeNo)
                .close(price)
                .closeTradeNo(tradeNo)
                .hi(price)
                .low(price)
                .createSysTime(createTime)
                .createSourceSysTime(sourceSysTime)
                .updateSysTime(createTime)
                .updateSourceSysTime(sourceSysTime)
                .numTrades(1)
                .build();
    }

    public void update(double price, long tradeNo, Instant sourceSysTime) {
        if (tradeNo > closeTradeNo) {
            close = price;
        }

        if (tradeNo < openTradeNo) {
            open = price;
        }
        hi = Math.max(hi, price);
        low = Math.min(low, price);
        updateSysTime = Instant.now();
        updateSourceSysTime = sourceSysTime;
        numTrades = numTrades+1;
    }
}
