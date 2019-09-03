package org.rebecalang.coarsegrainedmodelchecker.statementinterpreter;

import org.rebecalang.coarsegrainedmodelchecker.ActorState;
import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modelchecker.corerebeca.StatementContainer;

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
