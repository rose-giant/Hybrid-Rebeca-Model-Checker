package org.rebecalang.modelchecker.statementinterpreter;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.modelchecker.ActorState;

public class LiteralInterpreter implements StatementInterpreter {

	public Object interpret(Statement statement, ActorState actorState) {
		Literal expression = (Literal) statement;
		String literalValue = expression.getLiteralValue();
		if (expression.getType() == TypesUtilities.BYTE_TYPE)
			return Byte.valueOf(literalValue);
		if (expression.getType() == TypesUtilities.SHORT_TYPE)
			return Short.valueOf(literalValue);
		if (expression.getType() == TypesUtilities.INT_TYPE)
			return Integer.valueOf(literalValue);
		if (expression.getType() == TypesUtilities.DOUBLE_TYPE)
			return Double.valueOf(literalValue);
		if (expression.getType() == TypesUtilities.FLOAT_TYPE)
			return Float.valueOf(literalValue);
		if (expression.getType() == TypesUtilities.BOOLEAN_TYPE)
			return Boolean.valueOf(literalValue);
		
		return literalValue;
	}

}
