package org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.action;

import org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state.RealTimeRebecaMessage;

public class TakeMessageAction extends Action {
    private RealTimeRebecaMessage message;

    public TakeMessageAction(RealTimeRebecaMessage message) {
        this.message = message;
    }
    public RealTimeRebecaMessage getMessage() {
        return message;
    }
    public void setMessage(RealTimeRebecaMessage message) {
        this.message = message;
    }

    public String getActionLabel() {
        return message.getName();
    }

    public String toString() {
        return message.toString();
    }
}
