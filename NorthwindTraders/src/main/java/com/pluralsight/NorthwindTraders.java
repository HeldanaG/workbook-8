package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.*;
import java.util.Scanner;

public class NorthwindTraders {

    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java com.pluralsight.NorthwindTraders <username> <password>");
            System.exit(1);
        }

        String username = args[0];
        String password = args[1];

        // Setup connection pool
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/northwind");
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        try (Connection connection = dataSource.getConnection()) {
            int option;
            do {
                System.out.println("\nWhat do you want to do?");
                System.out.println("1. Display all products.");
                System.out.println("2. Display all customers.");
                System.out.println("3. Display all categories.");
                System.out.println("0. Exit");
                System.out.print("Select your option: ");

                option = input.nextInt();
                input.nextLine(); // clear newline

                switch (option) {
                    case 1 -> DisplayProducts(connection);
                    case 2 -> DisplayCustomers(connection);
                    case 3 -> DisplayCategories(connection);
                    case 0 -> System.out.println("Exiting...");
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } while (option != 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void DisplayProducts(Connection connection) throws SQLException {
        String sql = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM Products";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet results = preparedStatement.executeQuery()) {

            System.out.println("\nAll Products");
            System.out.println("Id   Name                          Price     InStock");
            System.out.println("----------------------------------------------------");

            while (results.next()) {
                int id = results.getInt("ProductID");
                String name = results.getString("ProductName");
                double price = results.getDouble("UnitPrice");
                int stock = results.getInt("UnitsInStock");

                System.out.printf("%-4d %-30s %-9.2f %d%n", id, name, price, stock);
            }
        }
        System.out.println("----------------------------------------------------");
    }

    public static void DisplayCustomers(Connection connection) throws SQLException {
        String sql = "SELECT ContactName, CompanyName, City, Country, Phone FROM Customers ORDER BY Country";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet results = preparedStatement.executeQuery()) {

            System.out.println("\nAll Customers");
            System.out.println("Contact Name         Company               City             Country          Phone");
            System.out.println("----------------------------------------------------------------------------------");

            while (results.next()) {
                String contact = results.getString("ContactName");
                String company = results.getString("CompanyName");
                String city = results.getString("City");
                String country = results.getString("Country");
                String phone = results.getString("Phone");

                System.out.printf("%-20s %-20s %-16s %-16s %s%n",
                        contact, company, city, country, phone);
            }
        }
        System.out.println("----------------------------------------------------------------------------------");
    }

    public static void DisplayCategories(Connection connection) throws SQLException {
        String categorySql = "SELECT CategoryID, CategoryName FROM Categories ORDER BY CategoryID";

        try (Statement statement = connection.createStatement();
             ResultSet categoryResults = statement.executeQuery(categorySql)) {

            System.out.println("\nCategories");
            System.out.println("ID   Name");
            System.out.println("--------------------");

            while (categoryResults.next()) {
                int id = categoryResults.getInt("CategoryID");
                String name = categoryResults.getString("CategoryName");
                System.out.printf("%-4d %-20s%n", id, name);
            }

            int selectedCategoryId;
            do {
                System.out.print("\nEnter a category ID to see products (0 to exit): ");
                selectedCategoryId = input.nextInt();

                if (selectedCategoryId == 0) {
                    System.out.println("Exiting product lookup...");
                    break;
                }

                String productQuery = """
                        SELECT ProductID, ProductName, UnitPrice, UnitsInStock
                        FROM Products
                        WHERE CategoryID = ?
                        """;

                try (PreparedStatement productStmt = connection.prepareStatement(productQuery)) {
                    productStmt.setInt(1, selectedCategoryId);
                    ResultSet products = productStmt.executeQuery();

                    System.out.println("\nProducts in Category ID " + selectedCategoryId);
                    System.out.println("ID   Name                          Price     InStock");
                    System.out.println("----------------------------------------------------");

                    boolean hasProducts = false;
                    while (products.next()) {
                        hasProducts = true;
                        int id = products.getInt("ProductID");
                        String name = products.getString("ProductName");
                        double price = products.getDouble("UnitPrice");
                        int stock = products.getInt("UnitsInStock");

                        System.out.printf("%-4d %-30s %-9.2f %d%n", id, name, price, stock);
                    }

                    if (!hasProducts) {
                        System.out.println("No products found for that category.");
                    }
                }

            } while (true);
        }
        System.out.println("----------------------------------------------------");
    }
}
