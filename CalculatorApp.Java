
public class Calculator {

    /** Adds two integers. */
    public int add(int a, int b) {
        return a + b;
    }

    /** Adds two double values (overloaded method). */
    public double add(double a, double b) {
        return a + b;
    }

    /** Adds three integers (overloaded method). */
    public int add(int a, int b, int c) {
        return a + b + c;
    }

    /** Subtracts the second integer from the first. */
    public int subtract(int a, int b) {
        return a - b;
    }

    /** Multiplies two double numbers. */
    public double multiply(double a, double b) {
        return a * b;
    }

    /**
     * Divides the first integer by the second.
     * @throws IllegalArgumentException if divisor is zero.
     */
    public double divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Error: Division by zero is not allowed.");
        }
        return (double) a / b;
    }
}
