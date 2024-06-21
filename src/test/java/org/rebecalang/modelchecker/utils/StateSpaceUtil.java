package org.rebecalang.modelchecker.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.State;

public class StateSpaceUtil {
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
				transitionLabel = transitionLabel.split("\\.")[0] + "." + transitionLabel.split("\\.")[2].toUpperCase();
				System.out.println(
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
}
