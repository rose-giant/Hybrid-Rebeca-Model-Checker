package org.rebecalang.coarsegrainedmodelchecker.statementinterpreter;

import org.rebecalang.coarsegrainedmodelchecker.ActorState;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;

public interface StatementInterpreter {
	public Object interpret(Statement statement , ActorState actorState);
}
