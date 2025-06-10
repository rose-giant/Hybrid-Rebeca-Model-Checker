package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessage;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaNetworkState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class HybridRebecaNetworkState implements Serializable {
    private HashMap<Pair<String, String>, ArrayList<CoreRebecaMessage>> receivedMessages;

    public HybridRebecaNetworkState() {
        receivedMessages = new HashMap<Pair<String,String>, ArrayList<CoreRebecaMessage>>();
    }

    public HashMap<Pair<String, String>, ArrayList<CoreRebecaMessage>> getReceivedMessages() {
        return receivedMessages;
    }
    public void setReceivedMessages(HashMap<Pair<String, String>, ArrayList<CoreRebecaMessage>> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    public void addMessage(CoreRebecaMessage message) {
        Pair<String, String> key = new Pair<String, String>(
                message.getSender().getId(), message.getReceiver().getId());
        if(!receivedMessages.containsKey(key))
            receivedMessages. put(key, new ArrayList<CoreRebecaMessage>());
        receivedMessages.get(key).add(message);
    }

}
