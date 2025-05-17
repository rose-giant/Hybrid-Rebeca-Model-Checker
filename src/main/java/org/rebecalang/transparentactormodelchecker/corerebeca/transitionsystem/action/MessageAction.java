package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action;

import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessage;

public class MessageAction extends Action {

	private CoreRebecaMessage message;
	
	public MessageAction(CoreRebecaMessage message) {
		this.message = message;
	}
	public CoreRebecaMessage getMessage() {
		return message;
	}
	public void setMessage(CoreRebecaMessage message) {
		this.message = message;
	}
	
	@Override
	public String getActionLable() {
		return message.getName();
	}
	
	public String toString() {
		return message.toString();
	}

}
