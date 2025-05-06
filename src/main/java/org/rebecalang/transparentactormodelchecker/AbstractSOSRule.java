package org.rebecalang.transparentactormodelchecker;

import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaAbstractTransition;

public abstract class AbstractSOSRule<T> {
	public boolean isEnable(CoreRebecaActorState source) {
		return true;
	}
	
	public abstract CoreRebecaAbstractTransition<T> applyRule(T source);

	public abstract CoreRebecaAbstractTransition<T> applyRule(Action synchAction, T source);

}
