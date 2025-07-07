package org.rebecalang.transparentactormodelchecker.hybridrebeca.rilutils;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

import java.util.ArrayList;
import java.util.HashMap;

public class RILEquivalentActorClass {
    private HashMap<String, ArrayList<InstructionBean>> modes;
    private HashMap<String, ArrayList<InstructionBean>> methods;

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
