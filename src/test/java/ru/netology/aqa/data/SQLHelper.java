package ru.netology.aqa.data;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3307/app";
    private static final String DB_USER = "app";
    private static final String DB_PASSWORD = "pass";

    private SQLHelper() {
    }

    private static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    DB_URL,
                    DB_USER,
                    DB_PASSWORD
            );
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось подключиться к базе данных",
                    e
            );
        }
    }

    public static String getPaymentStatus() {
        var runner = new QueryRunner();

        String sql = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1";

        try (var conn = getConnection()) {
            return runner.query(conn, sql, new ScalarHandler<>());
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось получить статус платежа",
                    e
            );
        }
    }

    public static int getPaymentCount() {
        var runner = new QueryRunner();

        String sql = "SELECT COUNT(*) FROM payment_entity";

        try (var conn = getConnection()) {
            Number count = runner.query(conn, sql, new ScalarHandler<>());
            return count.intValue();
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось получить количество платежей",
                    e
            );
        }
    }

    public static int getOrderCount() {
        var runner = new QueryRunner();

        String sql = "SELECT COUNT(*) FROM order_entity";

        try (var conn = getConnection()) {
            Number count = runner.query(conn, sql, new ScalarHandler<>());
            return count.intValue();
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось получить количество заказов",
                    e
            );
        }
    }

    public static void cleanDatabase() {
        var runner = new QueryRunner();

        try (var conn = getConnection()) {
            runner.update(conn, "DELETE FROM order_entity");
            runner.update(conn, "DELETE FROM payment_entity");
            runner.update(conn, "DELETE FROM credit_request_entity");
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось очистить базу данных",
                    e
            );
        }
    }
}
