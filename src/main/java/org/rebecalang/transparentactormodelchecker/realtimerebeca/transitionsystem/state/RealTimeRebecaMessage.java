package org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state;

import org.rebecalang.compiler.utils.Pair;
import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class RealTimeRebecaMessage implements Serializable {
    private RealTimeRebecaActorState sender;
    private RealTimeRebecaActorState receiver;
    private Pair<Float, Float> messageArrivalInterval;
    private String name;
    private HashMap<String, Object> parameters;

    public RealTimeRebecaMessage(RealTimeRebecaMessage m) {
        this.messageArrivalInterval = new Pair<>(
                m.getMessageArrivalInterval().getFirst(),
                m.getMessageArrivalInterval().getSecond()
        );
    }

    public void setMessageArrivalInterval(Pair<Float, Float> messageArrivalInterval) {
        this.messageArrivalInterval = messageArrivalInterval;
    }
    public Pair<Float, Float> getMessageArrivalInterval() {
        return messageArrivalInterval;
    }
    public RealTimeRebecaMessage() {
        parameters = new HashMap<String, Object>();
    }
    public RealTimeRebecaMessage(String name, HashMap<String, Object> parameters) {
        this.name = name;
        this.parameters = parameters;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public HashMap<String, Object> getParameters() {
        return parameters;
    }
    public void setParameters(HashMap<String, Object> parameters) {
        this.parameters = parameters;
    }
    public void addParameter(String name, Object value) {
        parameters.put(name, value);
    }
    public RealTimeRebecaActorState getSender() {
        return sender;
    }
    public void setSender(RealTimeRebecaActorState sender) {
        this.sender = sender;
    }
    public RealTimeRebecaActorState getReceiver() {
        return receiver;
    }
    public void setReceiver(RealTimeRebecaActorState receiver) {
        this.receiver = receiver;
    }
    public String toString() {
        return (sender == null ? "main" : sender.getId()) + "->" + receiver.getId() + "." + name + "()";
    }
}
