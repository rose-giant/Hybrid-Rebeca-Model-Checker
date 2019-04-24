package org.rebecalang.interpreter.statementinterpreter;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConditionalStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.interpreter.ActorState;

public class ConditionalStatementInterpreter implements StatementInterpreter {

	public Object interpret(Statement statement, ActorState actorState) {
		ConditionalStatement expression = (ConditionalStatement) statement;
		Object interpretedCondition = StatementContainer.getInstance().interpret(expression.getCondition(), actorState);
		if (interpretedCondition.equals(Boolean.TRUE))
			StatementContainer.getInstance().interpret(expression.getStatement(), actorState);
		else if (expression.getElseStatement() != null)
			StatementContainer.getInstance().interpret(expression.getElseStatement(), actorState);

		return null;
	}

}
