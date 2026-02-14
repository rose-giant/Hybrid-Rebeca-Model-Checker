package org.rebecalang.transparentactormodelchecker.realtimerebeca.compositionlevelsosrules;

import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;

public class DotExporter {

    private final Set<String> edges = new LinkedHashSet<>();

    public void addTransition(int sourceId, String transitionType, String actionStr, int destId) {
        edges.add(
                "    s" + sourceId + " -> s" + destId +
                        " [label=\"" + transitionType + "(" + actionStr + ")\"];"
        );
    }

    public void writeToFile(String fileName) {
        try (PrintWriter out = new PrintWriter(fileName)) {
            out.println("digraph G {");
            out.println("    rankdir=LR;");
            out.println();
            for (String e : edges) {
                out.println(e);
            }
            out.println("}");
        } catch (Exception e) {
            throw new RuntimeException("Failed to write DOT file", e);
        }
    }
}
