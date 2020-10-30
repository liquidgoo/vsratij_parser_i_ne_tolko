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
            System.out.println(node.complexity);
        }
    }

    private int count(Node node) {
        HashSet<Node> nodes = new HashSet<>();
        if (!node.cycle) {
            return visit(node.next.get(0), 2, nodes) + visit(node.next.get(1), 2, nodes);
        } else {
            if (node.next.get(0) == node) {
                return 1;
            } else {
                nodes.add(node);
                return visit(node.next.get(0), 1, nodes) + 1;
            }
        }
    }

    private int visit(Node node, int branches, HashSet<Node> nodes) {
        if (!nodes.contains(node)) {
            nodes.add(node);
            int connecting = (node.cycle ^ node.doWhile) ? 2 : 1;
            branches -= node.prev.size() - connecting;
            if (branches > 0) {
                if (node.next.size() == 2) {
                    if (!node.cycle) {
                        branches++;
                        return visit(node.next.get(0), branches, nodes) + visit(node.next.get(1), branches, nodes) + 1;
                    } else {
                        return visit(node.next.get(0), 1, nodes) + visit(node.next.get(1), 1, nodes) + 1;
                    }
                } else if (node.next.size() == 1) {
                    return visit(node.next.get(0), branches, nodes) + 1;
                } else return 0;
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
        System.out.println(absoluteComplexity());
    }
}
