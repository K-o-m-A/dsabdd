import java.util.ArrayList;
import java.util.List;

public class Tester {
    public void correctnessTest(BDDFunctions.BDD bdd, String bfunction, String variableOrder, int numVars) {
        Utils util = new Utils();
        BDDFunctions function = new BDDFunctions();
        List<String> input_values = util.inputValuesGenerator(numVars);
        boolean err = false;

        for (int i = 0; i < input_values.size(); i++) {
//            System.out.println(input_values.get(i));
            String result1 = util.evaluateExpression(bfunction, input_values.get(i), variableOrder);
            String result2 = function.BDD_use(bdd, input_values.get(i));
            if (!result1.equals(result2)){
                err = true;
                System.out.println("Error on : " +  input_values.get(i));
                System.out.println("Expected result : " + result1);
                System.out.println("BDD_use result : " + result2);
                System.exit(1);
                break;
            }
        }
        System.out.println("[OK]");
    }

    public void allExpressionsTest(){
        Utils util = new Utils();
        BDDFunctions function = new BDDFunctions();
        ArrayList<String> expressions = util.dnfGenerator(13);
        for (int i = 0; i < expressions.size(); i++) {
            BDDFunctions.BDD bdd = function.BDD_create_with_best_order(expressions.get(i));
        }
        System.out.println("ALL EXPRESSIONS - [OK]");
    }

    public static void main(String[] args) {
        Tester tester = new Tester();
        Utils util = new Utils();
        BDDFunctions function = new BDDFunctions();

        int numVars = 13;
        String bfunction = "m+FB+JhA+L+e+cKD+G+ILCi+d+K+f+Ba+E+J+mH+G";
        String variableOrder = "JBDGIECMAFHLK";

//        BDDFunctions.BDD bdd = function.BDD_create(bfunction, variableOrder);
//        tester.correctnessTest(bdd, bfunction, variableOrder, numVars);

        tester.allExpressionsTest();
    }


}
