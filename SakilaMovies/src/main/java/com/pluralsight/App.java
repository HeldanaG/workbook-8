package com.pluralsight;

import com.pluralsight.dao.ActorDao;
import com.pluralsight.models.Actor;
import com.pluralsight.models.Film;
import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java SakilaMovies <username> <password>");
            return;
        }

        String username = args[0];
        String password = args[1];

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        ActorDao dataManager = new ActorDao(dataSource);
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter actor's last name to search: ");
            String lastName = scanner.nextLine();
            List<Actor> actors = dataManager.searchActorsByLastName(lastName);

            if (actors.isEmpty()) {
                System.out.println("No actors found with last name: " + lastName);
                return;
            }

            System.out.println("\nMatching actors:");
            for (Actor actor : actors) {
                System.out.printf("%d - %s %s\n", actor.getActorId(), actor.getFirstName(), actor.getLastName());
            }

            System.out.print("\nEnter actor ID to see their films: ");
            int actorId = Integer.parseInt(scanner.nextLine());

            List<Film> films = dataManager.getFilmsByActorId(actorId);
            if (films.isEmpty()) {
                System.out.println("No films found for this actor.");
            } else {
                System.out.println("\nFilms:");
                System.out.println("Title                          Year    Length  Description");
                System.out.println("------------------------------------------------------------------");
                for (Film film : films) {
                    System.out.printf("%-30s %-7s %-7d %s\n",
                            film.getTitle(), film.getReleaseYear(), film.getLength(), film.getDescription());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
