package org.rebecalang.modelchecker.statementinterpreter;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modelchecker.ActorState;

public interface StatementInterpreter {
	public Object interpret(Statement statement , ActorState actorState);
}
