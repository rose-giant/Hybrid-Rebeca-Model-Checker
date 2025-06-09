package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state;

import java.util.HashMap;

public class HybridRebecaMessage {
    private HybridRebecaActorState sender;
    private HybridRebecaActorState receiver;

    private String name;
    private HashMap<String, Object> parameters;


    public HybridRebecaMessage() {
        parameters = new HashMap<String, Object>();
    }
    public HybridRebecaMessage(String name, HashMap<String, Object> parameters) {
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
    public HybridRebecaActorState getSender() {
        return sender;
    }
    public void setSender(HybridRebecaActorState sender) {
        this.sender = sender;
    }
    public HybridRebecaActorState getReceiver() {
        return receiver;
    }
    public void setReceiver(HybridRebecaActorState receiver) {
        this.receiver = receiver;
    }

    public String toString() {
        return (sender == null ? "main" : sender.getId()) + "->" + receiver.getId() + "." + name + "()";
    }
}
