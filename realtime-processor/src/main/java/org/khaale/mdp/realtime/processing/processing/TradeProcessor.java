package org.khaale.mdp.realtime.processing.processing;

import org.khaale.mdp.realtime.common.entities.Trade;

import java.util.Collection;

@FunctionalInterface
public interface TradeProcessor {

    void process(Collection<Trade> trades);
}
