package calculator;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestCalculator {

    Calculator calculator;
    Stack<String> stack;
    ArrayList<String> output;

    @BeforeEach
    void setup() {
        calculator = new Calculator();
        stack = new Stack<>();
        output = new ArrayList<>();
    }

    @ParameterizedTest(name = "Testing normalisation {index}: {0}")
    @MethodSource("equations")
    void test_normalisation(String str, String[] checkNormal) {
        calculator.normaliseInput(str.split(""), output, "", 0, false);
        assertEquals(output.toString(), Arrays.toString(checkNormal));
    }

    @ParameterizedTest(name = "Testing RPN {index}: {0}")
    @MethodSource("equations")
    void test_rpn(String str, String[] checkNormal, String[] checkRPN) {
        ArrayList<String> testArr = new ArrayList<>(Arrays.asList(checkNormal));
        calculator.rpn(testArr, stack, output, 0);
        assertEquals(output.toString(), Arrays.toString(checkRPN));
    }

    @ParameterizedTest(name = "Testing addition {index}: {0} + {1}")
    @MethodSource("numbers")
    void test_addition(double x, double y) {
        assertEquals(calculator.add(x, y), x + y);
    }

    @ParameterizedTest(name = "Testing subtraction {index}: {0} - {1}")
    @MethodSource("numbers")
    void test_subtract(double x, double y) {
        assertEquals(calculator.subtract(x, y), x - y);
    }

    @ParameterizedTest(name = "Testing multiplication {index}: {0} * {1}")
    @MethodSource("numbers")
    void test_multiply(double x, double y) {
        assertEquals(calculator.multiply(x, y), x * y);
    }

    @ParameterizedTest(name = "Testing division {index}: {0} / {1}")
    @MethodSource("numbers")
    void test_divide(double x, double y) {
        assertEquals(calculator.divide(x, y), x / y);
    }

    @ParameterizedTest(name = "Testing sqrt {index}: {0} root {1}")
    @MethodSource("numbers")
    void test_sqrt(double x, double y, double sqrtCheck) {
        assertEquals(calculator.sqrtA(x, 1, (int) y, 1), sqrtCheck, 0.05);
    }

    @ParameterizedTest(name = "Testing pow {index}: {0} ^ {1}")
    @MethodSource("numbers")
    void test_pow(double x, double y, double sqrtCheck, double powCheck) {
        boolean b = false;
        if (y < 0) {
            b = true;
            y = y * -1;
        }

        assertEquals(calculator.pow(x, y, x, b), powCheck, 0.05);
    }

    @ParameterizedTest(name = "Testing is number: {0}")
    @MethodSource("misc")
    void test_isNumeric(String test, boolean check) {
        boolean b = calculator.isNumeric(test);
        assertEquals(b, check);
    }

    @ParameterizedTest(name = "Testing is decimal: {0}")
    @MethodSource("decimals")
    void test_decimalToFraction(double decimal, long whole, long numerator, long denominator) {
        long[] fraction = calculator.decimalToFraction(decimal);
        long testWhole = fraction[0];
        long testNumerator = fraction[1];
        int testDenominator = (int) fraction[2];
        assertEquals(testWhole, whole);
        assertEquals(testNumerator, numerator);
        assertEquals(testDenominator, denominator);
    }

    @ParameterizedTest(name = "Testing is Evaluate: {0}")
    @MethodSource("equations")
    void test_evaluate(String exp, String[] checkNormal, String[] checkRPN, double checkAns) {
        double answer = calculator.evaluate(exp);

        BigDecimal a = BigDecimal.valueOf(answer);
        BigDecimal b = BigDecimal.valueOf(checkAns);
        MathContext m = new MathContext(6);
        double ans = a.round(m).doubleValue();
        double check = b.round(m).doubleValue();

        assertEquals(ans, check);
    }

    static Stream<Arguments> equations() {
        return Stream.of(
                Arguments.of("2^-5.675", new String[]{"2","^","-5.675"}, new String[]{"2", "-5.675", "^"}, 0.01957288185),
                Arguments.of("-(5*4)+5+-6", new String[]{"-1","*","(","5","*","4",")","+","5","+","-6"}, new String[]{"-1","5","4","*","*","5","+","-6","+"}, -21),
                Arguments.of("5+-(7-3)+67", new String[]{"5","-","(","7","-","3",")","+","67"}, new String[]{"5","7","3","-","-","67","+"}, 68),
                Arguments.of("5*-(7-3)+67", new String[]{"-5","*","(","7","-","3",")","+","67"}, new String[]{"-5","7","3","-","*","67","+"}, 47),
                Arguments.of("-12-34*-(-2.36--3.64)--76-3/(32*(-54+36))+4^7",
                        new String[]{"-12","-","-34","*","(","-2.36","-","-3.64",")"
                                ,"-","-76","-","3","/","(","32","*","(","-54","+","36",")",")","+","4","^","7"},
                        new String[]{"-12","-34","-2.36","-3.64","-","*","-","-76","-",
                                "3","32","-54","36","+","*","/","-","4","7","^","+"}, 16491.5252083)

        );
    }

    static Stream<Arguments> numbers() {
        return Stream.of(
                Arguments.of(2, 5, 1.148698354997035, 32),
                Arguments.of(29, 10, 1.400360345840454, 4.20707233300201E14),
                Arguments.of(30, -78, 0.9573319383517154, 6.088946131066839E-116),
                Arguments.of(3.67, 7.5, 1.1769558135365725, 17178.916827722234),
                Arguments.of(3.5, -7.5, 1.0, 0.00008307869)
        );
    }

    static Stream<Arguments> misc() {
        return Stream.of(
                Arguments.of("2", true),
                Arguments.of("29", true),
                Arguments.of("/", false),
                Arguments.of("3.67", true),
                Arguments.of("(", false)
        );
    }

    static Stream<Arguments> decimals() {
        return Stream.of(
                Arguments.of(2.3, 2, 3, 10),
                Arguments.of(3.6, 3, 3, 5),
                Arguments.of(7.88, 7, 22, 25),
                Arguments.of(3.67, 3, 67, 100),
                Arguments.of(0.5, 0, 1, 2)
        );
    }

    // Normalise Inputs
    // Single Digit
    @Test
    void test_normalise_input_single_digit_with_basic_operators() {
        String exp = "1+3/4-7*8";
        String[] test = exp.split("");
        String[] check = {"1","+","3","/","4","-","7","*","8"};

        calculator.normaliseInput(test, output, "", 0, false);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_normalise_input_single_digit_with_decimals() {
        String exp = "1+3.4/4-7.8*8";
        String[] test = exp.split("");
        String[] check = {"1","+","3.4","/","4","-","7.8","*","8"};

        calculator.normaliseInput(test, output, "", 0, false);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_normalise_input_single_digit_with_brackets() {
        String exp = "1+(3/4)-7*8";
        String[] test = exp.split("");
        String[] check = {"1","+","(","3","/","4",")","-","7","*","8"};

        calculator.normaliseInput(test, output, "", 0, false);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_normalise_input_single_digit_with_negative_numbers() {
        String exp = "1+-3/4--7*8";
        String[] test = exp.split("");
        String[] check = {"1","+","-3","/","4","-","-7","*","8"};

        calculator.normaliseInput(test, output, "", 0, false);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_normalise_input_single_digit_with_brackets_and_negative_numbers() {
        String exp = "1+(-3/4)--7*8";
        String[] test = exp.split("");
        String[] check = {"1","+","(","-3","/","4",")","-","-7","*","8"};

        calculator.normaliseInput(test, output, "", 0, false);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_normalise_input_single_digit_complex_input() {
        String exp = "-1+(-3.7/4)-(-7^-8)";
        String[] test = exp.split("");
        String[] check = {"-1","+","(","-3.7","/","4",")","-","(","-7","^","-8",")"};

        calculator.normaliseInput(test, output, "", 0, false);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    // Multi Digit
    @Test
    void test_normalise_input_multi_digit_with_basic_operators() {
        String exp = "12+34/41-72*80";
        String[] test = exp.split("");
        String[] check = {"12","+","34","/","41","-","72","*","80"};

        calculator.normaliseInput(test, output, "", 0, false);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_normalise_input_multi_digit_with_decimals() {
        String exp = "12.36+34.45/41-72.2*80";
        String[] test = exp.split("");
        String[] check = {"12.36","+","34.45","/","41","-","72.2","*","80"};

        calculator.normaliseInput(test, output, "", 0, false);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_normalise_input_multi_digit_with_brackets() {
        String exp = "12+(34/(41-72))*80";
        String[] test = exp.split("");
        String[] check = {"12","+","(","34","/","(","41","-","72",")",")","*","80"};

        calculator.normaliseInput(test, output, "", 0, false);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_normalise_input_multi_digit_with_negative_numbers() {
        String exp = "12+-34/41--72*-80";
        String[] test = exp.split("");
        String[] check = {"12","+","-34","/","41","-","-72","*","-80"};

        calculator.normaliseInput(test, output, "", 0, false);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_normalise_input_multi_digit_with_brackets_and_negative_numbers() {
        String exp = "12+(-34/(-41-72))*-80";
        String[] test = exp.split("");
        String[] check = {"12","+","(","-34","/","(","-41","-","72",")",")","*","-80"};

        calculator.normaliseInput(test, output, "", 0, false);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_normalise_input_multi_digit_complex_input() {
        String exp = "-12-34*(-2.36--3.64)--76^3/(32*(-54+36))+4-7";
        String[] test = exp.split("");
        String[] check = {"-12", "-", "34", "*", "(", "-2.36", "-", "-3.64", ")", "-", "-76", "^", "3", "/", "("
                , "32", "*", "(", "-54", "+", "36", ")", ")", "+", "4", "-", "7"};

        calculator.normaliseInput(test, output, "", 0, false);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    // Reverse Polish Notation
    // Single Digits
    @Test
    void test_rpn_single_digit_with_basic_operators() {
        String[] test = {"1","+","3","/","4","-","7","*","8"};
        ArrayList<String> testArr = new ArrayList<String>(Arrays.asList(test));
        String[] check = {"1","3","4","/","+","7","8","*","-"};

        calculator.rpn(testArr, stack, output, 0);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_rpn_single_digit_with_decimals() {
        String[] test = {"1","+","3.4","/","4.6","-","7","*","8"};
        ArrayList<String> testArr = new ArrayList<String>(Arrays.asList(test));
        String[] check = {"1","3.4","4.6","/","+","7","8","*","-"};

        calculator.rpn(testArr, stack, output, 0);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_rpn_single_digit_with_brackets() {
        String[] test = {"1","+","(","3","/","4","-","7",")","*","8"};
        ArrayList<String> testArr = new ArrayList<String>(Arrays.asList(test));
        String[] check = {"1","3","4","/","7","-","8","*","+"};

        calculator.rpn(testArr, stack, output, 0);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_rpn_single_digit_with_negative_numbers() {
        String[] test = {"1","+","-3","/","4","-","-7","*","8"};
        ArrayList<String> testArr = new ArrayList<String>(Arrays.asList(test));
        String[] check = {"1","-3","4","/","+","-7","8","*","-"};

        calculator.rpn(testArr, stack, output, 0);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_rpn_single_digit_with_brackets_and_negatives_numbers() {
        String[] test = {"1","+","(","-3","/","4","-","-7",")","*","-8"};
        ArrayList<String> testArr = new ArrayList<String>(Arrays.asList(test));
        String[] check = {"1","-3","4","/","-7","-","-8","*","+"};

        calculator.rpn(testArr, stack, output, 0);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_rpn_single_digit_complex_input() {
        String[] test = {"-1", "-", "3", "*", "(", "-2", "-", "-3", ")", "-", "-7", "^", "3", "/", "("
                , "3", "*", "(", "-5", "+", "3", ")", ")", "+", "4", "-", "7"};
        ArrayList<String> testArr = new ArrayList<String>(Arrays.asList(test));
        String[] check = {"-1", "3", "-2", "-3", "-", "*", "-", "-7", "3", "^"
                ,"3", "-5", "3", "+", "*", "/","-", "4", "+", "7", "-"};

        calculator.rpn(testArr, stack, output, 0);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    // Multi Digit
    @Test
    void test_rpn_multi_digit_with_basic_operators() {
        String[] test = {"12","+","34","/","45","-","76","*","80"};
        ArrayList<String> testArr = new ArrayList<String>(Arrays.asList(test));
        String[] check = {"12","34","45","/","+","76","80","*","-"};

        calculator.rpn(testArr, stack, output, 0);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_rpn_multi_digit_with_decimals() {
        String[] test = {"12.34","+","34.36","/","45","-","76","*","80"};
        ArrayList<String> testArr = new ArrayList<String>(Arrays.asList(test));
        String[] check = {"12.34","34.36","45","/","+","76","80","*","-"};

        calculator.rpn(testArr, stack, output, 0);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_rpn_multi_digit_with_brackets() {
        String[] test = {"12","+","(","34","/","45","-","76",")","*","80"};
        ArrayList<String> testArr = new ArrayList<String>(Arrays.asList(test));
        String[] check = {"12","34","45","/","76","-","80","*","+"};

        calculator.rpn(testArr, stack, output, 0);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_rpn_multi_digit_with_negative_numbers() {
        String[] test = {"12","+","-34","/","45","-","-76","*","80"};
        ArrayList<String> testArr = new ArrayList<String>(Arrays.asList(test));
        String[] check = {"12","-34","45","/","+","-76","80","*","-"};

        calculator.rpn(testArr, stack, output, 0);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_rpn_multi_digit_with_brackets_and_negatives_numbers() {
        String[] test = {"12","+","(","-34","/","-45","-","76",")","-","-80"};
        ArrayList<String> testArr = new ArrayList<String>(Arrays.asList(test));
        String[] check = {"12","-34","-45","/","76","-","+","-80","-"};

        calculator.rpn(testArr, stack, output, 0);
        assertEquals(output.toString(), Arrays.toString(check));
    }

    @Test
    void test_rpn_multi_digit_complex_input() {
        String[] test = {"-12", "-", "34", "*", "(", "-2.36", "-", "-3.64", ")", "-", "-76", "^", "3", "/", "("
                , "32", "*", "(", "-54", "+", "36", ")", ")", "+", "4", "-", "7"};
        ArrayList<String> testArr = new ArrayList<String>(Arrays.asList(test));
        String[] check = {"-12", "34", "-2.36", "-3.64", "-", "*", "-", "-76", "3", "^"
                ,"32", "-54", "36", "+", "*", "/","-", "4", "+", "7", "-"};

        calculator.rpn(testArr, stack, output, 0);
        assertEquals(output.toString(), Arrays.toString(check));
    }
}
