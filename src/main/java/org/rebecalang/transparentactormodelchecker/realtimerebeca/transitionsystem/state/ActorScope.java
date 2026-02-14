package org.rebecalang.transparentactormodelchecker.realtimerebeca.transitionsystem.state;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

import java.io.Serializable;
import java.util.HashMap;

public class ActorScope implements Serializable {
    private String blockName;
    private int PC;
    private HashMap<String, Object> scope;

    public ActorScope() {
        PC = 0;
        scope = new HashMap<>();
    }

    public int getPC() {
        return PC;
    }
    public void incrementPC(){
        this.PC++;
    }
    public void setPC(int PC) {
        this.PC = PC;
    }

    public HashMap<String, Object> getScope() {
        return scope;
    }
    public void setScope(HashMap<String, Object> scope) {
        this.scope = scope;
    }

    public String getBlockName() {
        return blockName;
    }
    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public void addVariableToScope(String varName, Object value) {
        scope.put(varName, value);
    }

    public void setVariableValue(Variable leftVarName, Object value) {
//        for(int cnt = 0; cnt < scope.size(); cnt++) {
//            if(!scope.get(cnt).containsKey(leftVarName.getVarName()))
//                continue;

        scope.put(leftVarName.getVarName(), value);
            return;
//        }
//        throw new RebecaRuntimeInterpreterException("variable \"" + leftVarName + "\" not found");
    }

    public Object getVariableValue(String varName) {
//        for(int cnt = 0; cnt < scope.size(); cnt++) {
            if(scope.containsKey(varName))
                return scope.get(varName);
//        }
        return null;
//        return environment.getVariableValue(varName);
    }

    public boolean hasVariableInScope(String varName) {
//        for(int cnt = 0; cnt < scope.size(); cnt++) {
            if(scope.containsKey(varName))
                return true;
//        }
//        return environment.hasVariableInScope(varName);
        return false;
    }

}
