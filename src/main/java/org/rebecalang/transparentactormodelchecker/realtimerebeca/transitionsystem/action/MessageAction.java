package org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action;

import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaMessage;

public class MessageAction extends Action {

    private RealTimeRebecaMessage message;
    @Override
    public String getActionLabel() {
        return message.getName();
    }
    public MessageAction(RealTimeRebecaMessage message) {
        this.message = message;
    }
    public RealTimeRebecaMessage getMessage() {
        return message;
    }
    public void setMessage(RealTimeRebecaMessage message) {
        this.message = message;
    }
    public String toString() {
        return message.toString();
    }
}
