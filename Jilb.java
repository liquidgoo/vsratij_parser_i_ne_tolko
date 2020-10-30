import java.util.HashSet;

public class Jilb {
    private HashSet<Node> nodes;
    private int maxNesting;
    private int currentNesting;

    private void visit(Node node) {

        for (Node prev : node.prev) {
            if (nodes.contains(prev)) {
                currentNesting--;
            }
        }
        currentNesting++;
        updateMax();

        nodes.add(node);

        if ((node.next.size() == 2 && !node.cycle && !node.doWhile) || node.cycle ^ node.doWhile) {
            currentNesting++;
            updateMax();
        }

        if (node.next.size() > 0 && !nodes.contains(node.next.get(0)))
            visit(node.next.get(0));

        if (node.cycle) {
            currentNesting--;
            updateMax();
        }

        if (node.next.size() > 1 && !nodes.contains(node.next.get(1))) {
            visit(node.next.get(1));
        }
    }


    private void updateMax() {
        if (currentNesting > maxNesting) {
            maxNesting = currentNesting;
        }
    }

    public int operators() {
        return nodes.size() - 2;
    }

    public int branching() {
        int i = 0;
        for (Node node : nodes) {
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
        nodes = new HashSet<>();
        maxNesting = currentNesting = -2;
        visit(graph.root);
    }
}
