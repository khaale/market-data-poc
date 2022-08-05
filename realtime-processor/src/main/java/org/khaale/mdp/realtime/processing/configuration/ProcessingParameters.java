package org.khaale.mdp.realtime.processing.configuration;

import lombok.Getter;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;


public class ProcessingParameters {

    public static final List<Duration> candleDurations = Arrays.asList(
            Duration.ofMinutes(1),
            Duration.ofMinutes(5),
            Duration.ofMinutes(10),
            Duration.ofMinutes(15),
            Duration.ofMinutes(30)
    );
}
