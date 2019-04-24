package org.rebecalang.modelchecker.statementinterpreter;

import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.modelchecker.ActorState;
import org.rebecalang.modelchecker.RebecaRuntimeInterpreterException;

public class UnaryExpressionInterpreter implements StatementInterpreter {

	public Object interpret(Statement statement, ActorState actorState) {
		UnaryExpression unaryExpression = (UnaryExpression) statement;
		String operator = unaryExpression.getOperator();
		Object result = null;
		Object interpretedExpression = StatementContainer.getInstance().interpret(unaryExpression.getExpression(), actorState);
		if (operator.equals("++")) {
			
			throw new RebecaRuntimeInterpreterException("++ and -- are not handled yet!");
			
		} else if (operator.equals("--")) {
			
			throw new RebecaRuntimeInterpreterException("++ and -- are not handled yet!");

		} else if (operator.equals("-")) {
			result = SemanticCheckerUtils.evaluateConstantTerm("-", unaryExpression.getType(), 0,
				interpretedExpression);

		} else if (operator.equals("!")) {

			result = SemanticCheckerUtils.evaluateConstantTerm("!", unaryExpression.getType(),
					interpretedExpression, null);
		}
		result = BinaryExpressionInterpreter.typeAdjustment(result, unaryExpression.getType());

		return result;
	}

}
