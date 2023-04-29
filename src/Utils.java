import java.util.*;

public class Utils {
    public ArrayList<String> dnfGenerator(int numVars) {
        Random rand = new Random();
        ArrayList<String> expressions = new ArrayList<String>();
        String[] variables = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"};
        for (int i = 0; i < 100; i++) {
            StringBuilder expression = new StringBuilder();
            int numClauses = rand.nextInt(numVars) + 1; // Choose a random number of clauses
            for (int j = 0; j < numClauses; j++) {
                StringBuilder clause = new StringBuilder();
                boolean[] usedVars = new boolean[numVars];
                int numLiterals = numVars;

                for (int k = 0; k < numLiterals; k++) {
                    int varIndex = rand.nextInt(numVars);
                    while (usedVars[varIndex]) {
                        varIndex = rand.nextInt(numVars);
                    }
                    usedVars[varIndex] = true;

                    String var = variables[varIndex];
                    boolean negated = rand.nextBoolean();
                    clause.append(negated ? var.toLowerCase() : var);

                    if (k != numLiterals - 1) {
                        clause.append(rand.nextBoolean() ? "" : "+"); // Use "+" for OR
                    }
                }

                expression.append(clause.toString());
            }

            expressions.add(expression.toString());
//            System.out.println(expression.toString());

        }
        return expressions;
    }

    public String evaluateExpression(String expression, String evaluation, String variableOrder) {
//        System.out.println("Expression : " + expression);
        // Convert the evaluation string to a boolean array
        boolean[] values = new boolean[evaluation.length()];
        for (int i = 0; i < evaluation.length(); i++) {
            values[i] = evaluation.charAt(i) == '1';
        }

        // Replace the variable names in the expression with their corresponding values
        for (int i = 0; i < variableOrder.length(); i++) {
            char variable = variableOrder.charAt(i);
//            System.out.print(variable);
            int index = i;
//            System.out.println(index);
            String value = values[index] ? "1" : "0";
            String negatedValue = values[index] ? "0" : "1";
            expression = expression.replaceAll(variable + "", value);
//            System.out.println(expression);
            expression = expression.replaceAll((variable + "").toLowerCase(), negatedValue);

        }

        // Split the expression into individual terms
        String[] terms = expression.split("\\+");

        // Convert each term to a boolean value
        values = new boolean[terms.length];
        for (int i = 0; i < terms.length; i++) {
            // Determine the length of the binary number
            int numDigits = terms[i].length();

            // Convert the binary number to a boolean value
            boolean value = true;
            for (int j = 0; j < numDigits; j++) {
                value = value && (terms[i].charAt(j) == '1');
            }
            values[i] = value;
        }

        // OR
        boolean result = false;
        for (int i = 0; i < values.length; i++) {
            result = result || values[i];
        }

//        System.out.println("Rewritten expression: " + expression);
//        System.out.println("Evaluation: " + evaluation);
//        System.out.println("Expected result1: " + (result ? "1" : "0"));

        return (result ? "1" : "0");
    }

    public List<String> inputValuesGenerator(int numVars) {
        List<String> combinations = new ArrayList<>();
        generateCombinations(numVars, "", combinations);
        return combinations;
    }

    private static void generateCombinations(int numVars, String currentCombination, List<String> combinations) {
        if (currentCombination.length() == numVars) {
            combinations.add(currentCombination);
            return;
        }
        generateCombinations(numVars, currentCombination + "0", combinations);
        generateCombinations(numVars, currentCombination + "1", combinations);
    }


    public ArrayList<String> generatePermutations(String variableOrder) {
        List<Character> chars = new ArrayList<>();
        for (char c : variableOrder.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars);
        ArrayList<String> permutations = new ArrayList<>();
        for (int i = 0; i < variableOrder.length(); i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < variableOrder.length(); j++) {
                sb.append(chars.get((i + j) % variableOrder.length()));
            }
            permutations.add(sb.toString());
        }
        return permutations;
    }

    public static void main(String[] args) {
        Utils util = new Utils();
//        util.dnfGenerator();
//
        String bfunction = "aJe+CBa+hDCgfIA";
//        String variableOrder = "FGADEIBCHJ";
        String variableOrder = "GFEADIBCHJ";
        String input_values = "0101011011";
        int numVars = 13;
        System.out.println(bfunction);
//        util.evaluateExpression(bfunction, input_values, variableOrder);
//        util.generatePermutations("ABCDEFGHIJKLM");

//        System.out.println(util.inputValuesGenerator(numVars).size());
    }


}