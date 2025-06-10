package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Scanner;

public class App {
    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println(
                    "Application needs two arguments to run: " +
                            "java com.pluralsight.UsingDriverManager <username> <password>"
            );
            System.exit(1);}

        String username = args[0];
        String password = args[1];

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
        dataSource.setUsername(username);
        dataSource.setPassword(password);



        try {
            // Ask for last name and display matches
            System.out.print("Enter actor's last name: ");
            String lastName = input.nextLine();

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(
                         "SELECT actor_id, first_name, last_name FROM actor WHERE last_name = ?"
                 )) {
                stmt.setString(1, lastName);

                try (ResultSet results = stmt.executeQuery()) {
                    if (results.next()) {
                        System.out.println("\nActors with last name '" + lastName + "':");
                        System.out.println("ID   First Name       Last Name");
                        System.out.println("-----------------------------------");
                        do {
                            System.out.printf("%-4d %-15s %-15s%n",
                                    results.getInt("actor_id"),
                                    results.getString("first_name"),
                                    results.getString("last_name"));
                        } while (results.next());
                    } else {
                        System.out.println("No actors found with last name '" + lastName + "'.");
                    }
                }
            }

            // Ask for full name to display movies
            System.out.print("\nEnter actor's first name: ");
            String firstName = input.nextLine();
            System.out.print("Enter actor's last name: ");
            String fullLastName = input.nextLine();

            String movieQuery = """
                SELECT film.title
                FROM film
                JOIN film_actor ON film.film_id = film_actor.film_id
                JOIN actor ON film_actor.actor_id = actor.actor_id
                WHERE actor.first_name = ? AND actor.last_name = ?
                ORDER BY film.title;
                """;

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(movieQuery)) {
                stmt.setString(1, firstName);
                stmt.setString(2, fullLastName);

                try (ResultSet results = stmt.executeQuery()) {
                    if (results.next()) {
                        System.out.println("\nMovies featuring " + firstName + " " + fullLastName + ":");
                        do {
                            System.out.println("- " + results.getString("title"));
                        } while (results.next());
                    } else {
                        System.out.println("No movies found for " + firstName + " " + fullLastName + ".");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
