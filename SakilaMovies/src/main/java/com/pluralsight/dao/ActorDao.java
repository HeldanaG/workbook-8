package com.pluralsight.dao;

import com.pluralsight.models.Actor;
import com.pluralsight.models.Film;
import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActorDao {
    private DataSource dataSource;

    public ActorDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Actor> searchActorsByLastName(String lastName) throws SQLException {
        String sql = "SELECT actor_id, first_name, last_name FROM actor WHERE last_name = ?";
        List<Actor> actors = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lastName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    actors.add(new Actor(
                            rs.getInt("actor_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name")
                    ));
                }
            }
        }
        return actors;
    }

    public List<Film> getFilmsByActorId(int actorId) throws SQLException {
        String sql = """
            SELECT f.film_id, f.title, f.description, f.release_year, f.length
            FROM film f
            JOIN film_actor fa ON f.film_id = fa.film_id
            WHERE fa.actor_id = ?
            ORDER BY f.title""";

        List<Film> films = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, actorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    films.add(new Film(
                            rs.getInt("film_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("release_year"),
                            rs.getInt("length")
                    ));
                }
            }
        }
        return films;
    }
}