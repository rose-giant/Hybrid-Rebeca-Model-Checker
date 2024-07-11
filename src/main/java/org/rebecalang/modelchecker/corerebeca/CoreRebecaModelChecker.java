package org.rebecalang.modelchecker.corerebeca;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.rebecalang.compiler.modelcompiler.ObjectModelUtils;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConstructorDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MainRebecDefinition;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.builtinmethod.BuiltInMethodExecutor;
import org.rebecalang.modelchecker.corerebeca.builtinmethod.ExternalMethodRepository;
import org.rebecalang.modelchecker.corerebeca.policy.AbstractPolicy;
import org.rebecalang.modelchecker.corerebeca.policy.CoarseGrainedPolicy;
import org.rebecalang.modelchecker.corerebeca.policy.FineGrainedPolicy;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.AssignmentInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.MsgsrvCallInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.DeclarationInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.EndMethodInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.EndMsgSrvInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.ExternalMethodCallInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.JumpIfNotInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.MethodCallInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.PopARInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.ProgramCounter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.PushARInstructionInterpreter;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMethodInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ExternalMethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PushARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.RebecInstantiationInstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

@Component
//@Log4j2
public class CoreRebecaModelChecker {
	
	@Autowired
	protected RebecaModelCompiler rebecaModelCompiler;
	
	@Autowired
	protected ExceptionContainer exceptionContainer;

	@Autowired
	protected Rebeca2RILModelTransformer rebeca2RILModelTransformer;
	
	@Autowired
	protected CoreRebecaTypeSystem coreRebecaTypeSystem;
	
	@Autowired
	protected StatementInterpreterContainer statementInterpreterContainer;

	@Autowired
	protected ExternalMethodRepository externalMethodRepository;
	
	@Autowired
	private ConfigurableApplicationContext appContext;

	
	protected StateSpace<ActorState> statespace;

	protected AbstractPolicy modelCheckingPolicy;

	public final static String FINE_GRAINED_POLICY = "fine";
	public final static String COARSE_GRAINED_POLICY = "coarse";
	
	public StateSpace<ActorState> getStatespace() {
		return statespace;
	}

	
	protected Pair<RebecaModel, SymbolTable> compileModel(File model, Set<CompilerExtension> extension, CoreVersion coreVersion) {
		return rebecaModelCompiler.compileRebecaFile(model, extension, coreVersion);
	}

	public void modelCheck(File model, 
			Set<CompilerExtension> extension, 
			CoreVersion coreVersion) throws ModelCheckingException {
		modelCheck(compileModel(model, extension, coreVersion),
				extension, coreVersion);
	}

	public void modelCheck(Pair<RebecaModel, SymbolTable> model, Set<CompilerExtension> extension, CoreVersion coreVersion) throws ModelCheckingException {
		this.statespace = new StateSpace<ActorState>();

		if(!exceptionContainer.exceptionsIsEmpty())
			return;

		RILModel transformedRILModel = 
				rebeca2RILModelTransformer.transformModel(model, extension, coreVersion);

		initializeStatementInterpreterContainer();
		
		generateFirstState(transformedRILModel, model);
		
		doFineGrainedModelChecking(transformedRILModel);
	}
	
	protected void initializeStatementInterpreterContainer() {
		statementInterpreterContainer.clear();
		statementInterpreterContainer.registerInterpreter(AssignmentInstructionBean.class,
				appContext.getBean(AssignmentInstructionInterpreter.class, 
						coreRebecaTypeSystem));
		statementInterpreterContainer.registerInterpreter(MsgsrvCallInstructionBean.class,
				appContext.getBean(MsgsrvCallInstructionInterpreter.class));
		statementInterpreterContainer.registerInterpreter(MethodCallInstructionBean.class,
				appContext.getBean(MethodCallInstructionInterpreter.class));
		statementInterpreterContainer.registerInterpreter(DeclarationInstructionBean.class,
				appContext.getBean(DeclarationInstructionInterpreter.class));
		statementInterpreterContainer.registerInterpreter(EndMethodInstructionBean.class,
				appContext.getBean(EndMethodInstructionInterpreter.class));
		statementInterpreterContainer.registerInterpreter(EndMsgSrvInstructionBean.class,
				appContext.getBean(EndMsgSrvInstructionInterpreter.class));
		statementInterpreterContainer.registerInterpreter(JumpIfNotInstructionBean.class,
				appContext.getBean(JumpIfNotInstructionInterpreter.class));
		statementInterpreterContainer.registerInterpreter(PopARInstructionBean.class,
				appContext.getBean(PopARInstructionInterpreter.class));
		statementInterpreterContainer.registerInterpreter(PushARInstructionBean.class,
				appContext.getBean(PushARInstructionInterpreter.class));
		statementInterpreterContainer.registerInterpreter(ExternalMethodCallInstructionBean.class,
				appContext.getBean(ExternalMethodCallInterpreter.class));

		externalMethodRepository.clear();
		externalMethodRepository.registerExecuter(BuiltInMethodExecutor.KEY,
				new BuiltInMethodExecutor());
	}

	protected void generateFirstState(RILModel transformedRILModel, Pair<RebecaModel, SymbolTable> model) {

		RebecaModel rebecaModel = model.getFirst();
		
		State<ActorState> initialState = createInitialStates(rebecaModel);

		List<MainRebecDefinition> mainRebecDefinitions = 
				ObjectModelUtils.getMainRebecDefinition(rebecaModel);		
		setInitialKnownRebecsOfActors(initialState, mainRebecDefinitions);

		callConstructorsOfActors(transformedRILModel, initialState, 
				mainRebecDefinitions);
		
		statespace.addInitialState(initialState);
	}

	protected State<ActorState> createFreshState() {
		return new State<ActorState>();
	}

	protected ActorState createFreshActorState() {
		ActorState actorState = new ActorState();
		actorState.setTypeSystem(coreRebecaTypeSystem);
		return actorState;
	}

	private State<ActorState> createInitialStates(RebecaModel rebecaModel) {

		State<ActorState> initialState = createFreshState();
		for (MainRebecDefinition definition : ObjectModelUtils.getMainRebecDefinition(rebecaModel)) {
			ActorState actorState = createAnActorInitialState(definition);
			initialState.putActorState(definition.getName(), actorState);
		}
		return initialState;
	}

	protected ActorState createAnActorInitialState(MainRebecDefinition mainDefinition) {
		ActorState actorState = createFreshActorState();
		
		LinkedList<ReactiveClassDeclaration> actorDeclarationHierarchy = 
				extractActorDeclarationHierarchy(mainDefinition);
		
		actorState.initializeScopeStack();
		
		for(ReactiveClassDeclaration rcd : actorDeclarationHierarchy) {
			actorState.pushInScopeStackForInheritanceStack(rcd.getName());

			//"self" must be added to all activation records of hierarchy
			//to have "self" in the scope of parent method calls
			actorState.addVariableToRecentScope("self", actorState);
			for (FieldDeclaration fieldDeclaration : rcd.getStatevars()) {
				for (VariableDeclarator variableDeclator : fieldDeclaration.getVariableDeclarators()) {
					actorState.addVariableToRecentScope(variableDeclator.getVariableName(), 
							retrieveDefaultValue(fieldDeclaration.getType()));
				}
			}
		}
		
		actorState.setTypeName(mainDefinition.getType().getTypeName());
		actorState.setQueue(new LinkedList<MessageSpecification>());
		actorState.setName(mainDefinition.getName());
		return actorState;
	}

	private Object retrieveDefaultValue(Type type) {
		if(type == CoreRebecaTypeSystem.BYTE_TYPE ||
				type == CoreRebecaTypeSystem.SHORT_TYPE ||
				type == CoreRebecaTypeSystem.INT_TYPE)
			return 0;
		if(type == CoreRebecaTypeSystem.FLOAT_TYPE ||
				type == CoreRebecaTypeSystem.DOUBLE_TYPE)
			return 0.0;
		if(type == CoreRebecaTypeSystem.BOOLEAN_TYPE)
			return false;
		if(type == CoreRebecaTypeSystem.STRING_TYPE)
			return "";
			
		return null;
	}

	private LinkedList<ReactiveClassDeclaration> extractActorDeclarationHierarchy(MainRebecDefinition definition) {
		LinkedList<ReactiveClassDeclaration> actorDeclarationHierarchy = 
				new LinkedList<ReactiveClassDeclaration>();
		Type type = definition.getType();
		while(type != null) {
			ReactiveClassDeclaration metaData;
			try {
				metaData = (ReactiveClassDeclaration) coreRebecaTypeSystem.getMetaData(type);
			} catch (CodeCompilationException e) {
				System.err.println("This exception should not happen!");
				e.printStackTrace();
				break;
			}
			actorDeclarationHierarchy.addFirst(metaData);
			type = metaData.getExtends();
		}
		return actorDeclarationHierarchy;
	}
	
	
	private void callConstructorsOfActors(
			RILModel transformedRILModel,
			State<ActorState> initialState, 
			List<MainRebecDefinition> mainRebecDefinitions) {
		ArrayList<InstructionBean> mainInstructions = 
				transformedRILModel.getInstructionList("main");
		int cnt = 1;
		for (MainRebecDefinition definition : mainRebecDefinitions) {
			ReactiveClassDeclaration metaData;
			try {
				metaData = (ReactiveClassDeclaration) coreRebecaTypeSystem.getMetaData(definition.getType());
				ConstructorDeclaration constructorDeclaration = metaData.getConstructors().get(0);
				String computedConstructorName = RILUtilities.computeMethodName(metaData, constructorDeclaration);
				ActorState actorState = (ActorState) initialState.getActorState(definition.getName());
				actorState.pushInScopeStackForMethodCallInitialization(definition.getType().getTypeName());
				RebecInstantiationInstructionBean riib = (RebecInstantiationInstructionBean) mainInstructions.get(cnt++);
				for(Entry<String, Object> constructorParameter : riib.getConstructorParameters().entrySet()) {
					actorState.addVariableToRecentScope(
							constructorParameter.getKey(), constructorParameter.getValue());
				}
				actorState.initializePC(computedConstructorName, 0);
				ProgramCounter pc = actorState.getPC();
				ArrayList<InstructionBean> instructionsList = 
						transformedRILModel.getInstructionList(pc.getMethodName());
				while (actorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
					InstructionBean ib = instructionsList.get(pc.getLineNumber());
					statementInterpreterContainer.retrieveInterpreter(ib).interpret(
							ib, actorState, initialState);
				}
			} catch (CodeCompilationException e) {
				e.printStackTrace();
			}
		}
	}

	private void setInitialKnownRebecsOfActors(State<ActorState> initialState, 
			List<MainRebecDefinition> mainRebecDefinitions) {
		
		for (MainRebecDefinition definition : mainRebecDefinitions) {
			ActorState actorState = (ActorState) initialState.getActorState(definition.getName());
			LinkedList<ReactiveClassDeclaration> actorDeclarationHierarchy = 
					extractActorDeclarationHierarchy(definition);
			ActorScopeStack actorScopeStack = actorState.getActorScopeStack();
			List<Expression> bindings = definition.getBindings();
			int cnt = 0;
			int bindingCounter = 0;
			for(ReactiveClassDeclaration rcd : actorDeclarationHierarchy) {
				for (FieldDeclaration fieldDeclaration : rcd.getKnownRebecs()) {
					for (VariableDeclarator variableDeclator : fieldDeclaration.getVariableDeclarators()) {
						Expression knownRebecDefExp = bindings.get(bindingCounter++);
						ActorState knownRebecActorState = 
								extractActorStateBasedOnTheRebecName(
										initialState, knownRebecDefExp);
						actorScopeStack.addVariable(variableDeclator.getVariableName(), 
								knownRebecActorState, cnt);
					}
				}
				cnt++;
			}
		}
	}

	protected ActorState extractActorStateBasedOnTheRebecName(
			State<ActorState> initialState, Expression knownRebecDefExp) {
		if (!(knownRebecDefExp instanceof TermPrimary))
			throw new RebecaRuntimeInterpreterException("not handled yet!");
		String knownRebecName = ((TermPrimary) knownRebecDefExp).getName();
		return (ActorState) initialState.getActorState(knownRebecName);
	}

	protected void doFineGrainedModelChecking(
			RILModel transformedRILModel) throws ModelCheckingException {
		int stateCounter = 1;
		
		State<ActorState> initialState = statespace.getInitialState();
		LinkedList<State<ActorState>> nextStatesQueue = 
				new LinkedList<State<ActorState>>();
		nextStatesQueue.add(initialState);
		while (!nextStatesQueue.isEmpty()) {
			State<ActorState> currentState = nextStatesQueue.pollFirst();
//			log.info("Current state is:" + currentState);
			List<ActorState> enabledActors = currentState.getEnabledActors();
			if (enabledActors.isEmpty())
				throw new ModelCheckingException("Deadlock");
			for (BaseActorState actorState : enabledActors) {
				do {
					statementInterpreterContainer.clearNondeterminism();
					State<ActorState> newState = cloneState(currentState);
	
					ActorState newActorState = (ActorState) newState.getActorState(actorState.getName());
					newActorState.execute(newState, statementInterpreterContainer, 
							transformedRILModel, modelCheckingPolicy);
					String transitionLabel = calculateTransitionLabel(
							(ActorState) actorState, newActorState);
					Long stateKey = Long.valueOf(newState.hashCode());
	
					if (!statespace.hasStateWithKey(stateKey)) {
						newState.setId(stateCounter++);
						nextStatesQueue.add(newState);
						statespace.addState(stateKey, newState);
						newState.clearLinks();
						currentState.addChildState(transitionLabel, newState);
						newState.addParentState(transitionLabel, currentState);
//						log.info("Transition: " + currentState.getId() + "->" + newState.getId());
					} else {
						State<ActorState> repeatedState = statespace.getState(stateKey);
						currentState.addChildState(transitionLabel, repeatedState);
						repeatedState.addParentState(transitionLabel, currentState);
//						log.info("Transition: " + currentState.getId() + "->" + repeatedState.getId());
					}
				} while (statementInterpreterContainer.hasNondeterminism());
			}
		}
	}

	protected String calculateTransitionLabel(ActorState actorState, ActorState newActorState) {

		String executingMessageName;

		if (actorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
			ProgramCounter pc = actorState.getPC();
			executingMessageName = pc.getMethodName();
			executingMessageName += " [" + pc.getLineNumber() + ",";
		} else {
			executingMessageName = actorState.getQueue().peek().getMessageName();
			executingMessageName += " [START,";

		}

		if (newActorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
			ProgramCounter pc = newActorState.getPC();
			executingMessageName += pc.getLineNumber() + "]";
		} else {
			executingMessageName += "END]";

		}
		return actorState.getName() + "." + executingMessageName;
	}

	protected State<ActorState> cloneState(State<ActorState> currentState) {
		List<Pair<String, State<ActorState>>> childStates = currentState.getChildStates();
		List<Pair<String, State<ActorState>>> parentStates = currentState.getParentStates();
		currentState.clearLinks();
		State<ActorState> newState = SerializationUtils.clone(currentState);
		for(ActorState actorState : newState.getAllActorStates())
			actorState.setTypeSystem(coreRebecaTypeSystem);
		currentState.setParentStates(parentStates);
		currentState.setChildStates(childStates);
		return newState;
	}

	public void configPolicy(String policyName) throws ModelCheckingException {
		if (policyName.equals(COARSE_GRAINED_POLICY))
			modelCheckingPolicy = new CoarseGrainedPolicy();
		else if (policyName.equals(FINE_GRAINED_POLICY))
			modelCheckingPolicy = new FineGrainedPolicy();
		else
			throw new ModelCheckingException("Unknown policy " + policyName);
	}
}