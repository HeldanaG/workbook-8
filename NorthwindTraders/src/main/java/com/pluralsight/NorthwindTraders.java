package com.pluralsight;
 import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.*;
import javax.sql.DataSource;
import java.util.Scanner;

public class NorthwindTraders {

    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            if (args.length != 2) {
                System.out.println(
                        "Application needs two arguments to run: " +
                                "java com.pluralsight.UsingDriverManager <username> <password>"
                );
                System.exit(1);
            }

            String username = args[0];
            String password = args[1];

            // Create the datasource
            BasicDataSource dataSource = new BasicDataSource ();

            // Configure the datasource
            dataSource.setUrl("jdbc:mysql://localhost:3306/northwind");
            dataSource.setUsername(username);
            dataSource.setPassword(password);

            int option;
            do {
                System.out.println("\nWhat do you want to do?");
                System.out.println("1. Display all products.");
                System.out.println("2. Display all customers.");
                System.out.println("3. Display all categories.");
                System.out.println("0.Exit");
                System.out.print("Select Your options: ");


                option = input.nextInt();
                switch (option) {
                    case 1:
                        DisplayProduts(dataSource);
                        break;
                    case 2:
                        DisplayCustomers(dataSource);
                        break;
                    case 3:
                        DisplayCategories(dataSource);
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }

            } while (option != 0 && option > 3);


        } catch (SQLException e) {
            e.printStackTrace();        }
    }

    public static void DisplayProduts(DataSource dataSource) throws SQLException {

        // opened MySql workbench and clicking localhost
        try (Connection connection = dataSource.getConnection();
             // opened new query window & type the query in that window
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT ProductID,ProductName, UnitPrice, UnitsInStock FROM Products "
             );
        ) {

            // like clicking the lighting bolt to run
            try (ResultSet results = preparedStatement.executeQuery();
            ) {
                System.out.println("\nAll products");
                System.out.println("Id   Name                          Price     InStock");
                System.out.println("--------------------------------------------------");

                // view the resuls until next value is null
                while (results.next()) {
                    int id = results.getInt(1);
                    String name = results.getString(2);
                    double price = results.getDouble(3);
                    int stock = results.getInt(4);

                    System.out.printf("%-4d %-30s %-9.2f %d%n", id, name, price, stock);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();        }
        // closing opened  statement
        //You should close your resources in the reverse order that you created those resources

    }

    public static void DisplayCustomers(DataSource dataSource) throws SQLException {

        // Connect to MySQL Northwind database using username and password
        try (Connection connection =dataSource.getConnection();
             // Query to get customer contact name, company name, city, country, and phone number
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT ContactName, CompanyName, City, Country, Phone FROM Customers ORDER BY Country"
             )
        ) {

            try (ResultSet results = preparedStatement.executeQuery()) {
                System.out.println("\nAll customers");
                System.out.println("Contact Name         Company               City             Country          Phone");
                System.out.println("----------------------------------------------------------------------------------");

                // Iterate through the results
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

        } catch (SQLException e) {
            e.printStackTrace();        }

    }


    public static void DisplayCategories(DataSource dataSource) throws SQLException{
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
        ) {
            try (ResultSet categoryResults = statement.executeQuery(
                    "SELECT CategoryID, CategoryName FROM Categories ORDER BY CategoryID")){

                System.out.println("\nCategories");
                System.out.println("ID   Name");
                System.out.println("-------------------");

                while (categoryResults.next()) {
                    int categoryId = categoryResults.getInt("CategoryID");
                    String categoryName = categoryResults.getString("CategoryName");
                    System.out.printf("%-4d %-20s%n", categoryId, categoryName);
                }
            }
            int selectedCategoryId;
            do {
                System.out.print("\nEnter a category ID to see products (0 to exit): ");
                selectedCategoryId = input.nextInt();

                if (selectedCategoryId == 0) {
                    System.out.println("Exiting product lookup...");
                    break;
                }
            String query = """
                    SELECT ProductID, ProductName, UnitPrice, UnitsInStock
                    FROM Products
                    WHERE CategoryID = ?
                    """;

            try (PreparedStatement productStmt = connection.prepareStatement(query)) {
                productStmt.setInt(1, selectedCategoryId);
                ResultSet products = productStmt.executeQuery();

                System.out.println("\nProducts in Category ID - " + selectedCategoryId);
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
    } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
