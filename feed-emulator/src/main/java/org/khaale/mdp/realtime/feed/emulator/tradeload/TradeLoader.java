package org.khaale.mdp.realtime.feed.emulator.tradeload;

import org.khaale.mdp.realtime.common.entities.Trade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
public interface TradeLoader {

    void onLoaded(Consumer<List<Trade>> trade);
}
