package org.rebecalang.transparentactormodelchecker.hybridrebeca.utils;

import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaActorState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.HybridRebecaSystemState;
import org.springframework.util.SerializationUtils;

public class HybridRebecaStateSerializationUtils {
    public static HybridRebecaNetworkState clone(HybridRebecaNetworkState object) {
        HybridRebecaNetworkState clone = SerializationUtils.clone(object);
        return clone;
    }
    public static HybridRebecaActorState clone(HybridRebecaActorState object) {
        HybridRebecaActorState clone = SerializationUtils.clone(object);
        clone.setRILModel(object.getRILModel());
        return clone;
    }
    public static HybridRebecaSystemState clone(HybridRebecaSystemState object) {
        HybridRebecaSystemState clone = SerializationUtils.clone(object);

        RILModel rilModel = object.getActorsState().values().iterator().next().getRILModel();
        for(CoreRebecaActorState state : clone.getActorsStatesValues())
            state.setRILModel(rilModel);

        return clone;
    }
}
