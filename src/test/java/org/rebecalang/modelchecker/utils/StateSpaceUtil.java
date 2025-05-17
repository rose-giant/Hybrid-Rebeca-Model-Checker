package org.rebecalang.modelchecker.utils;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.timedrebeca.TimedActorState;

public class StateSpaceUtil {
	public static void printStateSpace(State<ActorState> state, PrintStream ps) {
		Set<Integer> visitedSet = new HashSet<Integer>();
		LinkedList<State<ActorState>> openBorderList = new LinkedList<State<ActorState>>();
		openBorderList.add(state);
		visitedSet.add(state.getId());
		ps.println("S0 [label=\"S0\"];");
		while (!openBorderList.isEmpty()) {
			State<ActorState> polledFirst = openBorderList.pollFirst();
			for (Pair<String, State<ActorState>> child : polledFirst.getChildStates()) {
				int childId = child.getSecond().getId();
				ps.println("S" + childId + "[label=\"S" + childId + "\"];");
				String transitionLabel = child.getFirst();
				transitionLabel = transitionLabel.split("\\.")[0] + "." + transitionLabel.split("\\.")[2].toUpperCase();
				ps.println(
						"S" + polledFirst.getId() + " -> S" + childId + "[label=\"" + transitionLabel + "\"];");

				// System.out
				// .println(polledFirst.getId() + "---[" + child.getFirst() + "]--->" +
				// childId);
				if (!visitedSet.contains(childId)) {
					openBorderList.add(child.getSecond());
					visitedSet.add(childId);
				}
			}
		}
	}
	public static void printTimedStateSpace(State<TimedActorState> state, PrintStream ps) {
		Set<Integer> visitedSet = new HashSet<Integer>();
		LinkedList<State<TimedActorState>> openBorderList = new LinkedList<State<TimedActorState>>();
		openBorderList.add(state);
		visitedSet.add(state.getId());
		ps.println("S0 [label=\"S0\"];");
		while (!openBorderList.isEmpty()) {
			State<TimedActorState> polledFirst = openBorderList.pollFirst();
			for (Pair<String, State<TimedActorState>> child : polledFirst.getChildStates()) {
				int childId = child.getSecond().getId();
				ps.println("S" + childId + "[label=\"S" + childId + "\"];");
				String transitionLabel = child.getFirst();
				transitionLabel = transitionLabel.split("\\.")[0] + "." + transitionLabel.split("\\.")[2].toUpperCase();
				ps.println(
						"S" + polledFirst.getId() + " -> S" + childId + "[label=\"" + transitionLabel + "\"];");
				if (!visitedSet.contains(childId)) {
					openBorderList.add(child.getSecond());
					visitedSet.add(childId);
				}
			}
		}
	}
}
