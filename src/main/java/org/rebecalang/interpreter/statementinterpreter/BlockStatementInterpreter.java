package org.rebecalang.interpreter.statementinterpreter;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.interpreter.ActorState;

public class BlockStatementInterpreter implements StatementInterpreter {

	public Object interpret(Statement statement, ActorState actorState) {
		BlockStatement blockStatement = (BlockStatement) statement;

		actorState.pushInActorScope();
		for (Statement st : blockStatement.getStatements())
			StatementContainer.getInstance().interpret(st, actorState);
		actorState.popFromActorScope();

		return null;
	}

}
