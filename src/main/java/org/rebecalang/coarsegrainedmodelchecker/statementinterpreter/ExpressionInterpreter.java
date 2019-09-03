package org.rebecalang.coarsegrainedmodelchecker.statementinterpreter;

import org.rebecalang.coarsegrainedmodelchecker.ActorState;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;

public class ExpressionInterpreter implements StatementInterpreter {

	public Object interpret(Statement statement, ActorState actorState) {
		Expression expression = (Expression) statement;
		if(expression instanceof BinaryExpression) {
			
		}
		return null;
	}

}
