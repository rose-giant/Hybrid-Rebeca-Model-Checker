package org.rebecalang.transparentactormodelchecker.hybridrebeca.utils;

import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.rilutils.RILEquivalentActorClass;
import org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state.*;
import org.springframework.util.SerializationUtils;

public class HybridRebecaStateSerializationUtils {
    public static HybridRebecaNetworkState clone(HybridRebecaNetworkState object) {
        HybridRebecaNetworkState clone = SerializationUtils.clone(object);
        return clone;
    }

    public static Environment clone(Environment environment) {
        Environment clone = SerializationUtils.clone(environment);
        return clone;
    }

    public static HybridRebecaActorState clone(HybridRebecaActorState object) {
//        HybridRebecaActorState clone = KryoCloner.clone(object);
        HybridRebecaActorState clone = SerializationUtils.clone(object);
        return clone;
    }
    public static HybridRebecaSystemState clone(HybridRebecaSystemState object) {
        HybridRebecaSystemState clone = SerializationUtils.clone(object);

        RILModel rilModel = object.getActorsState().values().iterator().next().getRILModel();
        for(HybridRebecaActorState state : clone.getActorsStatesValues())
            state.setRILModel(rilModel);

        return clone;
    }

    public static HybridRebecaMessage clone(HybridRebecaMessage object) {
        HybridRebecaMessage clone = SerializationUtils.clone(object);
        return clone;
    }

    public static RILEquivalentActorClass clone(RILEquivalentActorClass object) {
        RILEquivalentActorClass clone = SerializationUtils.clone(object);

//        RILModel rilModel = object.getActorsState().values().iterator().next().getRILModel();
//        for(HybridRebecaActorState state : clone.getActorsStatesValues())
//            state.setRILModel(rilModel);

        return clone;
    }
}
