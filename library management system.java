import java.io.*;
import java.util.*;

/*
  Single-file simple implementation.
  Save as LibraryApp.java and run: javac LibraryApp.java && java LibraryApp
  Data files: books.txt, members.txt (created automatically)
*/

public class LibraryApp {
    public static void main(String[] args) {
        LibraryManager lm = new LibraryManager();
        lm.loadFromFile();
        lm.menu();
        lm.saveToFile();
        System.out.println("Exiting. Data saved.");
    }
}

/* ------------------ Book ------------------ */
class Book {
    int bookId;
    String title;
    String author;
    String category;
    boolean isIssued;

    Book(int id, String t, String a, String c, boolean issued) {
        this.bookId = id; this.title = t; this.author = a; this.category = c; this.isIssued = issued;
    }

    String toFileLine() {
        // CSV: id|title|author|category|isIssued
        return bookId + "|" + escape(title) + "|" + escape(author) + "|" + escape(category) + "|" + isIssued;
    }

    static Book fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 5) return null;
        int id = Integer.parseInt(p[0]);
        String t = unescape(p[1]);
        String a = unescape(p[2]);
        String c = unescape(p[3]);
        boolean issued = Boolean.parseBoolean(p[4]);
        return new Book(id, t, a, c, issued);
    }

    void display() {
        System.out.printf("ID:%d | %s | %s | %s | Issued:%s%n", bookId, title, author, category, isIssued);
    }

    void markAsIssued() { isIssued = true; }
    void markAsReturned() { isIssued = false; }

    private static String escape(String s) { return s.replace("|", "/|/"); }
    private static String unescape(String s) { return s.replace("/|/", "|"); }
}

/* ------------------ Member ------------------ */
class Member {
    int memberId;
    String name;
    String email;
    List<Integer> issuedBooks = new ArrayList<>();

    Member(int id, String n, String e) {
        this.memberId = id; this.name = n; this.email = e;
    }

    String toFileLine() {
        // id|name|email|commaSeparatedBookIds
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < issuedBooks.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(issuedBooks.get(i));
        }
        return memberId + "|" + escape(name) + "|" + escape(email) + "|" + sb.toString();
    }

    static Member fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 4) return null;
        int id = Integer.parseInt(p[0]);
        String n = unescape(p[1]);
        String e = unescape(p[2]);
        Member m = new Member(id, n, e);
        if (!p[3].isEmpty()) {
            String[] ids = p[3].split(",");
            for (String s : ids) m.issuedBooks.add(Integer.parseInt(s));
        }
        return m;
    }

    void display() {
        System.out.printf("ID:%d | %s | %s | IssuedBooks:%s%n", memberId, name, email, issuedBooks);
    }

    void addIssuedBook(int bookId) { issuedBooks.add(bookId); }
    boolean returnIssuedBook(int bookId) { return issuedBooks.remove(Integer.valueOf(bookId)); }

    private static String escape(String s) { return s.replace("|", "/|/"); }
    private static String unescape(String s) { return s.replace("/|/", "|"); }
}

/* ------------------ LibraryManager ------------------ */
class LibraryManager {
    Map<Integer, Book> books = new HashMap<>();
    Map<Integer, Member> members = new HashMap<>();
    final String booksFile = "books.txt";
    final String membersFile = "members.txt";
    Scanner sc = new Scanner(System.in);

    void menu() {
        while (true) {
            System.out.println("\n--- City Library ---");
            System.out.println("1.Add Book 2.Add Member 3.Issue Book 4.Return Book 5.Search Books 6.Sort Books 7.Show All 8.Exit");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            try {
                switch (ch) {
                    case "1": addBook(); saveToFile(); break;
                    case "2": addMember(); saveToFile(); break;
                    case "3": issueBook(); saveToFile(); break;
                    case "4": returnBook(); saveToFile(); break;
                    case "5": searchBooks(); break;
                    case "6": sortBooks(); break;
                    case "7": showAll(); break;
                    case "8": return;
                    default: System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    void addBook() {
        System.out.print("Title: "); String t = sc.nextLine().trim();
        System.out.print("Author: "); String a = sc.nextLine().trim();
        System.out.print("Category: "); String c = sc.nextLine().trim();
        int id = nextBookId();
        Book b = new Book(id, t, a, c, false);
        books.put(id, b);
        System.out.println("Book added with ID: " + id);
    }

    void addMember() {
        System.out.print("Name: "); String n = sc.nextLine().trim();
        System.out.print("Email: "); String e = sc.nextLine().trim();
        if (!isValidEmail(e)) { System.out.println("Invalid email format. Member not added."); return; }
        int id = nextMemberId();
        Member m = new Member(id, n, e);
        members.put(id, m);
        System.out.println("Member added with ID: " + id);
    }

    void issueBook() {
        System.out.print("Member ID: "); int mid = readInt();
        Member m = members.get(mid);
        if (m == null) { System.out.println("Member not found."); return; }
        System.out.print("Book ID: "); int bid = readInt();
        Book b = books.get(bid);
        if (b == null) { System.out.println("Book not found."); return; }
        if (b.isIssued) { System.out.println("Book already issued."); return; }
        b.markAsIssued();
        m.addIssuedBook(bid);
        System.out.println("Book issued.");
    }

    void returnBook() {
        System.out.print("Member ID: "); int mid = readInt();
        Member m = members.get(mid);
        if (m == null) { System.out.println("Member not found."); return; }
        System.out.print("Book ID: "); int bid = readInt();
        Book b = books.get(bid);
        if (b == null) { System.out.println("Book not found."); return; }
        boolean removed = m.returnIssuedBook(bid);
        if (!removed) { System.out.println("This member did not have that book."); return; }
        b.markAsReturned();
        System.out.println("Book returned.");
    }

    void searchBooks() {
        System.out.print("Search by (title/author/category): ");
        String key = sc.nextLine().trim().toLowerCase();
        System.out.print("Enter search text: ");
        String q = sc.nextLine().trim().toLowerCase();
        boolean found = false;
        for (Book b : books.values()) {
            switch (key) {
                case "title":
                    if (b.title.toLowerCase().contains(q)) { b.display(); found = true; }
                    break;
                case "author":
                    if (b.author.toLowerCase().contains(q)) { b.display(); found = true; }
                    break;
                case "category":
                    if (b.category.toLowerCase().contains(q)) { b.display(); found = true; }
                    break;
                default:
                    System.out.println("Unknown key."); return;
            }
        }
        if (!found) System.out.println("No results.");
    }

    void sortBooks() {
        System.out.println("Sort by: 1.Title 2.Author 3.Category");
        String c = sc.nextLine().trim();
        List<Book> list = new ArrayList<>(books.values());
        switch (c) {
            case "1":
                Collections.sort(list, Comparator.comparing(b -> b.title.toLowerCase()));
                break;
            case "2":
                Collections.sort(list, Comparator.comparing(b -> b.author.toLowerCase()));
                break;
            case "3":
                Collections.sort(list, Comparator.comparing(b -> b.category.toLowerCase()));
                break;
            default:
                System.out.println("Invalid."); return;
        }
        for (Book b : list) b.display();
    }

    void showAll() {
        System.out.println("\nBooks:");
        for (Book b : books.values()) b.display();
        System.out.println("\nMembers:");
        for (Member m : members.values()) m.display();
    }

    /* ---------------- File IO ---------------- */
    void loadFromFile() {
        // books
        File bf = new File(booksFile);
        if (bf.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(bf))) {
                String line;
                while ((line = br.readLine()) != null) {
                    Book b = Book.fromFileLine(line);
                    if (b != null) books.put(b.bookId, b);
                }
            } catch (IOException e) { System.out.println("Error loading books: " + e.getMessage()); }
        }
        // members
        File mf = new File(membersFile);
        if (mf.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(mf))) {
                String line;
                while ((line = br.readLine()) != null) {
                    Member m = Member.fromFileLine(line);
                    if (m != null) members.put(m.memberId, m);
                }
            } catch (IOException e) { System.out.println("Error loading members: " + e.getMessage()); }
        }
    }

    void saveToFile() {
        // books
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(booksFile))) {
            for (Book b : books.values()) bw.write(b.toFileLine() + "\n");
        } catch (IOException e) { System.out.println("Error saving books: " + e.getMessage()); }
        // members
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(membersFile))) {
            for (Member m : members.values()) bw.write(m.toFileLine() + "\n");
        } catch (IOException e) { System.out.println("Error saving members: " + e.getMessage()); }
    }

    /* ---------------- Helpers ---------------- */
    int nextBookId() {
        return books.keySet().stream().max(Integer::compare).orElse(100) + 1;
    }
    int nextMemberId() {
        return members.keySet().stream().max(Integer::compare).orElse(200) + 1;
    }
    int readInt() {
        String s = sc.nextLine().trim();
        try { return Integer.parseInt(s); } catch (Exception e) { return -1; }
    }
    boolean isValidEmail(String e) {
        return e.contains("@") && e.contains(".") && e.length() >= 5;
    }
}
