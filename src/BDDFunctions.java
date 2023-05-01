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

    Node terminal_0 = new Node('0', "0", null, null);
    Node terminal_1 = new Node('1', "1", null, null);
    double finAverage = 0;
    double finAverageTime = 0;

    public BDD BDD_create(String bfunction, String variableOrder) {
        int numVars = variableOrder.length();

        ArrayList<ArrayList<Node>> expressionsDepth = new ArrayList<ArrayList<Node>>();
        for (int i = 0; i < numVars; i++) {
            expressionsDepth.add(new ArrayList<Node>());
        }

        Node root = decompose(new Node(' ', bfunction, null, null), variableOrder, 0, expressionsDepth);
        int expectedSize = ((int) Math.pow(2, variableOrder.length() + 1)) - 1;
        ArrayList<Node> uniqueNodes = new ArrayList<>();
        double size = countUniqueNodes(root, uniqueNodes);

        printNode(root, "", true);
        System.out.println("Variable order : " + variableOrder);
        System.out.println("Expected size : " + expectedSize);
        System.out.println("Size : " + (size));
        System.out.println("NumVars : " + numVars);

//        for (int i = 0; i < numVars; i++) {
//            System.out.print("\n" + i + " - " + variableOrder.charAt(i) + " | ");
//            for (int j = 0; j < expressionsDepth.get(i).size(); j++){
//                System.out.print(" " + expressionsDepth.get(i).get(j).getExpression());
//            }
//        }

        return new BDD(numVars, size, root, variableOrder);
    }

    private int countUniqueNodes(Node node, ArrayList<Node> nodes) {
        if (node == null) { return 0; }

        if (!nodes.contains(node)) { nodes.add(node); }
        countUniqueNodes(node.getHighChild(), nodes);
        countUniqueNodes(node.getLowChild(), nodes);

        return nodes.size();
    }

    public Node decompose(Node node, String variableOrder, int depth, ArrayList<ArrayList<Node>> expressionsDepth) {
        if (depth >= variableOrder.length()) { return node; }

        Node highChild = null;
        Node lowChild = null;

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
                }
                else {
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
                if (!part.contains("0")) { tmpLengthHigh++; }
            }

            if (tmpLengthHigh == 0) { highChildExpression = "0"; }
            else {
                int tmpLengthPartHigh = 0;
                for (String part : highChildParts) {
                    if (!part.contains("0")) {
                        tmpLengthPartHigh++;
                        if (part.replaceAll("1", "").isEmpty()) {
                            sb.append("1+");
                            flag1High = true;
                            break;
                        }
                        if (tmpLengthPartHigh == tmpLengthHigh) { sb.append(part); }
                        else { sb.append(part).append("+"); }
                    }
                }

                if (flag1High) { highChildExpression = "1"; }
                else { highChildExpression = sb.toString(); }
            }

            //LOW
            String[] lowChildParts = lowChildExpression.split("\\+");
            sb = new StringBuilder();
            int tmpLengthLow = 0;
            for (String part : lowChildParts) {
                if (!part.contains("0")) { tmpLengthLow++; }
            }

            if (tmpLengthLow == 0) { lowChildExpression = "0"; }
            else {
                int tmpLengthPartLow = 0;
                for (String part : lowChildParts) {
                    if (!part.contains("0")) {
                        tmpLengthPartLow++;
                        if (part.replaceAll("1", "").isEmpty()) {
                            sb.append("1+");
                            flag1Low = true;
                            break;
                        }

                        if (tmpLengthPartLow == tmpLengthLow) { sb.append(part); }
                        else { sb.append(part).append("+"); }
                    }
                }

                if (flag1Low) { lowChildExpression = "1"; }
                else { lowChildExpression = sb.toString(); }
            }

            //TERMINAL
            if (highChildExpression.equals("1")) { highChild = terminal_1; }
            else if (highChildExpression.equals("0")) { highChild = terminal_0; }
            else {
                highChild = decompose(new Node(var, highChildExpression, null, null), variableOrder, depth + 1, expressionsDepth);
                expressionsDepth.get(depth).add(highChild);
            }

            if (lowChildExpression.equals("1")) { lowChild = terminal_1; }
            else if (lowChildExpression.equals("0")) { lowChild = terminal_0; }
            else {
                lowChild = decompose(new Node(var, lowChildExpression, null, null), variableOrder, depth + 1, expressionsDepth);
                expressionsDepth.get(depth).add(lowChild);
            }

            //I-reduction
            for (int i = 0; i < variableOrder.length(); i++) {
                for (int j = 0; j < expressionsDepth.get(i).size(); j++){
                    if(decomposedNode.getExpression().equals(expressionsDepth.get(i).get(j).getExpression())){
                        decomposedNode = expressionsDepth.get(i).get(j);
                        return decomposedNode;
                    }
                }
            }

             //S-reduction
            if (highChildExpression.equals(lowChildExpression)) { return highChild; }

            return new Node(var, decomposedNode.getExpression(), highChild, lowChild);
        }
        else { return decompose(node, variableOrder, depth + 1, expressionsDepth); }
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
        double averageTime = 0;

        for (String order : permutations) {
            System.out.println(order);

            long startTime = System.currentTimeMillis();
            BDDFunctions.BDD currentBDD = function.BDD_create(bfunction, order);
            long endTime = System.currentTimeMillis();

            long finTime = endTime - startTime;
            printNode(currentBDD.root, "", true);
            double expectedSize = ((int) Math.pow(2, currentBDD.variableOrder.length() + 1)) - 1;
            double percentage = currentBDD.size / (expectedSize/100);
            average += percentage;
            averageTime += finTime;
            tester.correctnessTest(currentBDD ,bfunction , currentBDD.variableOrder, currentBDD.numVars);
            System.out.println("Time: " + finTime);

            if (currentBDD.size < bestSize) {
                bestBDD = currentBDD;
                bestSize = currentBDD.size;
            }
        }

        System.out.println("--------------------------");
        average = average / permutationVars.length();
        averageTime = averageTime / permutationVars.length();
        finAverage += average;
        finAverageTime += averageTime;
        System.out.println(100 - average);
        System.out.println("Average time: " + averageTime);
        System.out.println("--------------------------");
        System.out.println("Variable order: " + bestBDD.variableOrder);
        System.out.println("Size: " + bestBDD.size);
        printNode(bestBDD.root, "", true);
        System.out.println("Average of all permutations: " + (100 - (finAverage / permutationVars.length())));
        System.out.println("Average time of all permutations: " + (finAverageTime / permutationVars.length()));
        System.out.println();
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
                    if (current.getExpression().equals("0") || current.getExpression().equals("1")) { return current.getExpression(); }
                    current = current.getHighChild();
                }

                else if (value == '0') {
//                    System.out.println(variable + " 0: " + current.getExpression());
                    if (current.getExpression().equals("0") || current.getExpression().equals("1")) { return current.getExpression(); }
                    if (current.getLowChild() == null) { current = current.getHighChild(); }
                    else{ current = current.getLowChild(); }
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
        System.out.println(prefix + (isTail ? "└── " : "├── ") + "[" + node + "] Variable: " + node.getVariable() + ", Expression: " + node.getExpression());

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
        //PETO FUNKCIA
//        String bfunction = "AbcDef+AgHBC+aBc+abC+IEJKL+M";
//        String variableOrder = "BCDEFGHIJKLMA";
//        String input_values = "0000000000000";

//        String bfunction = "L+C+BHg+e+aFJ+kDmIMhcl+f+i+Kb+ED+J+gA";
//        String variableOrder = "JCMDELHFKBAGI";
//        String input_values = "0000000000000";

        //ADO FUNKCIA
//        String bfunction = "FLIejB+CDgKm+H+aCjE+bmdK+F+L+GI+Haca+Dkg+JhM+BL+ieF";
//        String variableOrder = "EGJDIKBAHFMCL";
//        String input_values = "0000000000000";

//
//        String bfunction = "abC+ABc+Abc+abc+ABC+aBc";
//        String variableOrder = "ABC";
//        String input_values = "000";


//        String bfunction = "KICEIhGB+mBDCH+EhJLC+aDlm+B+KMCD+CIlD+gIh+LgC+jk+HdBKf";
//        String variableOrder = "HFGLIMBAJKECD";
//        String input_values = "0000000000000";

        //MARTIN FUNKCIA
        String bfunction = "ABCdef+aBC+eFABc+FBeCCA+BF+CAF+BFea";
        String variableOrder = "ABCDEF";
        String input_values = "000000";

        Utils util = new Utils();
        BDDFunctions function = new BDDFunctions();


//        BDDFunctions.BDD bdd = function.BDD_create_with_best_order(bfunction);
        BDDFunctions.BDD bdd = function.BDD_create(bfunction, variableOrder);


//        System.out.println("BDD numVars : " + bdd.numVars);
//        System.out.println("BDD size : " + bdd.size);
//        System.out.println("BDD variable order : " + bdd.variableOrder);
//        printNode(bdd.root, "", true);
//        System.out.println();

        System.out.println();
        System.out.println(util.evaluateExpression(bfunction, input_values, variableOrder));
        System.out.println(function.BDD_use(bdd, input_values));
    }


}
