import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class task7 {
    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String hash(String value) throws Exception {
        byte[] bytes = MessageDigest.getInstance("SHA-256")
                .digest(value.getBytes(StandardCharsets.UTF_8));
        StringBuilder out = new StringBuilder();
        for (byte b : bytes) out.append(String.format("%02x", b));
        return out.toString();
    }

    public static void main(String[] args) throws Exception {
        String url = env("DB_URL", "jdbc:mysql://localhost:3306/assignment5");
        String user = env("DB_USER", "root");
        String password = env("DB_PASSWORD", "");
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement st = con.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS users (id VARCHAR(80) PRIMARY KEY, pwd_hash CHAR(64) NOT NULL)");
            st.executeUpdate("DELETE FROM users WHERE id='demo'");
            try (PreparedStatement insert = con.prepareStatement(
                    "INSERT INTO users(id, pwd_hash) VALUES (?, ?)")) {
                insert.setString(1, "demo");
                insert.setString(2, hash("correct-password"));
                insert.executeUpdate();
            }

            String injectedId = "demo' OR '1'='1' -- ";
            String invalidPassword = "wrong";
            String unsafeSql = "SELECT * FROM users WHERE id='" + injectedId
                    + "' AND pwd_hash='" + hash(invalidPassword) + "'";
            try (Statement unsafe = con.createStatement();
                 ResultSet rs = unsafe.executeQuery(unsafeSql)) {
                System.out.println("Unsafe login accepted: " + rs.next());
            }

            String safeSql = "SELECT 1 FROM users WHERE id=? AND pwd_hash=?";
            try (PreparedStatement safe = con.prepareStatement(safeSql)) {
                safe.setString(1, injectedId);
                safe.setString(2, hash(invalidPassword));
                try (ResultSet rs = safe.executeQuery()) {
                    System.out.println("PreparedStatement login accepted: " + rs.next());
                }
            }
        }
    }
}
