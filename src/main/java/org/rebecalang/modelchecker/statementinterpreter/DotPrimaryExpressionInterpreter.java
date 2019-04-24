package org.rebecalang.modelchecker.statementinterpreter;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PrimaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.modelchecker.ActorState;

public class DotPrimaryExpressionInterpreter implements StatementInterpreter {

	public Object interpret(Statement statement, ActorState actorState) {
		DotPrimary expression = (DotPrimary) statement;
		Object result = null;
		ActorState baseActorState = (ActorState) StatementContainer.getInstance().interpret(expression.getLeft(),
				actorState);
		PrimaryExpression right = expression.getRight();
		TermPrimaryExpressionInterpreter interpreter = (TermPrimaryExpressionInterpreter) StatementContainer
				.getInstance().getInterpreter(TermPrimary.class);
		interpreter.interpret(right, baseActorState, actorState);
		return result;
	}

}
