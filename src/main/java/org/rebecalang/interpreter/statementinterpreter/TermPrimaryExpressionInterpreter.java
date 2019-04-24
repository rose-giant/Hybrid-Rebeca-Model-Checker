package org.rebecalang.interpreter.statementinterpreter;

import java.util.LinkedList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.SymbolTable.MethodInSymbolTableSpecifier;
import org.rebecalang.compiler.modelcompiler.SymbolTableException;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaLabelUtility;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.interpreter.ActorState;
import org.rebecalang.interpreter.MessageSpecification;
import org.rebecalang.interpreter.RebecaRuntimeInterpreterException;

public class TermPrimaryExpressionInterpreter implements StatementInterpreter {
	public Object interpret(Statement statement, ActorState baseActorState, ActorState contextActorState) {

		TermPrimary expression = (TermPrimary) statement;
		Object retreiveVariableValue = null;
		if (expression.getParentSuffixPrimary() == null) {
			if (expression.getIndices().size() != 0) {
				throw new RebecaRuntimeInterpreterException("Arrays are not supported yet!");
			}
			retreiveVariableValue = baseActorState.retreiveVariableValue(expression.getName());
		} else {
			try {
				SymbolTable symbolTable = StatementContainer.getInstance().getSymbolTable();
				Type baseActorType = TypesUtilities.getInstance().getType(baseActorState.getTypeName());
				ReactiveClassDeclaration actorMetaData = (ReactiveClassDeclaration) TypesUtilities.getInstance().getMetaData(baseActorType);
				Type type = TypesUtilities.getInstance().getType(actorMetaData.getName());
				List<Type> argumentsTypes = new LinkedList<Type>();
				List<Object> interpretedArguments = new LinkedList<Object>();
				for (Expression argument : expression.getParentSuffixPrimary().getArguments()) {
					argumentsTypes.add(argument.getType());
					interpretedArguments.add(StatementContainer.getInstance().interpret(argument, contextActorState));
				}

				MethodInSymbolTableSpecifier castableMethodSpecification = symbolTable
						.getCastableMethodSpecification(type, expression.getName(), argumentsTypes);
				if (castableMethodSpecification.getLabel() == CoreRebecaLabelUtility.MSGSRV) {
					MessageSpecification msgSpec = new MessageSpecification(castableMethodSpecification.getName(),
							interpretedArguments, contextActorState);
					baseActorState.addToQueue(msgSpec);
				} else {
					throw new RebecaRuntimeInterpreterException("Calling sync methods in dot primary not handled yet!");
				}
			} catch (SymbolTableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CodeCompilationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retreiveVariableValue;
	}

	public Object interpret(Statement statement, ActorState actorState) {
		return interpret(statement, actorState, actorState);
	}

}
