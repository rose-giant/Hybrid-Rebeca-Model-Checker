package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.rebecalang.compiler.utils.Pair;

@SuppressWarnings("serial")
public class CoreRebecaNetworkState extends CoreRebecaAbstractState implements Serializable {
	private HashMap<Pair<String, String>, ArrayList<CoreRebecaMessage>> receivedMessages;
	
	public CoreRebecaNetworkState() {
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
