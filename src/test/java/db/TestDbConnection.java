package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class TestDbConnection {
    private static String url;
    private static String user;
    private static String password;
    private static int connectionTimeout;
    private static int queryTimeout;

    static {
        try (InputStream input = TestDbConnection.class.getClassLoader().getResourceAsStream("db-test.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            url = prop.getProperty("db.url");
            user = prop.getProperty("db.user");
            String encodedPwd = prop.getProperty("db.password");
            if (encodedPwd != null && encodedPwd.startsWith("REDACTED_BASE64:")) {
                password = new String(Base64.getDecoder().decode(encodedPwd.replace("REDACTED_BASE64:", "")));
            } else {
                password = encodedPwd;
            }
            connectionTimeout = Integer.parseInt(prop.getProperty("db.connectionTimeout", "5000"));
            queryTimeout = Integer.parseInt(prop.getProperty("db.queryTimeout", "3000"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DB properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        DriverManager.setLoginTimeout(connectionTimeout / 1000);
        return DriverManager.getConnection(url, user, password);
    }

    public static int getQueryTimeout() {
        return queryTimeout;
    }
}

