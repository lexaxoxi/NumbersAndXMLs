import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Dao {
    private static final Logger logger = LoggerFactory.getLogger(Dao.class);

    private String dbName;
    private String host;
    private int port;
    private String user;
    private String pass;

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    private String buildURL() {
        return String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
    }

    public void inputDB(String tableName, String columnName, int entriesCount) {
        logger.info("Start input database");
        try (Connection connection = DriverManager.getConnection(buildURL(), user, pass)) {

            String insertSQL = String.format("INSERT INTO %s (%s) VALUES (?)", tableName, columnName);
            String truncateSQL = "TRUNCATE " + tableName;

            connection.createStatement().execute(truncateSQL);
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);

            for (int i = 1; i <= entriesCount; i++) {
                preparedStatement.setInt(1, i);
                preparedStatement.addBatch();
                if (i % 1000 == 0 || i == entriesCount) {
                    preparedStatement.executeBatch();
                }
            }
            logger.info("Database input");
        } catch (SQLException e) {
            logger.error("Connection to DB failed", e);
            throw new IllegalStateException("Connection to DB failed", e);
        }
    }

    public List<Integer> getAll(String tableName) {
        List<Integer> result = new ArrayList<>();
        String select = "SELECT * FROM " + tableName;
        try (Connection connection = DriverManager.getConnection(buildURL(), user, pass)) {
            ResultSet resultSet = connection.createStatement().executeQuery(select);
            while (resultSet.next()) {
                result.add(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            logger.error("Connection to DB failed", e);
            throw new IllegalStateException("Connection to DB failed", e);
        }
        return result;
    }
}