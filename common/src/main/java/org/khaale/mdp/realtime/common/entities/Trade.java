package org.khaale.mdp.realtime.common.entities;

import lombok.*;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Trade {

    private Long tradeNo;
    private LocalTime tradeTime;
    private String secId;
    private Double price;
    private Long quantity;
    private Double value;
    private String currency;
    private String buySell;
    private Instant sysTime;
}
