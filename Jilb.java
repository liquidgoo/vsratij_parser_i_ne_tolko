import java.util.HashSet;
import java.util.Stack;

public class Jilb {
    private HashSet<Node> visited;
    private Stack<Node> connectors;

    private int maxNesting;
    private int currentNesting;

    private boolean visit(Node node) {
        boolean foundNext = true;
        visited.add(node);

        if (node.inBranches < 1) {
            if (node.next.size() == 2 && !node.doWhile && !node.cycle) {
                currentNesting++;
                updateMax();
                if (!visited.contains(node.next.get(0))) {
                    foundNext = visit(node.next.get(0));
                } else {
                    node.next.get(0).inBranches--;
                    foundNext = false;
                }
//                if (!connectors.empty()) {
//                    Node connector = connectors.pop();
//                    if (node.next.get(0) == connector) connector.inBranches--;
//                    connectors.push(connector);
//                }
                if (foundNext) {
                    Node connector = connectors.pop();
                    connector.inBranches++;
                    connectors.push(connector);
                }
                if (!visited.contains(node.next.get(1)))
                    visit(node.next.get(1));
                else node.next.get(1).inBranches--;
                currentNesting--;
                if (foundNext) {
                    Node connector = connectors.pop();
                    visit(connector);
                }
            } else if (node.doWhile ^ node.cycle) {
                currentNesting++;
                updateMax();
                if (!visited.contains(node.next.get(0)))
                    visit(node.next.get(0));
                else node.next.get(0).inBranches--;
            } else if (node.next.size() > 0)
                if (!visited.contains(node.next.get(0)))
                    visit(node.next.get(0));
                else node.next.get(0).inBranches--;
            if (node.cycle) {
                currentNesting--;
                if (!visited.contains(node.next.get(1)))
                    visit(node.next.get(1));
                else node.next.get(1).inBranches--;
            }
        } else {
            foundNext = true;
            node.inBranches--;
            connectors.push(node);
        }
        return foundNext;
    }


    private void updateMax() {
        if (currentNesting > maxNesting) {
            maxNesting = currentNesting;
        }
    }

    public int operators() {
        return visited.size() - 2;
    }

    public int branching() {
        int i = 0;
        for (Node node : visited) {
            if (node.next.size() > 1) i++;
        }
        return i;
    }

    public double relativeComplexity() {
        return (float) branching() / operators();
    }

    public int nesting() {
        return maxNesting;
    }

    public Jilb(Graph graph) {
        visited = new HashSet<>();
        connectors = new Stack<>();
        maxNesting = currentNesting = -1;
        visit(graph.root);
    }
}
