import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class task8 {
    public static int loadAll(Connection con, List<String[]> passes) throws SQLException {
        String sql = "INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) VALUES (?, ?, ?, ?, ?)";
        boolean oldAutoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int n = 0;
            for (String[] p : passes) {
                ps.setString(1, p[0]);
                ps.setString(2, p[1]);
                ps.setString(3, p[2]);
                ps.setInt(4, Integer.parseInt(p[3]));
                ps.setDouble(5, Double.parseDouble(p[4]));
                ps.addBatch();
                n++;
                if (n % 1000 == 0) ps.executeBatch();
            }
            ps.executeBatch();
            con.commit();
            return n;
        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(oldAutoCommit);
        }
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    public static void main(String[] args) throws Exception {
        String url = env("DB_URL", "jdbc:mysql://localhost:3306/assignment5");
        String user = env("DB_USER", "root");
        String password = env("DB_PASSWORD", "");
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement st = con.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS bus_pass (pass_id VARCHAR(30) PRIMARY KEY, reg_no VARCHAR(30) NOT NULL, name VARCHAR(100) NOT NULL, route_no INT NOT NULL, amount DECIMAL(10,2) NOT NULL)");
            st.executeUpdate("DELETE FROM bus_pass");
            List<String[]> rows = new ArrayList<>();
            int size = args.length > 0 ? Integer.parseInt(args[0]) : 1000;
            for (int i = 1; i <= size; i++) {
                rows.add(new String[]{"P" + i, "REG" + i, "Student" + i,
                        String.valueOf((i % 20) + 1), "4500.00"});
            }
            long start = System.nanoTime();
            int loaded = loadAll(con, rows);
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            System.out.println("Rows loaded: " + loaded);
            System.out.println("Batch transaction time: " + elapsed + " ms");

            try (PreparedStatement count = con.prepareStatement("SELECT COUNT(*) FROM bus_pass");
                 var rs = count.executeQuery()) {
                if (rs.next()) System.out.println("Rows in table: " + rs.getInt(1));
            }
        }
    }
}
