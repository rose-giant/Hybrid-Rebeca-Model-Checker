package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.compiler.modelcompiler.ObjectModelUtils;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.ModelChecker;
import org.rebecalang.modelchecker.corerebeca.*;
import org.rebecalang.modelchecker.setting.TimedRebecaModelCheckerSetting;
import org.rebecalang.modelchecker.timedrebeca.rilinterpreter.TimedMsgsrvCallInstructionInterpreter;
import org.rebecalang.modelchecker.timedrebeca.utils.SchedulingPolicy;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.timedrebeca.rilinstruction.TimedMsgsrvCallInstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.*;

@Component
@Qualifier("TIMED_REBECA")
public class TimedRebecaModelChecker extends ModelChecker {

	public final static String CURRENT_TIME = "current_time";
    public final static String RESUMING_TIME = "resuming_time";

	public int stateCounter = 1;

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
	protected TimedActorState createAnActorInitialState(RebecaModel rebecaModel, MainRebecDefinition mainDefinition) {
		TimedActorState timedActorState = (TimedActorState) super.createAnActorInitialState(rebecaModel, mainDefinition);

		timedActorState = setPriority(rebecaModel, timedActorState);

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
	protected void doModelChecking(RILModel transformedRILModel, RebecaModel rebecaModel) throws ModelCheckingException {
		// do nothing
	}

	@Override
	protected TimedState createInitialStates(RebecaModel rebecaModel) throws ModelCheckingException {
		TimedState initialState = createFreshState();
		TimedRebecaModelCheckerSetting timedRebecaModelCheckerSetting = getModelCheckerSetting();
		boolean isFTTS = timedRebecaModelCheckerSetting.isFTTS();

		initialState.setFTTS(isFTTS);

		for (MainRebecDefinition definition : ObjectModelUtils.getMainRebecDefinition(rebecaModel)) {
			TimedActorState actorState = createAnActorInitialState(rebecaModel, definition);
			actorState.setFTTS(isFTTS);

			if (timedRebecaModelCheckerSetting.getSchedulingPolicy() != null) {
				actorState.setSchedulingPolicy(timedRebecaModelCheckerSetting.getSchedulingPolicy());
			}

			List<Annotation> annotations;
			String scheduling;
			if(!(annotations = definition.getAnnotations()).isEmpty() && !(Objects.requireNonNull(scheduling = getAnnotation(annotations, "schedulingPolicy"))).isEmpty()) {
				actorState.setSchedulingPolicy(SchedulingPolicy.getSchedulingPolicy(scheduling));
			}

			initialState.putActorState(definition.getName(), actorState);
		}

		return initialState;
	}

	@Override
	protected RILModel getTransformModel(Pair<RebecaModel, SymbolTable> model) {
		RILModel transFormModel = rebeca2RILModelTransformer.transformModel(model, modelCheckerSetting.getCompilerExtension(), modelCheckerSetting.getCoreVersion());

		for (ReactiveClassDeclaration reactiveClassDeclaration : model.getFirst().getRebecaCode().getReactiveClassDeclaration()) {
			for (MsgsrvDeclaration msgsrv : reactiveClassDeclaration.getMsgsrvs()) {
				String computedMethodName = RILUtilities.computeMethodName(reactiveClassDeclaration, msgsrv);
				List<Annotation> annotations = msgsrv.getAnnotations();
				String periodStr;
				if (!annotations.isEmpty() && (periodStr = TimedRebecaModelChecker.getAnnotation(annotations, "period")) != null) {
					int period = Integer.parseInt(periodStr);
					Variable self = new Variable("self");
					ArrayList<InstructionBean> instructionList = transFormModel.getInstructionList(computedMethodName);
					TimedMsgsrvCallInstructionBean timedMsgsrvCallInstructionBean = new TimedMsgsrvCallInstructionBean(self, computedMethodName, new TreeMap<>(), period, Integer.MAX_VALUE);

					// Insert the new record at indexBeforeLast
					instructionList.add(instructionList.size() - 1, timedMsgsrvCallInstructionBean);

					transFormModel.addMethod(computedMethodName, instructionList);
				}
			}
		}

		return transFormModel;
	}

	protected TimedActorState setPriority(RebecaModel rebecaModel, TimedActorState timedActorState) {
		for (ReactiveClassDeclaration reactiveClassDeclaration : rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
			if (!reactiveClassDeclaration.getName().equals(timedActorState.getTypeName())) continue;
			List<Annotation> annotations = reactiveClassDeclaration.getAnnotations();
			String priorityStr;
			if(!annotations.isEmpty() && !(Objects.requireNonNull(priorityStr = getAnnotation(annotations, "priority"))).isEmpty()) {
				timedActorState.setPriority(Integer.parseInt(priorityStr));
			}
		}

		return timedActorState;
	}

	public static String getAnnotation(List<Annotation> annotations, String identifier) {
		for (Annotation annotation : annotations) {
			if (annotation.getIdentifier().equals(identifier)) {
				Expression value = annotation.getValue();

				if (value instanceof Literal) {
					return ((Literal)annotation.getValue()).getLiteralValue();
				}

				if (value instanceof TermPrimary) {
					return ((TermPrimary)annotation.getValue()).getName();
				}
			}
		}

		return null;
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
			RebecaModel rebecaModel,
			TimedMessageSpecification msg) {

		TimedState newState = cloneState(currentState);
		TimedActorState newActorState = executeNewTimedActorState(newState, actorState.getName(), transformedRILModel, rebecaModel, msg);

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

	protected TimedActorState createTimedActorState(TimedState newState, String actorName, TimedMessageSpecification timedMessageSpecification) {
		TimedActorState newActorState = (TimedActorState) newState.getActorState(actorName);

		TimedMessageSpecification firstInQueue = newActorState.getMessage(true);
		if (newActorState.getQueue().size() >= 1 && firstInQueue != null && firstInQueue.equals(timedMessageSpecification)) {
			newActorState.getTimedPriorityQueueItem(false);
		}

		return newActorState;
	}

	protected TimedActorState executeNewTimedActorState(
			TimedState newState,
			String actorName,
			RILModel transformedRILModel,
			RebecaModel rebecaModel,
			TimedMessageSpecification timedMessageSpecification
	) {
		TimedActorState newActorState = createTimedActorState(newState, actorName, timedMessageSpecification);

		newActorState.execute(newState, statementInterpreterContainer, transformedRILModel, rebecaModel, modelCheckingPolicy, timedMessageSpecification);

		return newActorState;
	}

	private void addTimedScopeToScopeStack(BaseActorState<?> baseActorState) {
		baseActorState.pushInScopeStackForMethodCallInitialization("TimedRebec");
		baseActorState.addVariableToRecentScope(CURRENT_TIME, 0);
		baseActorState.addVariableToRecentScope(RESUMING_TIME, 0);
	}
}
