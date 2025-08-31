package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.HashMap;

public class Environment implements Serializable {
    private HashMap<String, Serializable> envVars;

    public Environment() {
        envVars = new HashMap<String, Serializable>();
    }

    public void setVariableValue(String varName, Serializable value) {
        envVars.put(varName, value);
    }

    public Serializable getVariableValue(String varName) {
        return envVars.get(varName);
    }

    public boolean hasVariableInScope(String varName) {
        return envVars.containsKey(varName);
    }

    public String toString() {
        return "[" + envVars + "]";
    }

}
