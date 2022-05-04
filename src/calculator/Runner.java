package calculator;

public class Runner {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();

//        String exp = "-12-34*(-2.36--3.64)--76^3/(32*(-54+36))+4-7";
//        String exp = "1-4*(2-3)-6^3/(2*(4+6))+4-7";
        String exp = "1+3+(4-7)*8";
//        String exp = "4-7-3";
        System.out.println(exp);
//        calculator.evaluate(exp);
        System.out.println(calculator.evaluate(exp));
    }
}
