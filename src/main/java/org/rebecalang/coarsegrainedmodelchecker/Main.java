package org.rebecalang.coarsegrainedmodelchecker;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.rebecalang.coarsegrainedmodelchecker.statementinterpreter.BinaryExpressionInterpreter;
import org.rebecalang.coarsegrainedmodelchecker.statementinterpreter.BlockStatementInterpreter;
import org.rebecalang.coarsegrainedmodelchecker.statementinterpreter.ConditionalStatementInterpreter;
import org.rebecalang.coarsegrainedmodelchecker.statementinterpreter.DotPrimaryExpressionInterpreter;
import org.rebecalang.coarsegrainedmodelchecker.statementinterpreter.ExpressionInterpreter;
import org.rebecalang.coarsegrainedmodelchecker.statementinterpreter.LiteralInterpreter;
import org.rebecalang.coarsegrainedmodelchecker.statementinterpreter.StatementContainer;
import org.rebecalang.coarsegrainedmodelchecker.statementinterpreter.TermPrimaryExpressionInterpreter;
import org.rebecalang.coarsegrainedmodelchecker.statementinterpreter.UnaryExpressionInterpreter;
import org.rebecalang.compiler.modelcompiler.RebecaCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConditionalStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConstructorDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MainRebecDefinition;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;

import com.rits.cloning.Cloner;

public class Main {

	public static void main(String[] args) throws CodeCompilationException {

		StatementContainer.getInstance().registerInterpreter(Expression.class, new ExpressionInterpreter());
		StatementContainer.getInstance().registerInterpreter(BinaryExpression.class, new BinaryExpressionInterpreter());
		StatementContainer.getInstance().registerInterpreter(UnaryExpression.class, new UnaryExpressionInterpreter());
		StatementContainer.getInstance().registerInterpreter(Literal.class, new LiteralInterpreter());
		StatementContainer.getInstance().registerInterpreter(DotPrimary.class, new DotPrimaryExpressionInterpreter());
		StatementContainer.getInstance().registerInterpreter(TermPrimary.class, new TermPrimaryExpressionInterpreter());
		StatementContainer.getInstance().registerInterpreter(BlockStatement.class, new BlockStatementInterpreter());
		StatementContainer.getInstance().registerInterpreter(ConditionalStatement.class,
				new ConditionalStatementInterpreter());
		RebecaCompiler rebecaCompiler = new RebecaCompiler();
		Set<CompilerFeature> compilerFeatures = new HashSet<CompilerFeature>();
		compilerFeatures.add(CompilerFeature.CORE_2_1);
		compilerFeatures.add(CompilerFeature.TIMED_REBECA);

		 File rebecaFile = new File("src/test/resources/train-controller-two-trains.rebeca");
//		File rebecaFile = new File("src/test/resources/phils.rebeca");
		Pair<RebecaModel, SymbolTable> compilationResult = rebecaCompiler.compileRebecaFile(rebecaFile,
				compilerFeatures);

		StatementContainer.getInstance().registerSymbolTable(compilationResult.getSecond());

		interpret(compilationResult.getFirst());
		System.out.println(statespace.size());
	}

	static Hashtable<Long, State> statespace = new Hashtable<Long, State>();

	private static void interpret(RebecaModel model) throws CodeCompilationException {
		State state = new State();

		List<MainRebecDefinition> mainRebecDefinitions = model.getRebecaCode().getMainDeclaration()
				.getMainRebecDefinition();
		for (MainRebecDefinition definition : mainRebecDefinitions) {
			ActorState actorState = new ActorState();
			actorState.initializeScopeStack();
			actorState.pushInActorScope();
			ReactiveClassDeclaration metaData = (ReactiveClassDeclaration) TypesUtilities.getInstance()
					.getMetaData(definition.getType());
			for (FieldDeclaration fieldDeclaration : metaData.getStatevars()) {
				for (VariableDeclarator variableDeclator : fieldDeclaration.getVariableDeclarators()) {
					actorState.addVariableToRecentScope(variableDeclator.getVariableName(), 0);
				}
			}
			actorState.addVariableToRecentScope("self", actorState);

			actorState.setTypeName(TypesUtilities.getTypeName(definition.getType()));
			actorState.setQueue(new LinkedList<MessageSpecification>());
			actorState.setName(definition.getName());
			state.putActorState(definition.getName(), actorState);
		}

		for (MainRebecDefinition definition : mainRebecDefinitions) {
			ReactiveClassDeclaration metaData = (ReactiveClassDeclaration) TypesUtilities.getInstance()
					.getMetaData(definition.getType());
			List<FieldDeclaration> knownRebecs = metaData.getKnownRebecs();
			for (int i = 0; i < definition.getBindings().size(); i++) {
				Expression knownRebecDefExp = definition.getBindings().get(i);
				if (!(knownRebecDefExp instanceof TermPrimary))
					throw new RebecaRuntimeInterpreterException("not handled yet!");
				String name = ((TermPrimary) knownRebecDefExp).getName();
				String knownRebecName = getKnownRebecName(knownRebecs, i);
				ActorState actState = state.getActorState(name);
				state.getActorState(definition.getName()).addVariableToRecentScope(knownRebecName, actState);
			}

		}

		for (MainRebecDefinition definition : mainRebecDefinitions) {
			ReactiveClassDeclaration metaData = (ReactiveClassDeclaration) TypesUtilities.getInstance()
					.getMetaData(definition.getType());
			ConstructorDeclaration constructorDeclaration = metaData.getConstructors().get(0);

			StatementContainer.getInstance().interpret(constructorDeclaration.getBlock(),
					state.getActorState(definition.getName()));

		}

		statespace.put(new Long(state.hashCode()), state);
		doModelChecking();
		printStateSpace(state);

	}

	private static String getKnownRebecName(List<FieldDeclaration> knownRebecs, int i) {
		int cnt = 0;
		for (FieldDeclaration fd : knownRebecs) {
			for (VariableDeclarator vd : fd.getVariableDeclarators()) {
				if (cnt == i)
					return vd.getVariableName();
				cnt++;
			}
		}
		throw new RebecaRuntimeInterpreterException("this case should not happen!!");
	}

	private static void doModelChecking() {
		int stateCounter = 0;
		State initialState = statespace.get(statespace.keySet().iterator().next());
		LinkedList<State> nextStatesQueue = new LinkedList<State>();
		nextStatesQueue.add(initialState);
		Cloner cloner = new Cloner();
		while (!nextStatesQueue.isEmpty()) {
			State currentState = nextStatesQueue.pollFirst();
			for (ActorState actorState : currentState.getEnabledActors()) {
				State newState = cloner.deepClone(currentState);
				ActorState newActorState = newState.getActorState(actorState.getName());
				MessageSpecification executableMessage = newActorState.getQueue().poll();
				runMessageServer(executableMessage, newState, newActorState.getName());
				Long stateKey = new Long(newState.hashCode());
				String transitionMessageName = actorState.getName() + "." + executableMessage.getMessageName();
				if (!statespace.containsKey(stateKey)) {
					newState.setId(++stateCounter);
					nextStatesQueue.add(newState);
					statespace.put(stateKey, newState);
					newState.clearLinks();
					currentState.addChildState(transitionMessageName, newState);
					newState.addParentState(transitionMessageName, currentState);
				} else {
					State repeatedState = statespace.get(stateKey);
					currentState.addChildState(transitionMessageName, repeatedState);
					repeatedState.addParentState(transitionMessageName, currentState);
				}
			}
		}
	}

	private static void runMessageServer(MessageSpecification executableMessage, State newState, String owner) {

		Type ownerType;
		try {
			ActorState actorState = newState.getActorState(owner);
			ownerType = TypesUtilities.getInstance().getType(actorState.getTypeName());
			ReactiveClassDeclaration actorMetaData = (ReactiveClassDeclaration) TypesUtilities.getInstance()
					.getMetaData(ownerType);
			List<Type> argumentsTypes = new LinkedList<Type>();
			for (Object argument : executableMessage.getParameters()) {
				argumentsTypes.add(((Expression) argument).getType());
			}

			MethodDeclaration messageServerDeclaration = getMessageServer(actorMetaData,
					executableMessage.getMessageName(), argumentsTypes);
			actorState.pushInActorScope();
			actorState.addVariableToRecentScope("sender", executableMessage.getSenderActorState());
			StatementContainer.getInstance().interpret(messageServerDeclaration.getBlock(), actorState);

			actorState.popFromActorScope();
		} catch (CodeCompilationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static MethodDeclaration getMessageServer(ReactiveClassDeclaration rcd, String msgsrvName,
			List<Type> parameters) {
		next: for (MethodDeclaration md : rcd.getMsgsrvs()) {
			if (!md.getName().equals(msgsrvName))
				continue;
			if (md.getFormalParameters().size() != parameters.size())
				continue;
			for (int i = 0; i < md.getFormalParameters().size(); i++) {
				if (md.getFormalParameters().get(i).getType() != parameters.get(i))
					continue next;
			}
			return md;
		}
		throw new RebecaRuntimeInterpreterException("should not happen!!");
	}

	public static void printStateSpace(State state) {
		Set<Integer> visitedSet = new HashSet<Integer>();
		LinkedList<State> openBorderList = new LinkedList<State>();
		openBorderList.add(state);
		visitedSet.add(state.getId());
		System.out.println("S0 [label=\"S0\"];");
		while (!openBorderList.isEmpty()) {
			State polledFirst = openBorderList.pollFirst();
			for (Pair<String, State> child : polledFirst.getChildStates()) {
				int childId = child.getSecond().getId();
				System.out.println("S" + childId + "[label=\"S" + childId + "\"];");
				String transitionLabel = child.getFirst();
				transitionLabel = "";//transitionLabel.split("\\.")[0] + "." + transitionLabel.split("\\.")[0].toUpperCase();
				System.out.println("S" + polledFirst.getId() + " -> S"+ childId + "[label=\"" + transitionLabel + "\"];");

//				System.out
//						.println(polledFirst.getId() + "---[" + child.getFirst() + "]--->" + childId);
				if (!visitedSet.contains(childId)) {
					openBorderList.add(child.getSecond());
					visitedSet.add(childId);
				}
			}
		}
	}
}
