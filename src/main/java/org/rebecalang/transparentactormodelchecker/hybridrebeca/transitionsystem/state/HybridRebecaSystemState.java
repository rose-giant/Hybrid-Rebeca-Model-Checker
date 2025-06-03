package org.rebecalang.transparentactormodelchecker.hybridrebeca.transitionsystem.state;

import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;

import java.io.Serializable;
import java.util.HashMap;

public class HybridRebecaSystemState extends CoreRebecaSystemState implements Serializable {

    private HashMap<String, CoreRebecaActorState> actorsState;


}
