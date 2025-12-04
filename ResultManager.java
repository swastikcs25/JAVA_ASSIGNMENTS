import java.util.*;

class InvalidMarksException extends Exception {
    public InvalidMarksException(String msg) {
        super(msg);
    }
}

class Student {
    private int roll;
    private String name;
    private int[] marks = new int[3];

    public Student(int roll, String name, int[] marks) throws InvalidMarksException {
        this.roll = roll;
        this.name = name;
        this.marks = marks;
        validateMarks();
    }

    private void validateMarks() throws InvalidMarksException {
        for (int m : marks)
            if (m < 0 || m > 100)
                throw new InvalidMarksException("Marks must be 0–100.");
    }

    public double average() {
        return (marks[0] + marks[1] + marks[2]) / 3.0;
    }

    public String result() {
        for (int m : marks) if (m < 35) return "Fail";
        return "Pass";
    }

    public int getRoll() { return roll; }

    public void display() {
        System.out.println("\nRoll: " + roll);
        System.out.println("Name: " + name);
        System.out.println("Marks: " + marks[0] + " " + marks[1] + " " + marks[2]);
        System.out.println("Average: " + average());
        System.out.println("Result: " + result());
    }
}

public class ResultManager {
    private Student[] list = new Student[100];
    private int count = 0;
    private Scanner sc = new Scanner(System.in);

    public void addStudent() {
        try {
            System.out.print("Roll Number: ");
            int roll = sc.nextInt();
            sc.nextLine();

            System.out.print("Student Name: ");
            String name = sc.nextLine();

            int[] m = new int[3];
            for (int i = 0; i < 3; i++) {
                System.out.print("Marks " + (i + 1) + ": ");
                m[i] = sc.nextInt();
            }

            list[count++] = new Student(roll, name, m);
            System.out.println("Student added.\n");

        } catch (InvalidMarksException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input.");
            sc.nextLine();
        }
    }

    public void showStudent() {
        System.out.print("Enter roll: ");
        int r = sc.nextInt();
        for (int i = 0; i < count; i++) {
            if (list[i].getRoll() == r) {
                list[i].display();
                return;
            }
        }
        System.out.println("Student not found.");
    }

    public void menu() {
        while (true) {
            System.out.println("\n1. Add Student");
            System.out.println("2. Show Student");
            System.out.println("3. Exit");
            System.out.print("Choice: ");

            int ch = sc.nextInt();
            switch (ch) {
                case 1: addStudent(); break;
                case 2: showStudent(); break;
                case 3: System.out.println("Exit."); return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    public static void main(String[] args) {
        new ResultManager().menu();
    }
}
