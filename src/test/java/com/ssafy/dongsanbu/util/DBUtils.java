package com.ssafy.dongsanbu.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {

    public static void truncate(String tableName, String databaseName, String username, String password) {
        String url = "jdbc:mysql://localhost:3306/" + databaseName;

        String sql = "DELETE FROM " + tableName;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int countAll(String tableName, String databaseName, String username, String password) {
        String url = "jdbc:mysql://localhost:3306/" + databaseName;

        String sql = "SELECT count(*) AS total_count FROM " + tableName;

        int totalCount = 0;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                totalCount = resultSet.getInt("total_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalCount;
    }
}
