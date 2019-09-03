package org.rebecalang.coarsegrainedmodelchecker.statementinterpreter;

import java.util.HashSet;
import java.util.Set;

import org.rebecalang.coarsegrainedmodelchecker.ActorState;
import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modelchecker.corerebeca.StatementContainer;

public class BinaryExpressionInterpreter implements StatementInterpreter {

	public Object interpret(Statement statement, ActorState actorState) {
		BinaryExpression expression = (BinaryExpression) statement;
		String operator = expression.getOperator();
		Object result = null;
		Set<String> assignmentOperators = new HashSet<String>();
		assignmentOperators.add("+=");
		assignmentOperators.add("-=");
		assignmentOperators.add("*=");
		assignmentOperators.add("/=");
		assignmentOperators.add("&=");
		assignmentOperators.add("|=");
		assignmentOperators.add(">>=");
		assignmentOperators.add("<<=");
		assignmentOperators.add("=");
		
		if (!assignmentOperators.contains(operator)) {
			Object interpretedLeft = StatementContainer.getInstance().interpret(expression.getLeft(), actorState);
			Object interpretedRight = StatementContainer.getInstance().interpret(expression.getRight(), actorState);
			if(interpretedLeft instanceof ActorState)
				interpretedLeft = "'" + ((ActorState) interpretedLeft).getName() + "'";
			if(interpretedRight instanceof ActorState)
				interpretedRight = "'" + ((ActorState) interpretedRight).getName() + "'";
			result = SemanticCheckerUtils.evaluateConstantTerm(operator, expression.getType(), 
					interpretedLeft,
					interpretedRight);
			result = typeAdjustment(result, expression.getType());
		} else {
			if (operator.equals("=")) {
				if (expression.getLeft() instanceof DotPrimary)
					throw new RebecaRuntimeInterpreterException("Dotted Expression as L-value has to be resolved");
				else if (expression.getLeft() instanceof TermPrimary) {
					String name = ((TermPrimary) expression.getLeft()).getName();
					actorState.setVariableValue(name, StatementContainer.getInstance().interpret(expression.getRight(), actorState));

				}else
					throw new RebecaRuntimeInterpreterException("Unknown Type for L-value: " +expression.getLeft().getClass());

			}
		}

		return result;
	}

	static public Object typeAdjustment(Object result, Type type) {

		if (type == TypesUtilities.BYTE_TYPE)
			return (Byte.valueOf(result.toString()));
		if (type == TypesUtilities.SHORT_TYPE)
			return (Short.valueOf(result.toString()));
		if (type == TypesUtilities.INT_TYPE)
			return (Integer.valueOf(result.toString()));
		if (type == TypesUtilities.DOUBLE_TYPE)
			return (Double.valueOf(result.toString()));
		if (type == TypesUtilities.FLOAT_TYPE)
			return (Float.valueOf(result.toString()));
		if (type == TypesUtilities.STRING_TYPE)
			return result.toString();
		if (type == TypesUtilities.BOOLEAN_TYPE)
			return Boolean.valueOf(result.toString());
		return null;
	}

}
