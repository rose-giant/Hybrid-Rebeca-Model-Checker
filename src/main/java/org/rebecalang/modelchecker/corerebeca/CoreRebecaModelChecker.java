package org.rebecalang.modelchecker.corerebeca;

import java.util.LinkedList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.ObjectModelUtils;
import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MainRebecDefinition;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.modelchecker.ModelChecker;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.rebecalang.compiler.utils.Pair;
import org.springframework.util.SerializationUtils;

@Component
@Qualifier("CORE_REBECA")
//@Log4j2
public class CoreRebecaModelChecker extends ModelChecker {
	@Autowired
	@Override
	public void setTypeSystem(@Qualifier("CORE_REBECA") AbstractTypeSystem typeSystem) {
		this.typeSystem = typeSystem;
	}

	@Override
	protected State<ActorState> createFreshState() {
		return new State<ActorState>();
	}

	@Override
	protected BaseActorState createInitialActorState() {
		return new ActorState();
	}

	@Override
	protected void doModelChecking(
			RILModel transformedRILModel) throws ModelCheckingException {
		int stateCounter = 1;
		
		State<ActorState> initialState = (State<ActorState>) stateSpace.getInitialState();
		LinkedList<State<ActorState>> nextStatesQueue = 
				new LinkedList<State<ActorState>>();
		nextStatesQueue.add(initialState);
		while (!nextStatesQueue.isEmpty()) {
			State<ActorState> currentState = nextStatesQueue.pollFirst();
			List<ActorState> enabledActors = currentState.getEnabledActors();
			if (enabledActors.isEmpty())
				throw new ModelCheckingException("Deadlock");
			for (ActorState actorState : enabledActors) {
				do {
					statementInterpreterContainer.clearNondeterminism();
					State<ActorState> newState = executeNewState(currentState, actorState, transformedRILModel, stateCounter);

					if (!newState.getParentStates().isEmpty()) {
						nextStatesQueue.add(newState);
					}
				} while (statementInterpreterContainer.hasNondeterminism());
			}
		}
	}

	@Override
	protected ActorState createAnActorInitialState(MainRebecDefinition mainDefinition) {
		ActorState actorState = (ActorState) createFreshActorState();

		LinkedList<ReactiveClassDeclaration> actorDeclarationHierarchy = extractActorDeclarationHierarchy(mainDefinition);

		actorState.initializeScopeStack();

		addRequiredScopeToScopeStack(actorState, actorDeclarationHierarchy);

		actorState.setTypeName(mainDefinition.getType().getTypeName());
		actorState.setName(mainDefinition.getName());
		return actorState;
	}

	protected State<ActorState> cloneState(State<ActorState> currentState) {
		List<Pair<String, State<ActorState>>> childStates = currentState.getChildStates();
		List<Pair<String, State<ActorState>>> parentStates = currentState.getParentStates();
		currentState.clearLinks();
		State<ActorState> newState = SerializationUtils.clone(currentState);
		for(ActorState actorState : newState.getAllActorStates())
			actorState.setTypeSystem(typeSystem);
		currentState.setParentStates(parentStates);
		currentState.setChildStates(childStates);
		return newState;
	}

	@Override
	protected State<? extends BaseActorState> createInitialStates(RebecaModel rebecaModel) {

		State<ActorState> initialState = createFreshState();
		for (MainRebecDefinition definition : ObjectModelUtils.getMainRebecDefinition(rebecaModel)) {
			ActorState actorState = createAnActorInitialState(definition);
			initialState.putActorState(definition.getName(), actorState);
		}
		return initialState;
	}

	private State<ActorState> executeNewState(
			State<ActorState> currentState,
			ActorState actorState,
			RILModel transformedRILModel,
			int stateCounter) {
		State<ActorState> newState = cloneState(currentState);

		ActorState newActorState = (ActorState) newState.getActorState(actorState.getName());
		newActorState.execute(newState, statementInterpreterContainer,
				transformedRILModel, modelCheckingPolicy);
		String transitionLabel = calculateTransitionLabel(actorState, newActorState);
		Long stateKey = Long.valueOf(newState.hashCode());

		if (!stateSpace.hasStateWithKey(stateKey)) {
			newState.setId(stateCounter++);
			stateSpace.addState(stateKey, newState);
			newState.clearLinks();
			currentState.addChildState(transitionLabel, newState);
			newState.addParentState(transitionLabel, currentState);
		} else {
			State<ActorState> repeatedState = (State<ActorState>) stateSpace.getState(stateKey);
			currentState.addChildState(transitionLabel, repeatedState);
			repeatedState.addParentState(transitionLabel, currentState);
		}

		return newState;
	}
}