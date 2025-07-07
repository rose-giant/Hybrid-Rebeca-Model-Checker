package org.rebecalang.transparentactormodelchecker.hybridrebeca.rilutils;

import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

import java.util.*;

public class ActorClassMakerFromRIL {

    private RILModel rilModel;
    public ActorClassMakerFromRIL(RILModel rilModel) {
        this.rilModel = rilModel;
        this.makeActorClasses();
        System.out.println("");
    }

    private ArrayList<InstructionBean> mainBlock = new ArrayList<>();
    private Map <String, ArrayList<InstructionBean>> envVars = new HashMap<>();
    private Map<String, RILEquivalentActorClass> actorClasses = new HashMap<>();

    private void makeActorClasses() {
        Set<String> methodNames = this.rilModel.getMethodNames();

        for (String methodName : methodNames) {
            if (methodName.startsWith("envVar")) {
                envVars.put(methodName, rilModel.getInstructionList(methodName));
            }
            else if (methodName.equals("main")) {
                this.mainBlock.addAll(rilModel.getInstructionList(methodName));
            } else {
                String[] parts = methodName.split("\\.");
                String actorName = parts[0];
                String pureMethodName = parts[1];
                boolean isMode = false;
                String modeName = "";
                if (pureMethodName.equals("mode")) {
                    modeName = parts[2];
                    isMode = true;
                }

                RILEquivalentActorClass actorClass = actorClasses.getOrDefault(actorName, new RILEquivalentActorClass());
                ArrayList<InstructionBean> instructions = rilModel.getInstructionList(methodName);
                if (isMode) {
                    if (actorClass.getModes() == null) {
                        actorClass.setModes(new HashMap<>());
                    }
                    actorClass.getModes().put(modeName, instructions);
                } else {
                    if (actorClass.getMethods() == null) {
                        actorClass.setMethods(new HashMap<>());
                    }
                    actorClass.getMethods().put(pureMethodName, instructions);
                }
                actorClasses.put(actorName, actorClass);
            }
        }
    }

}
