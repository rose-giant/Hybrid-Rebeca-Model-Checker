package org.rebecalang.transparentactormodelchecker.realtimerebeca.rilutils;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("serial")
public class RILEquivalentActorClass implements Serializable {
    private HashMap<String, ArrayList<InstructionBean>> modes = new HashMap<>();
    private HashMap<String, ArrayList<InstructionBean>> methods = new HashMap<>();

    public void RILEquivalentActorClass() {
        this.modes = new HashMap<>();
        this.methods = new HashMap<>();
    }

    public HashMap<String, ArrayList<InstructionBean>> getMethods() {
        return methods;
    }

    public HashMap<String, ArrayList<InstructionBean>> getModes() {
        return modes;
    }

    public void setMethods(HashMap<String, ArrayList<InstructionBean>> methods) {
        this.methods = methods;
    }

    public void setModes(HashMap<String, ArrayList<InstructionBean>> modes) {
        this.modes = modes;
    }
}
