package org.rebecalang.transparentactormodelchecker.hybridrebeca.rilutils;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.RebecInstantiationInstructionBean;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class GenerateInitialState {

    ActorClassMakerFromRIL maker;
    public GenerateInitialState(ActorClassMakerFromRIL actorClassMakerFromRIL) {
        this.maker = actorClassMakerFromRIL;
        this.processMainBlock();

    }

    public void processMainBlock() {
        ArrayList<InstructionBean> main = maker.getMainBlock();
        ArrayList<RebecInstantiationInstructionBean> instantiations =
                (ArrayList<RebecInstantiationInstructionBean>)
                 main.subList(1, main.size() - 1).stream()
                .filter(i -> i instanceof RebecInstantiationInstructionBean)
                .map(i -> (RebecInstantiationInstructionBean) i)
                .collect(Collectors.toList());
        System.out.println("");

        HybridRebecaSystemState systemStateZero = new HybridRebecaSystemState();
        HybridRebecaNetworkState networkState = new HybridRebecaNetworkState();
        systemStateZero.setNetworkState(networkState);
        for (RebecInstantiationInstructionBean instantiation: instantiations) {
            HybridRebecaActorState newActor = new HybridRebecaActorState("actor" + instantiations.indexOf(instantiation));
            String actorType = instantiation.getType().toString();
            newActor.setRilEquivalentActorClass(this.maker.getActorClasses().get(actorType));

        }
    }
}
