package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.compiler.modelcompiler.ObjectModelUtils;
import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MainRebecDefinition;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.ModelChecker;
import org.rebecalang.modelchecker.corerebeca.*;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.ProgramCounter;
import org.rebecalang.modelchecker.setting.ModelCheckerSetting;
import org.rebecalang.modelchecker.setting.TimedRebecaModelCheckerSetting;
import org.rebecalang.modelchecker.timedrebeca.rilinterpreter.TimedMsgsrvCallInstructionInterpreter;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.timedrebeca.rilinstruction.TimedMsgsrvCallInstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.LinkedList;
import java.util.List;

@Component
@Qualifier("TIMED_REBECA")
public class TimedRebecaModelChecker extends ModelChecker {

    public final static String CURRENT_TIME = "current_time";
    public final static String RESUMING_TIME = "resuming_time";

	public TimedRebecaModelChecker() {
		super();
	}

	protected void initializeStatementInterpreterContainer() {
		super.initializeStatementInterpreterContainer();
		statementInterpreterContainer.registerInterpreter(TimedMsgsrvCallInstructionBean.class,
				appContext.getBean(TimedMsgsrvCallInstructionInterpreter.class));
	}

	@Autowired
	public void setTypeSystem(@Qualifier("TIMED_REBECA") AbstractTypeSystem typeSystem) {
		this.typeSystem = typeSystem;
	}

	@Override
	protected TimedState createFreshState() {
		return new TimedState();
	}

	@Override
	protected TimedActorState createAnActorInitialState(MainRebecDefinition mainDefinition) {
		TimedActorState timedActorState = (TimedActorState) super.createAnActorInitialState(mainDefinition);

		timedActorState.setCurrentTime(0);
		timedActorState.setResumingTime(0);

		return timedActorState;
	}

	@Override
	protected BaseActorState<TimedMessageSpecification> createInitialActorState() {
		return new TimedActorState();
	}

	@Override
	protected TimedRebecaModelCheckerSetting getModelCheckerSetting() {
		return (TimedRebecaModelCheckerSetting) modelCheckerSetting;
	}

	@Override
	protected void doModelChecking(RILModel transformedRILModel) throws ModelCheckingException {
		TimedRebecaModelChecker modelChecker = TimedRebecaModelCheckerFactory.getModelChecker(getModelCheckerSetting().getTransitionSystem());

		modelChecker.doModelChecking(transformedRILModel);
	}

	@Override
	protected TimedState createInitialStates(RebecaModel rebecaModel) {
		TimedState initialState = createFreshState();
		boolean isFTTS = getModelCheckerSetting().isFTTS();
		initialState.setFTTS(isFTTS);

		for (MainRebecDefinition definition : ObjectModelUtils.getMainRebecDefinition(rebecaModel)) {
			TimedActorState actorState = createAnActorInitialState(definition);
			actorState.setFTTS(isFTTS);
			initialState.putActorState(definition.getName(), actorState);
		}

		return initialState;
	}

	@Override
	protected void addRequiredScopeToScopeStack(BaseActorState<?> baseActorState, LinkedList<ReactiveClassDeclaration> actorDeclarationHierarchy) {
		super.addRequiredScopeToScopeStack(baseActorState, actorDeclarationHierarchy);
		addTimedScopeToScopeStack(baseActorState);
	}

	protected TimedState executeNewState(
			TimedState currentState,
			TimedActorState actorState,
			StatementInterpreterContainer statementInterpreterContainer,
			RILModel transformedRILModel,
			int stateCounter,
			boolean resume,
			TimedMessageSpecification msg) {

		TimedState newState = cloneState(currentState);
		TimedActorState newActorState = (TimedActorState) newState.getActorState(actorState.getName());

		if (resume)
			newActorState.resumeExecution(newState, statementInterpreterContainer, transformedRILModel, modelCheckingPolicy);
		else
			newActorState.execute(newState, statementInterpreterContainer, transformedRILModel, modelCheckingPolicy, msg);

		String transitionLabel = calculateTransitionLabel(actorState, newActorState);

		Long stateKey = Long.valueOf(newState.hashCode());

		if (!stateSpace.hasStateWithKey(stateKey)) {
			newState.setId(stateCounter++);
			stateSpace.addState(stateKey, newState);
			newState.clearLinks();
			currentState.addChildState(transitionLabel, newState);
			newState.addParentState(transitionLabel, currentState);
		} else {
			TimedState repeatedState = (TimedState) stateSpace.getState(stateKey);
			currentState.addChildState(transitionLabel, repeatedState);
			repeatedState.addParentState(transitionLabel, currentState);
		}

		return newState;
	}

	@Override
	protected TimedState cloneState(State<? extends BaseActorState<?>> currentState) {
		TimedState state = (TimedState) currentState;
		List<Pair<String, State<TimedActorState>>> childStates = state.getChildStates();
		List<Pair<String, State<TimedActorState>>> parentStates = state.getParentStates();
		currentState.clearLinks();
		TimedState newState = SerializationUtils.clone(state);
		for (BaseActorState<?> actorState : newState.getAllActorStates()) {
			actorState.setTypeSystem(typeSystem);
		}
		state.setParentStates(parentStates);
		state.setChildStates(childStates);
		newState.setFTTS(state.isFTTS());
		return newState;
	}

	protected String calculateTransitionLabel(TimedActorState actorState, TimedActorState newActorState) {
		String executingMessageName;

		if (actorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
			ProgramCounter pc = actorState.getPC();
			executingMessageName = pc.getMethodName();
			executingMessageName += " [" + pc.getLineNumber() + ",";
		} else {
			executingMessageName = actorState.getQueue().peek().getItem().getMessageName();
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

	private void addTimedScopeToScopeStack(BaseActorState<?> baseActorState) {
		baseActorState.pushInScopeStackForMethodCallInitialization("TimedRebec");
		baseActorState.addVariableToRecentScope(CURRENT_TIME, 0);
		baseActorState.addVariableToRecentScope(RESUMING_TIME, 0);
	}
}
