import java.util.Stack;
import java.util.ArrayList;

public class Graph {
    Node root;
    Node current;
    ArrayList<String> tokens;
    Stack<ArrayList<Node>> prevsStack;
    int line;

    private void manageToken(String token, boolean pop) {
        if (token.startsWith("for ") || token.startsWith("for(") || token.equals("for") ||
                token.startsWith("while ") || token.startsWith("while(") || token.equals("while")) {
            addOne(token);
            current.cycle = true;
            ArrayList<Node> prevs = new ArrayList<>();
            prevs.add(current);
            prevsStack.push(prevs);
            manageFor(token);
        } else if (token.startsWith("do ") || token.equals("do")) {
            manageDo(token);
        } else if (token.startsWith("if ") || token.startsWith("if(") || token.equals("if")) {
            addOne(token);
            ArrayList<Node> prevs = new ArrayList<>();
            prevs.add(current);
            prevsStack.push(prevs);
            manageIf(token);
            token = tokens.get(line);
            while (token.equals("") || token.equals(";")) {
                line++;
                token = tokens.get(line);
            }
            if (token.startsWith("else ") || token.equals("else")) {
                prevs = new ArrayList<>();
                prevs.add(current);
                prevsStack.push(prevs);
                manageElse(token);
            } else {
                prevs = prevsStack.pop();
                prevs.add(current);
                prevsStack.push(prevs);
            }
        } else if (token.startsWith("switch ") || token.startsWith("switch(") || token.equals("switch")) {
            manageSwitch(token);
        } else {
            addOne(token);
            ArrayList<Node> prevs = pop ? prevsStack.pop() : new ArrayList<>();
            prevs.add(current);
            prevsStack.push(prevs);
            line++;
        }
    }

    private void manageFor(String token) {
        Node forNode = current;
        int i = skipParentheses(token);
        if (i == -1) {
            line++;
            token = tokens.get(line);
            if (token.startsWith("{")) {
                line++;
                manageBlock(true);
            } else if (!token.startsWith(";")) {
                manageToken(token, false);
                line++;
            }
        } else {
            manageToken(token.substring(i), false);
        }
        ArrayList<Node> prevs = prevsStack.pop();
        forNode.prev.addAll(prevs);
        for (Node prev : prevs) {
            prev.next.add(forNode);
        }
        prevs = new ArrayList<>();
        prevs.add(forNode);
        prevsStack.push(prevs);
        current = forNode;
    }

    private void manageDo(String token) {
        String tokenName = token;
        Node node = current;
        int i = token.indexOf(" ");
        if (i == -1) {
            line++;
            token = tokens.get(line);
            if (token.startsWith("{")) {
                line++;
                manageBlock(true);
            } else {
                manageToken(token, false);
                line++;
            }
        } else {
            manageToken(token.substring(i), false);
        }
        while (!tokens.get(line).startsWith("while(") && !tokens.get(line).startsWith("while ")) line++;
        line++;

        addOne(tokenName);
        current.cycle = current.doWhile = true;

        ArrayList<Node> prevs = new ArrayList<>();
        prevs.add(current);
        prevsStack.push(prevs);

        node = node.next.get(node.next.size() - 1);
        node.doWhile = true;
        node.prev.add(current);
        current.next.add(node);
    }

    private void manageIf(String token) {
        Node ifNode = current;
        int i = skipParentheses(token);
        if (i == -1) {
            line++;
            token = tokens.get(line);
            if (token.startsWith("{")) {
                line++;
                manageBlock(false);
            } else if (!token.startsWith(";")) {
                manageToken(token, true);
            }
        } else {
            manageToken(token.substring(i), true);
        }
        current = ifNode;
    }

    private void manageElse(String token) {
        Node ifNode = current;
        int i = token.indexOf(' ');
        if (i == -1) {
            line++;
            token = tokens.get(line);
            if (token.startsWith("{")) {
                line++;
                manageBlock(false);
            } else if (!token.startsWith(";")) {
                manageToken(token, true);
                line++;
            }
        } else {
            while (i < token.length() && token.charAt(i) == ' ') i++;
            manageToken(token.substring(i), true);
        }
        ArrayList<Node> prevs = prevsStack.pop();
        ArrayList<Node> temp = prevsStack.pop();
        temp.addAll(prevs);
        prevsStack.push(temp);
    }

    private void manageSwitch(String token) {
        Node ifNode = current;
        line++;
        boolean fallInDefault = false;
        boolean skippedPop = false;
        while (!token.startsWith("}") && !token.equals("default")) {
            if (token.startsWith("case ")) {

                addOne(token);
                current.isCase = true;
                ifNode = current;
                ArrayList<Node> prevs = new ArrayList<>();
                prevs.add(current);
                prevsStack.push(prevs);
                line++;
                token = tokens.get(line);
                while (!token.startsWith("break")) {
                    if (token.equals("") || token.equals(";") || token.equals("{") || token.equals("}") || token.equals(":")) {
                        line++;
                        token = tokens.get(line);
                        continue;
                    }
                    if (token.startsWith("case ")) {
                        ArrayList<Node> temp = prevsStack.pop();
                        prevs = new ArrayList<>();
                        prevs.add(ifNode);
                        prevsStack.push(prevs);
                        addOne(token);
                        current.isCase = true;
                        ifNode = current;
                        temp.add(current);
                        prevsStack.push(temp);
                        line++;
                    } else if (token.startsWith("default")) {
                        fallInDefault = true;
                        prevs = prevsStack.pop();
                        prevs.add(ifNode);
                        prevsStack.push(prevs);
                        line++;

                    } else {
                        int j = 1;
                        if (tokens.get(line + 1).startsWith(";")) {
                            j = 2;
                        }
                        boolean pop = tokens.get(line + j).startsWith("break");
                        if (pop && !skippedPop) {
                            pop = false;
                            skippedPop = true;
                        }
                        manageToken(token, false);
                        if (pop) {
                            prevs = prevsStack.pop();
                            ArrayList<Node> temp = prevsStack.pop();
                            temp.addAll(prevs);
                            prevsStack.push(temp);
                        }
                    }
                    token = tokens.get(line);
                }
                if (current == ifNode && !skippedPop) {
                    prevs = prevsStack.pop();
                    ArrayList<Node> temp = prevsStack.pop();
                    temp.addAll(prevs);
                    prevsStack.push(temp);
                }
                prevs = new ArrayList<>();
                prevs.add(ifNode);
                prevsStack.push(prevs);
            } else {
                line++;
                token = tokens.get(line);
            }
        }
        if (token.equals("default")) {
            line += 2;
            token = tokens.get(line);
            while (!token.startsWith("break") && !token.startsWith("}")) {
                if (token.equals("") || token.equals(";")) {
                    line++;
                    token = tokens.get(line);
                    continue;
                }
                int j = 1;
                if (tokens.get(line + 1).startsWith(";")) {
                    j = 2;
                }
                boolean pop = tokens.get(line + 1).startsWith("break") || tokens.get(line + 1).startsWith("}");
                if (pop && !skippedPop) {
                    pop = false;
                    skippedPop = true;
                }
                manageToken(token, false);
                if (pop) {
                    ArrayList<Node> prevs = prevsStack.pop();
                    ArrayList<Node> temp = prevsStack.pop();
                    temp.addAll(prevs);
                    prevsStack.push(temp);
                }
                token = tokens.get(line);
            }
        } else {
            ArrayList<Node> temp = prevsStack.pop();
            if (!fallInDefault) {
                ArrayList<Node> prevs = prevsStack.pop();
                prevs.addAll(temp);
                prevsStack.push(prevs);
            }
        }
        while (!tokens.get(line).startsWith("}")) {
            line++;
        }
        line++;
    }

    private void manageBlock(boolean push) {
        while (line < tokens.size()) {
            String token = tokens.get(line);
            if (token.equals("}")) {
                line++;
                break;
            }
            if (line == tokens.size() - 1) {
                push = true;
            } else if (!tokens.get(line + 1).equals("}")) {
                push = true;
            }
            if (token.equals("") || token.equals(";")) {
                line++;
                continue;
            } else {
                manageToken(token, !push);
            }
        }

    }

    private void addOne(String token) {
        ArrayList<Node> prevs = prevsStack.pop();
        Node node = new Node(prevs, token);
        for (Node prev : prevs) {
            prev.next.add(node);
        }
        current = node;
    }


    private int skipParentheses(String token) {
        int i = token.indexOf("(");
        while (i == -1) {
            line++;
            token = tokens.get(line);
            i = token.indexOf("(");
        }
        int parentheses = 0;
        do {
            if (i >= token.length()) {
                i = 0;
                line++;
                token = tokens.get(line);
            }
            if (token.charAt(i) == '(') parentheses++;
            if (token.charAt(i) == ')') parentheses--;
            i++;

        } while (parentheses > 0);
        while (i < token.length() && token.charAt(i) == ' ') {
            i++;
        }
        return i < token.length() ? i : -1;
    }

    Graph(ArrayList<String> tokens) {
        this.tokens = tokens;
        this.line = 0;
        this.root = new Node("Start");
        this.current = root;
        prevsStack = new Stack<>();
        ArrayList<Node> prevs = new ArrayList<>();
        prevs.add(root);
        prevsStack.push(prevs);
        manageBlock(false);
        addOne("Finish");
        current.complexity = 0;
    }
}

class Node {
    ArrayList<Node> next, prev;
    String token;
    boolean cycle;
    boolean doWhile;
    boolean isCase;
    int complexity;
    int inBranches;

    Node(ArrayList<Node> prevs, String token) {
        this.token = token;
        next = new ArrayList<>();
        this.prev = new ArrayList<>();
        this.prev.addAll(prevs);
        complexity = 1;
        inBranches = prevs.size() - 1;
    }


    Node(String token) {
        this.token = token;
        next = new ArrayList<>();
        this.prev = new ArrayList<>();
        complexity = 1;
        inBranches = 0;
    }

}
