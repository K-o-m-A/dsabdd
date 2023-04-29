public class Node {
    private char variable;
    private String expression;
    private Node highChild;
    private Node lowChild;
    boolean visited = false;

    public Node(char variable, String expression, Node highChild, Node lowChild) {
        this.variable = variable;
        this.visited = false;
        this.expression = expression;
        this.highChild = highChild;
        this.lowChild = lowChild;
    }

    public char getVariable() {
        return variable;
    }

    public String getExpression() {
        return expression;
    }

    public Node getHighChild() {
        return highChild;
    }

    public Node getLowChild() {
        return lowChild;
    }

    public void setVariable(char variable) {
        this.variable = variable;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setHighChild(Node highChild) {
        this.highChild = highChild;
    }

    public void setLowChild(Node lowChild) {
        this.lowChild = lowChild;
    }
}
