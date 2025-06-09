package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.action;

import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaMessage;

public class MessageAction extends Action {

    private HybridRebecaMessage message;
    @Override
    public String getActionLabel() {
        return message.getName();
    }
    public MessageAction(HybridRebecaMessage message) {
        this.message = message;
    }
    public HybridRebecaMessage getMessage() {
        return message;
    }
    public void setMessage(HybridRebecaMessage message) {
        this.message = message;
    }
    public String toString() {
        return message.toString();
    }
}
