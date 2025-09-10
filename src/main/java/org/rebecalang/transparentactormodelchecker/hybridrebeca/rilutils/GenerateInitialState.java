package org.rebecalang.transparentactormodelchecker.hybridrebeca.rilutils;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.RebecInstantiationInstructionBean;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.Environment;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GenerateInitialState {

//    ActorClassMakerFromRIL maker;
    RILModel rilModel = new RILModel();
    HybridRebecaSystemState initialState = new HybridRebecaSystemState();
    public GenerateInitialState(RILModel rilModel) {
//        this.maker = actorClassMakerFromRIL;
        this.rilModel = rilModel;
        this.initialState = this.processMainBlock();
    }

    public HybridRebecaSystemState getInitialState() {
        return initialState;
    }

    public HybridRebecaSystemState processMainBlock() {
        ArrayList<InstructionBean> main = rilModel.getInstructionList("main");
        ArrayList<RebecInstantiationInstructionBean> instantiations =
                (ArrayList<RebecInstantiationInstructionBean>)
                 main.subList(1, main.size() - 1).stream()
                .filter(i -> i instanceof RebecInstantiationInstructionBean)
                .map(i -> (RebecInstantiationInstructionBean) i)
                .collect(Collectors.toList());

        HybridRebecaSystemState systemStateZero = new HybridRebecaSystemState();
        HybridRebecaNetworkState networkState = new HybridRebecaNetworkState();
        systemStateZero.setNetworkState(networkState);

        Pair<Float, Float> now = new Pair<>((float)0, (float)0);
        systemStateZero.setNow(now);
        systemStateZero.getNetworkState().setNow(now);
        Pair<Float, Float> inputInterval = new Pair<>((float)0, (float)10);
        systemStateZero.setInputInterval(inputInterval);

        Environment environment = new Environment();
        systemStateZero.setEnvironment(environment);

        for (RebecInstantiationInstructionBean instantiation: instantiations) {
            HybridRebecaActorState newActor = new HybridRebecaActorState("actor" + instantiations.indexOf(instantiation));
            String actorType = instantiation.getType().getTypeName();
//            newActor.setRilEquivalentActorClass(this.maker.getActorClasses().get(actorType));
            newActor.setRILModel(rilModel);
//            TODO: add the constructor to the scope
            newActor.addScope(actorType + "." + actorType);

            Map<String, Object> knownRebecs = instantiation.getBindings();
            for (Map.Entry<String, Object> entry : knownRebecs.entrySet()) {
                newActor.addVariableToScope(entry.getKey(), entry.getValue());
            }
            Map<String, Object> constructorArgs = instantiation.getConstructorParameters();
            for (Map.Entry<String, Object> entry : constructorArgs.entrySet()) {
                newActor.addVariableToScope(entry.getKey(), entry.getValue());
            }

            systemStateZero.setActorState(newActor.getId(), newActor);
        }

        //TODO: add the envVars to the system state's environment

        return systemStateZero;
    }
}
