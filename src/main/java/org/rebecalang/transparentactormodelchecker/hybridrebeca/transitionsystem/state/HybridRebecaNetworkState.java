package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state;

import org.rebecalang.compiler.utils.Pair;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class HybridRebecaNetworkState implements Serializable {
    private HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> receivedMessages;

    public HybridRebecaNetworkState() {
        receivedMessages = new HashMap<Pair<String,String>, ArrayList<HybridRebecaMessage>>();
    }

    public HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> getReceivedMessages() {
        return receivedMessages;
    }
    public void setReceivedMessages(HashMap<Pair<String, String>, ArrayList<HybridRebecaMessage>> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    public void addMessage(HybridRebecaMessage message) {
        Pair<String, String> key = new Pair<String, String>(
                message.getSender().getId(), message.getReceiver().getId());
        if(!receivedMessages.containsKey(key))
            receivedMessages. put(key, new ArrayList<HybridRebecaMessage>());
        receivedMessages.get(key).add(message);
    }

}
