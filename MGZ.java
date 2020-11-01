import java.util.HashSet;
import java.util.Stack;

public class MGZ {
    Stack<Node> branching;
    HashSet<Node> nodes;


    private void findBranching(Node node) {
        if (node.next.size() == 2) {
            branching.push(node);
        }
        nodes.add(node);
        if (node.next.size() > 0) {
            if (!nodes.contains(node.next.get(0))) {
                findBranching(node.next.get(0));
            }
            if (node.next.size() == 2) {
                if (!nodes.contains(node.next.get(1))) {
                    findBranching(node.next.get(1));
                }
            }
        }
    }

    private void countAll() {
        while (!branching.empty()) {
            Node node = branching.pop();
            node.complexity = count(node);
        }
    }

    private int count(Node node) {
        HashSet<Node> nodes = new HashSet<>();
        nodes.add(node);
        if (!node.cycle) {
            return visit(node.next.get(0), node, 2, nodes) + visit(node.next.get(1), node, 2, nodes);
        } else {
            if (node.next.get(0) == node) {
                return 1;
            } else {
                nodes.add(node);
                return visit(node.next.get(0), node, 2, nodes) + 1;
            }
        }
    }

    private int visit(Node node, Node prev, int branches, HashSet<Node> nodes) {
        if (!nodes.contains(node)) {
            nodes.add(node);
            int connecting = (node.cycle ^ node.doWhile) ? 1 : 0;
            int doNotCount = 0;
            for (int i = 0; i < node.prev.indexOf(prev); i++) {
                if (!nodes.contains(node.prev.get(i))) doNotCount++;
            }
            for (int i = node.prev.size() - 1; i >= 0; i--) {
                if (node.prev.get(i).isCase && !nodes.contains(node.prev.get(i))) {
                    branches++;
                    break;
                }
            }
            branches -= node.prev.size() - doNotCount - connecting;
            if (branches > 0) {
                if (node.next.size() == 2) {
                    if (!node.cycle) {
                        branches++;
                        return visit(node.next.get(0), node, branches, nodes) + visit(node.next.get(1), node, branches, nodes) + 1;
                    } else {
                        branches++;
                        return visit(node.next.get(0), node, branches, nodes) + visit(node.next.get(1), node, branches, nodes) + 1;
                    }
                } else if (node.next.size() == 1) {
                    branches++;
                    return visit(node.next.get(0), node, branches, nodes) + 1;
                } else return 1;
            } else return 1;
        } else return 0;
    }

    public int absoluteComplexity() {
        int i = 0;
        for (Node node : nodes) {
            i += node.complexity;
        }
        return i;
    }

    public double relativeComplexity() {
        return 1 - ((double) (nodes.size() - 1) / absoluteComplexity());
    }

    public MGZ(Graph graph) {
        branching = new Stack<>();
        nodes = new HashSet<>();
        findBranching(graph.root);
        countAll();
    }
}
