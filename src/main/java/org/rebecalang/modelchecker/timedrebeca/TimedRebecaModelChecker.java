package org.rebecalang.modelchecker.timedrebeca;

import java.io.File;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.modelchecker.Main;
import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.CoreRebecaModelChecker;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.StatementInterpreterContainer;
import org.rebecalang.modelchecker.timedrebeca.rilinterpreter.CallTimedMsgSrvInstructionInterpreter;
import org.rebecalang.modeltransformer.ril.timedrebeca.rilinstruction.CallTimedMsgSrvInstructionBean;

public class TimedRebecaModelChecker extends CoreRebecaModelChecker{

	public TimedRebecaModelChecker(Set<CompilerFeature> compilerFeatures, File rebecaFile) {
		super(compilerFeatures, rebecaFile);
	}

	protected void doFineGrainedModelChecking() throws ModelCheckingException {
		int stateCounter = 1;
		TimedState initialState = (TimedState) statespace.get(statespace.keySet().iterator().next());
		PriorityQueue<OpenBorderQueueItem> nextStatesQueue = new PriorityQueue<OpenBorderQueueItem>();
		int enablingTime = initialState.getEnablingTime();
		if (enablingTime == Integer.MAX_VALUE)
			throw new ModelCheckingException("Deadlock");		
		nextStatesQueue.add(new OpenBorderQueueItem(enablingTime, initialState));
		while (!nextStatesQueue.isEmpty()) {
			OpenBorderQueueItem openBorderQueueItem = nextStatesQueue.poll();
			TimedState currentState = openBorderQueueItem.getTimedState();
			
			List<ActorState> enabledActors = currentState.getEnabledActors();
			if (enabledActors.isEmpty())
				throw new ModelCheckingException("Deadlock");
			for (ActorState actorState : enabledActors) {

				TimedState newState = (TimedState) cloneState(currentState);

				ActorState newActorState = newState.getActorState(actorState.getName());
				newActorState.execute(newState, transformedRILModel, modelCheckingPolicy);
				String transitionLabel = calculateTransitionLabel(actorState, newActorState);
				Long stateKey = new Long(newState.hashCode());

				if (!statespace.containsKey(stateKey)) {
					newState.setId(stateCounter++);
					nextStatesQueue.add(new OpenBorderQueueItem(newState.getEnablingTime(),newState));
					statespace.put(stateKey, newState);
					newState.clearLinks();
					currentState.addChildState(transitionLabel, newState);
					newState.addParentState(transitionLabel, currentState);
				} else {
					State repeatedState = statespace.get(stateKey);
					currentState.addChildState(transitionLabel, repeatedState);
					repeatedState.addParentState(transitionLabel, currentState);
				}
			}
		}
		Main.printStateSpace(initialState);
	}
	
	protected TimedState createFreshState() {
		return new TimedState();
	}
	protected TimedActorState createFreshActorState() {
		return new TimedActorState();
	}
	
	protected void initializeStatementInterpreterContainer() {
		super.initializeStatementInterpreterContainer();

		StatementInterpreterContainer.getInstance().registerInterpreter(CallTimedMsgSrvInstructionBean.class,
				new CallTimedMsgSrvInstructionInterpreter());
	}

	protected String calculateTransitionLabel(ActorState actorState, ActorState newActorState) {
		return null;
	}
	
	public void configPolicy(String policyName) throws ModelCheckingException {
		
	}
	
	private class OpenBorderQueueItem implements Comparable<OpenBorderQueueItem> {
		private int time;
		private TimedState timedState;
		
		public OpenBorderQueueItem(int time, TimedState timedState) {
			super();
			this.time = time;
			this.timedState = timedState;
		}
		public int getTime() {
			return time;
		}
		public void setTime(int time) {
			this.time = time;
		}
		public TimedState getTimedState() {
			return timedState;
		}
		public void setTimedState(TimedState timedState) {
			this.timedState = timedState;
		}
		public int compareTo(OpenBorderQueueItem openBorderQueueItem) {
			if (this.time > openBorderQueueItem.time)
				return -1;
			if (this.time < openBorderQueueItem.time)
				return 1;
			return 0;
		}
	}
}
