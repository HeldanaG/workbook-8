package com.pluralsight.dao;

import com.pluralsight.model.Shippers;
import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShippersDao {

    private BasicDataSource dataSource;

    public ShippersDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Shippers> displayShippers() {
        List<Shippers> shippers = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM shippers;");
             ResultSet resultSet = preparedStatement.executeQuery();
        ) {
            while (resultSet.next()) {
            int shipperId = resultSet.getInt("ShipperId");
            String companyName = resultSet.getString("CompanyName");
            String phone = resultSet.getString("Phone");
            Shippers shipper = new Shippers(shipperId, companyName, phone);
            shippers.add(shipper);
        }
            System.out.println("\nShipper ID        Company Name             Phone");
            System.out.println("----------------------------------------------------");
            for (Shippers shipper : shippers)
                System.out.printf("%-11d %-28s %15s\n", shipper.getShipperId(), shipper.getCompanyName(), shipper.getPhone());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shippers;
    }

    public int insertShipper(String companyName, String phone) {
        String sql = "INSERT INTO shippers (CompanyName, Phone) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        )
        {   preparedStatement.setString(1, companyName);
            preparedStatement.setString(2, phone);
            preparedStatement.executeUpdate();
            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updateShipperPhone(int inputId, String inputPhone) {
        String sql = "UPDATE shippers SET Phone = ? WHERE ShipperId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)
        )
        {   preparedStatement.setString(1, inputPhone);
            preparedStatement.setInt(2, inputId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }    }

    public void deleteShipper(int inputId) {
        String sql = "DELETE FROM shippers WHERE ShipperId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)
        )
        {   preparedStatement.setInt(1, inputId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
