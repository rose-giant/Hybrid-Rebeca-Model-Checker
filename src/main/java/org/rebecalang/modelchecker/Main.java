package org.rebecalang.modelchecker;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.CoreRebecaModelChecker;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.corerebeca.State;

public class Main {

	public static void main(String[] args) throws ModelCheckingException {

		Set<CompilerFeature> compilerFeatures = new HashSet<CompilerFeature>();
		compilerFeatures.add(CompilerFeature.CORE_2_1);

		File rebecaFile = new File("src/test/resources/phils-site.rebeca");
		CoreRebecaModelChecker coreRebecaModelChecker = new CoreRebecaModelChecker(compilerFeatures, rebecaFile);
		coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.FINE_GRAINED_POLICY);
		try {
			coreRebecaModelChecker.modelCheck();
		} catch (ExceptionContainer e) {
			reportCompilerErrors(e);
		}
	}

	private static void reportCompilerErrors(ExceptionContainer exceptionContainer) {
		for (Exception e : exceptionContainer.getExceptions()) {
			if (e instanceof CodeCompilationException) {
				CodeCompilationException ce = (CodeCompilationException) e;
				System.out.println("Line " + ce.getLine() + ", Error: " + ce.getMessage());
			} else {
				System.out.println(e.getMessage());
				e.printStackTrace(System.out);
			}
		}
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

	// static public State deepCopy(State oldObj) {
	// ObjectOutputStream oos = null;
	// ObjectInputStream ois = null;
	// try {
	// ByteArrayOutputStream bos = new ByteArrayOutputStream(); // A
	// oos = new ObjectOutputStream(bos); // B
	// // serialize and pass the object
	// oos.writeObject(oldObj); // C
	// oos.flush(); // D
	// ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray()); // E
	// byte[] byteArray = bos.toByteArray();
	// System.out.println("bos: " + byteArray.length);
	// ois = new ObjectInputStream(bin); // F
	// // return the new object
	// return (State) ois.readObject(); // G
	// } catch (Exception e) {
	// System.out.println("Exception in ObjectCloner = " + e);
	// throw (new RuntimeException(e));
	// } finally {
	// try {
	// oos.close();
	// ois.close();
	//
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
}
