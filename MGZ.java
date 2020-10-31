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
            if (node.prev.size() - 1 > node.prev.indexOf(prev) && !node.prev.get(node.prev.indexOf(prev)).cycle
            && !node.prev.get(node.prev.indexOf(prev)).doWhile && node.prev.get(node.prev.indexOf(prev)).next.size()==2
            && node.prev.get(node.prev.indexOf(prev)).next.indexOf(node) == 0) {
                branches ++;
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
