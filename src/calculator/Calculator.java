package calculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Program to calculate user expression
 *
 * @author Kyle
 * @version 1.0
 */
public class Calculator implements ICalculator {
    /**
     * Evaluates user inputted expression
     *
     * @param exp           User expression
     * @return              Returns answer
     */
    public double evaluate(String exp) {
        // Checks and removes whitespace
        if (exp.contains(" "))
            exp = exp.replaceAll(" ","");

        // Set-up to normalise the expression inputted
        String[] arr = exp.split("");
        ArrayList<String> normalised = new ArrayList<>();
        normaliseInput(arr, normalised, "", 0, false);

        // Set-up to create the reverse polish notation
        Stack<String> stack = new Stack<>();
        ArrayList<String> postFix = new ArrayList<>();
        rpn(normalised, stack, postFix, 0);

        // Set-up to calculate RPN expression
        Stack<Double> calcStack = new Stack<>();
        return calculate(0, postFix, calcStack, 0);
    }

    /**
     * Normalises a user input into a format that can be used by the rest
     * of the program
     *
     * @param arr           An array of the user input e.g. 1+23 -> [1,+,2,3]
     * @param expArr        Destination parameter, takes in the normalised input
     * @param temp          Holding parameter
     * @param i             Iterator
     * @param negCheck      Check to ensure negative numbers are properly parsed
     */
    public void normaliseInput(String[] arr, ArrayList<String> expArr, String temp, int i, boolean negCheck) {
        if (i < arr.length) {
            if (isNumeric(arr[i]) || arr[i].equals(".")) {
                temp += arr[i];
                negCheck = false;
            } else if (negCheck || (i == 0 && arr[i].equals("-")) || i == 1 && arr[i-1].equals("-")) { // Negative handling
                if (arr[i].equals("-")) {
                    if (isNumeric(arr[i + 1]))
                        temp += arr[i];
                    else
                        temp = addTempToArray(arr, temp, expArr, i);
                } else if (arr[i].equals("(") && arr[i-1].equals("-")) {
                    if (i == 1) {
                        expArr.remove(0);
                        expArr.add(0, "-1");
                        expArr.add(1, "*");
                    } else if (i >= 3 && arr[i-1].equals("-")) {
                        // Deals with num op - (
                        String op = expArr.get(expArr.size() - 2);
                        if (!op.equals(")")) {
                            switch (op) {
                                case "+" -> expArr.remove(expArr.size() - 2); // + -  -> -
                                case "-" -> { // - - -> +
                                    expArr.remove(expArr.size() - 1);
                                    expArr.remove(expArr.size() - 1);
                                    expArr.add(expArr.size(), "+");
                                }
                                case "*", "/" -> {
                                    String s = expArr.get(expArr.size() - 1)+expArr.get(expArr.size() - 3);
                                    expArr.remove(expArr.size() - 1);
                                    expArr.remove(expArr.size() - 2);
                                    expArr.add(expArr.size() - 1, s);
                                }
                                default -> throw new IllegalStateException("Unexpected value: " + op + " at position " + i);
                            }
                        }
                    }
                    temp = addTempToArray(arr, temp, expArr, i);
                } else {
                    temp = addTempToArray(arr, temp, expArr, i);
                }
            } else {
                negCheck = !arr[i].equals(")"); // Avoids ..)-6 becoming [...,),-6] and instead is [...,),-,6]
                temp = addTempToArray(arr, temp, expArr, i);
            }
            normaliseInput(arr, expArr, temp, ++i, negCheck);
        } else if (temp.length() != 0) { // For adding numbers if they are the last in the expression
            expArr.add(temp);
        }
    }

    /**
     * Converts normalised expression into reverse polish notation e.g. 1+2 ->  1 2 +
     *
     * @param exp           Normalised expression
     * @param stack         Holding parameter. Takes in each value in the expression for parsing
     * @param postFix       Final array in RPN format
     * @param i             Iterator
     */
    public void rpn(ArrayList<String> exp, Stack<String> stack, ArrayList<String> postFix, int i) {
        if (i < exp.size()) { // TO-DO COMMENT
            String str = exp.get(i);
            // Check if string is an operator
            if (precedence(str) > 0) {
                precedenceCheck(str, postFix, stack);
                stack.push(str);
            } else if (str.equals(")")) { // TO-DO COMMENT
                String temp = stack.pop();
                bracketCheck(temp, postFix, stack);
            } else if (str.equals("(")) { // TO-DO COMMENT
                stack.push(str);
            } else {
                // str is neither not an operator, hence it will be a number
                postFix.add(str);
            }
            rpn(exp, stack, postFix, ++i);
        }

        // Adds remaining operators in the stack
        if (!stack.isEmpty())
            postFix.add(stack.pop());
    }

    /**
     * Calculates the RPN expression
     *
     * @param answer        Incremental answer value. Will eventually contain the final answer
     * @param exp           RPN expression
     * @param stack         Holding parameter. Takes in a value from the array
     * @param i             Iterator
     * @return              Returns answer
     */
    public double calculate(double answer, ArrayList<String> exp, Stack<Double> stack, int i) {
        // TO-DO COMMENT
        if (i < exp.size()) {
            if (!exp.get(i).equals("+") // TO-DO COMMENT
                    && !exp.get(i).equals("-")
                    && !exp.get(i).equals("*")
                    && !exp.get(i).equals("/")
                    && !exp.get(i).equals("^")) {
                if (!isNumeric(exp.get(i)))
                    throw new RuntimeException("Extra opening bracket");
                stack.push(Double.parseDouble(exp.get(i)));
            } else { // TO-DO COMMENT
                if (stack.size() == 1)
                    throw new RuntimeException("Extra operator somewhere");
                double y = stack.pop();
                double x = stack.pop();
                switch (exp.get(i)) { // TO-DO COMMENT
                    case "+" -> answer = add(x, y);
                    case "-" -> answer = subtract(x, y);
                    case "*" -> answer = multiply(x, y);
                    case "/" -> answer = divide(x, y);
                    case "^" -> answer = y < 0
                            ? pow(x, multiply(-1, y), x, true)
                            : pow(x, y, x, false);
                    default -> throw new IllegalStateException("Unexpected value: " + exp.get(i));
                }
                stack.push(answer);
            }
            return calculate(answer, exp, stack, ++i);
        }
        return answer;
    }

    /**
     * Takes in two parameters and adds them
     *
     * @param x     First value
     * @param y     Second Value
     * @return      Returns y + x
     */
    public double add(double x, double y) {
        return x + y;
    }

    /**
     * Takes in two parameters and subtracts them
     *
     * @param x     First value
     * @param y     Second Value
     * @return      Returns x - y
     */
    public double subtract(double x, double y) {
        return x - y;
    }

    /**
     * Takes in two parameters and multiplies them
     *
     * @param x     First value
     * @param y     Second Value
     * @return      Returns x * y
     */
    public double multiply(double x, double y) {
        return x * y;
    }

    /**
     * Takes in two parameters and divides them
     *
     * @param x     First value
     * @param y     Second Value
     * @return      Returns x / y
     */
    public double divide(double x, double y) {
        if (y == 0)
            throw new ArithmeticException("Division by 0");
        return x / y;
    }

    /**
     * Takes in two parameters and performs an exponent calculation
     *
     * @param x     First value
     * @param y     Second Value (iterator)
     * @return      Returns x ^ y
     */
    public double pow(double x, double y, double base, boolean negCheck) {
        boolean xNegative = false;
        if (y % 1 == 0) { // Integer exponents
            if (y > 1) {
                x = multiply(x, base);
                return pow(x, --y, base, negCheck);
            }
            if (y == 0) // x^0
                x = 1;
        } else {
            // Extracts each element from the fraction[] array
            long[] fraction = decimalToFraction(y);
            long whole = fraction[0];
            long numerator = fraction[1];
            int denominator = (int) fraction[2];

            // Ensures that calculations use abs values
            if (x < 0) {
                x = multiply(x, -1);
                xNegative = true;
            }

            /*
             If the decimal/fraction was 2.625
             2.625 -> 21/8 -> 2 5/8
             Therefore the calculation is as follows
             (x^2) * (x^(5/8))
            */
            double root = sqrtA(x, 1.0, denominator, 1);
            double fractionalExponent = pow(root, numerator, root, false);
            double exponent = pow(x, whole, x, false);

            x = multiply(exponent, fractionalExponent);

            // Converts the answer back to a negative if began as one
            if (xNegative)
                x = multiply(x, -1);
        }

        // To calculate negative exponents take the abs value of the exponent then do 1/x^y
        return negCheck
                ? divide(1, x)
                : x;
    }

    /**
     * Converts a decimal to fraction
     *
     * @param decimal       Inputs a decimal
     * @return              The fraction as an array splitting the whole, numerator & denominator
     */
    public long[] decimalToFraction(double decimal) {
        long whole = 0;
        long numerator;
        long denominator;
        long gCommonFactor;

        /*
         Converts the decimal to BigDecimal
         Has to initially be converted to string to maintain parity
         Otherwise BigDecimal can convert 2.1 to 2.100000000000000088817841970012523233890533447265625
         Strip trailing zeros removes any extra zeros, and scale returns the number of decimal places
        */
        long decimalCheck = new BigDecimal(String.valueOf(decimal)).stripTrailingZeros().scale();
        long fractionTimes = (long) pow(10, decimalCheck, 10, false);

        numerator = (long) multiply(decimal, fractionTimes);
        denominator = (long) multiply(1, fractionTimes);
        gCommonFactor = gcf(numerator, denominator, 1, 0);

        numerator = (long) divide(numerator, gCommonFactor);
        denominator = (long) divide(denominator, gCommonFactor);

        if (numerator > denominator) {
            whole = (long) divide(numerator, denominator);
            numerator = (long) subtract(numerator, multiply(denominator, whole));
        }
        return new long[]{whole, numerator, denominator};
    }

    /**
     * Finds the root of a number
     *
     * @param radicand      Number whose root is being found
     * @param root          Holding parameter, stores root values
     * @param pow           The exponent of the root
     * @param i             Iterator
     * @return              Root of the radicand
     */
    public double sqrtA(double radicand, double root, int pow, int i) {
        if (i <= pow+1) {
            root = bisection(1, i, pow, 0);

            if (root == radicand)
                return i;

            return root > radicand
                    ? sqrtB(radicand, i - 1, i, pow)
                    : sqrtA(radicand, root, pow, ++i);
        }
        return root;
    }

    /**
     * Add the temporary holding value to the array, then clears it
     *
     * @param arr       Array which contains values
     * @param temp      Holding value
     * @param expArr    Array which the temp value is added to (and the arr[i] value if temp is empty)
     * @param i         Iterator
     * @return          Returns a blank string to clear the temp string
     */
    private String addTempToArray (String[] arr, String temp, ArrayList<String> expArr, int i) {
        if (!temp.isEmpty())
            expArr.add(temp);
        expArr.add(arr[i]);
        return "";
    }

    /**
     * Determines whether a string is a number of type double
     *
     * @param str       Parameter of type String. Will contain an 'individual value' i.e. "a", "2", "3.4"
     * @return          Returns a boolean if str is numeric
     */
    public boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * Defines the precedence of an operator. BODMAS
     *
     * @param str       Parameter of type String. Contains an operator
     * @return          Return a value based on the precedence of the passed in operator
     */
    private int precedence(String str) {
        return switch (str) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> -1;
        };
    }

    /**
     * Check if operator has a greater precedence than the top of the stack.
     * If it doesn't, add it to the output
     *
     * @param operator      String containing an operator i.e. + - / * ^
     * @param output        Value of the final array
     * @param stack         Contain the operators for precedence checking
     */
    private void precedenceCheck(String operator, ArrayList<String> output, Stack<String> stack) {
        if (!stack.isEmpty() && precedence(stack.peek()) >= precedence(operator)) {
            output.add(stack.pop());
            precedenceCheck(operator, output, stack);
        }
    }

    /**
     * Check if the operator is not an open bracket.
     * If true, add the operator to the output and set the
     * next operator value to the next element in the stack
     *
     * @param operator      String containing an operator i.e. + - / * ^
     * @param output        Value of the final array
     * @param stack         Contain the operators for precedence checking
     */
    private void bracketCheck(String operator, ArrayList<String> output, Stack<String> stack) {
        if (!operator.equals("(")) {
            output.add(operator);
            if (stack.empty())
                throw new RuntimeException("Extra closing bracket");
            operator = stack.pop();
            bracketCheck(operator, output, stack);
        }
    }

    /**
     * Calculates the greatest common factor
     *
     * @param x                 First value
     * @param y                 Second value
     * @param i                 Iterator & the common factor
     * @param gCommonFactor     Holds the common factors, will eventually hold the greatest common factor
     * @return                  The greatest common factor
     */
    private long gcf(long x, long y, long i, long gCommonFactor) {
        if (i > 1000) return gCommonFactor; // Ensure that it doesn't infinitely loop for fractions that do not have a common factor
        if (i <= x && i <= y) {
            if (x % i == 0 && y % i == 0)
                gCommonFactor = i;
            return gcf(x, y, ++i, gCommonFactor);
        }
        return gCommonFactor;
    }

    /**
     * Finds the root of a number that is a non-perfect square
     *
     * @param radicand      Number whose root is being found
     * @param low           Low estimate
     * @param high          High estimate
     * @param pow           The exponent of the root
     * @return              Closest root to the radicand
     */
    private double sqrtB(double radicand, double low, double high, int pow) {
        double root = 1;
        double mid = (low + high) / 2;

        root = bisection(root, mid, pow, 0);

        if (Math.abs(radicand - root) < 0.00001)
            return mid;

        if (root == 0)
            throw new RuntimeException("Root error. Root became 0");
        if (radicand < 0)
            throw new RuntimeException("Imaginary number");

        return root < radicand
                ? sqrtB(radicand, mid, high, pow)
                : sqrtB(radicand, low, mid, pow);
    }

    /**
     * Performs the bisection method.
     * Finds the solution by halving the search area each iteration
     *
     * @param root      Holding parameter, stores root values
     * @param mid       Middle of the low and high values
     * @param pow       The exponent of the root
     * @param i         Iterator
     * @return          Value of the bisection
     */
    private double bisection(double root, double mid, double pow, int i) {
        return i < pow
                ? bisection(root * mid, mid, pow, ++i)
                : root;
    }
}