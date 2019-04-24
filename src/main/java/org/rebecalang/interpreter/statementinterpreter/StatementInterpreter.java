package org.rebecalang.interpreter.statementinterpreter;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.interpreter.ActorState;

public interface StatementInterpreter {
	public Object interpret(Statement statement , ActorState actorState);
}
