import java.util.ArrayList;
import java.util.List;

public class BDDFunctions {
    public class BDD {
        int numVars;
        double size;
        Node root;
        String variableOrder;

        public BDD(int numVars, double size, Node root, String variableOrder) {
            this.numVars = numVars;
            this.size = size;
            this.root = root;
            this.variableOrder = variableOrder;
        }
    }

    public BDD BDD_create(String bfunction, String variableOrder) {
        int numVars = variableOrder.length();

        Node root = decompose(new Node(' ', bfunction, null, null), variableOrder, 0);
        int expectedSize = ((int) Math.pow(2, variableOrder.length() + 1)) - 1;
        double size = countNodes(root);

        printNode(root, "", true);
//        double percentage = size / (expectedSize/100);
        System.out.println("Variable order : " + variableOrder);
        System.out.println("Expected size : " + expectedSize);
        System.out.println("Size : " + size);
//        System.out.println("Percentage : " + percentage);
        System.out.println("NumVars : " + numVars);
        return new BDD(numVars, size, root, variableOrder);
    }

    private int countNodes(Node node) {
        if (node == null) {
            return 0;
        } else {
            return 1 + countNodes(node.getHighChild()) + countNodes(node.getLowChild());
        }
    }


    public Node decompose(Node node, String variableOrder, int depth) {
        if (depth >= variableOrder.length()) {
            return node;
        }

        char var = variableOrder.charAt(depth);
        Node decomposedNode = new Node(node.getVariable(), node.getExpression(), null, null);

        List<Integer> varIndices = new ArrayList<Integer>();
        for (int i = 0; i < decomposedNode.getExpression().length(); i++) {
            char c = decomposedNode.getExpression().charAt(i);
            if (c == var || c == Character.toUpperCase(var) || c == Character.toLowerCase(var)) {
                varIndices.add(i);
            }
        }

        if (varIndices.size() >= 1) {
            boolean flag1Low = false;
            boolean flag1High = false;
            char highValue = '1';
            char lowValue = '0';
            String highChildExpression = decomposedNode.getExpression();
            String lowChildExpression = decomposedNode.getExpression();
            for (int idx : varIndices) {
                if (decomposedNode.getExpression().charAt(idx) == Character.toLowerCase(var)) {
                    highChildExpression = highChildExpression.substring(0, idx) + lowValue + highChildExpression.substring(idx + 1);
                    lowChildExpression = lowChildExpression.substring(0, idx) + highValue + lowChildExpression.substring(idx + 1);
                } else {
                    highChildExpression = highChildExpression.substring(0, idx) + highValue + highChildExpression.substring(idx + 1);
                    lowChildExpression = lowChildExpression.substring(0, idx) + lowValue + lowChildExpression.substring(idx + 1);
                }
            }

            // Remove parts of the expression
            //HIGH
            String[] highChildParts = highChildExpression.split("\\+");
            StringBuilder sb = new StringBuilder();
            int tmpLengthHigh = 0;
            for (String part : highChildParts) {
                if (!part.contains("0")) {
                    tmpLengthHigh++;
                }
            }

            if (tmpLengthHigh == 0) {
                highChildExpression = "0";
            } else {
                int tmpLengthPartHigh = 0;
                for (String part : highChildParts) {
                    if (!part.contains("0")) {
                        tmpLengthPartHigh++;
                        if (part.replaceAll("1", "").isEmpty()) {
                            sb.append("1+");
                            flag1High = true;
                            break;
                        }
                        if (tmpLengthPartHigh == tmpLengthHigh) {
                            sb.append(part);
                        } else {
                            sb.append(part).append("+");
                        }

                    }
                }

                if (flag1High) {
                    highChildExpression = "1";
                } else {
                    highChildExpression = sb.toString();
                }
            }

            //LOW
            String[] lowChildParts = lowChildExpression.split("\\+");
            sb = new StringBuilder();
            int tmpLengthLow = 0;
            for (String part : lowChildParts) {
                if (!part.contains("0")) {
                    tmpLengthLow++;
                }
            }

            if (tmpLengthLow == 0) {
                lowChildExpression = "0";
            } else {
                int tmpLengthPartLow = 0;
                for (String part : lowChildParts) {
                    if (!part.contains("0")) {
                        tmpLengthPartLow++;
                        if (part.replaceAll("1", "").isEmpty()) {
                            sb.append("1+");
                            flag1Low = true;
                            break;
                        }

                        if (tmpLengthPartLow == tmpLengthLow) {
                            sb.append(part);
                        } else {
                            sb.append(part).append("+");
                        }

                    }
                }

                if (flag1Low) {
                    lowChildExpression = "1";
                } else {
                    lowChildExpression = sb.toString();
                }
            }

            Node highChild = decompose(new Node(var, highChildExpression, null, null), variableOrder, depth + 1);
            Node lowChild = decompose(new Node(var, lowChildExpression, null, null), variableOrder, depth + 1);

//            S-reduction
            if (highChildExpression.equals(lowChildExpression)) {
                return new Node(var, decomposedNode.getExpression(), highChild, null);
            }

            return new Node(var, decomposedNode.getExpression(), highChild, lowChild);
        }

        else {
            return decompose(node, variableOrder, depth + 1);
        }
    }

    public BDD BDD_create_with_best_order(String bfunction) {
        Utils util = new Utils();
        BDDFunctions function = new BDDFunctions();
        Tester tester = new Tester();
        String permutationVars = "ABCDEFGHIJKLM";

        ArrayList<String> permutations = util.generatePermutations(permutationVars);
        BDDFunctions.BDD bestBDD = null;
        double bestSize = 1000000000;
        double average = 0;

        for (String order : permutations) {
            System.out.println(order);
            BDDFunctions.BDD currentBDD = function.BDD_create(bfunction, order);
            printNode(currentBDD.root, "", true);
            double expectedSize = ((int) Math.pow(2, currentBDD.variableOrder.length() + 1)) - 1;
            double percentage = currentBDD.size / (expectedSize/100);
            average += percentage;
            tester.correctnessTest(currentBDD ,bfunction , currentBDD.variableOrder, currentBDD.numVars);

            if (currentBDD.size < bestSize) {
                bestBDD = currentBDD;
                bestSize = currentBDD.size;
            }
        }
        System.out.println("--------------------------");
        average = average / permutationVars.length();
        System.out.println(average);
        System.out.println("--------------------------");
        printNode(bestBDD.root, "", true);
        return bestBDD;
    }

    public String BDD_use(BDD bdd, String input_values) {
        if (input_values.length() != bdd.numVars) {
            System.out.println("Invalid input values");
            return "-1";
        }

        Node current = bdd.root;
//        System.out.println(bdd.variableOrder);


        for (int i = 0; i < bdd.numVars; i++) {
            char variable = bdd.variableOrder.charAt(i);
            char value = input_values.charAt(i);
//            System.out.println(variable + " " + value);
            if (current.getExpression().contains(String.valueOf(variable)) || current.getExpression().contains(String.valueOf(variable).toLowerCase())) {
                if (value == '1') {
//                    System.out.println(variable + " 1: " + current.getExpression());
                    if (current.getExpression().equals("0") || current.getExpression().equals("1")) {
                        return current.getExpression();
                    }
                    current = current.getHighChild();
                }

                else if (value == '0') {
//                    System.out.println(variable + " 0: " + current.getExpression());

                    if (current.getExpression().equals("0") || current.getExpression().equals("1")) {
                        return current.getExpression();
                    }

                    if (current.getLowChild() == null) {
                        current = current.getHighChild();
                    }
                    else{
                        current = current.getLowChild();
                    }

                }

                else {
                    System.out.println("Invalid input value");
                    return "-1";
                }

                if (current == null) {
                    System.out.println("Invalid BDD");
                    return "-1";
                }
            }
        }

//        System.out.println("Result : " + current.getExpression());
        return current.getExpression();
    }

    public static void printNode(Node node, String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + "Variable: " + node.getVariable() + ", Expression: " + node.getExpression());

        List<Node> children = new ArrayList<>();
        children.add(node.getHighChild());
        children.add(node.getLowChild());

        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            if (child != null) {
                boolean childIsTail = i == 1 || (i == 0 && children.get(1) == null);
                printNode(child, prefix + (isTail ? "    " : "│   "), childIsTail);
            }
        }
    }

    public static void main(String[] args) {
        String bfunction = "G+efJD+Ikh+LAMC+BB+Lj+h+CEDKF+Mai+gAE+lk+cmJ+ib+G+dhfdHfe+j+AC+lb+Ik+g+mD+AkC+F+I+M+jl+hbG+e";
        String variableOrder = "AILFCDMBJHGKE";
//        String variableOrder = "GFEADIBCHJ";
        String input_values = "0101011011";

        Utils util = new Utils();
        BDDFunctions function = new BDDFunctions();


//        BDDFunctions.BDD bdd = function.BDD_create_with_best_order(bfunction);
        BDDFunctions.BDD bdd = function.BDD_create(bfunction, variableOrder);


//        System.out.println("BDD numVars : " + bdd.numVars);
//        System.out.println("BDD size : " + bdd.size);
//        System.out.println("BDD variable order : " + bdd.variableOrder);
//        printNode(bdd.root, "", true);
//        System.out.println();
//
//
//        util.evaluateExpression(bfunction, input_values, variableOrder);
//        function.BDD_use(bdd, input_values);
    }


}
