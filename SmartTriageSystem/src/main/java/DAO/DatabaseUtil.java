package DAO;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseUtil {

    private static HikariDataSource dataSource;

    // Bloque estático para inicializar el pool una sola vez
    static {
        // 1. Configuración de HikariCP
        HikariConfig config = new HikariConfig();

        // El driver de PostgreSQL
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/triage_db");
        config.setUsername("postgres"); // ¡Cambia esto!
        config.setPassword("29072003"); // ¡Cambia esto!

        // 2. Optimización y configuración del pool
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // Propiedades de rendimiento (ajustar según la carga esperada)
        config.setMinimumIdle(5); // Conexiones inactivas mínimas
        config.setMaximumPoolSize(20); // Conexiones máximas para el servidor de sockets
        config.setConnectionTimeout(30000); // 30 segundos
        config.setIdleTimeout(600000); // 10 minutos

        // 3. Creación del HikariDataSource
        dataSource = new HikariDataSource(config);
        System.out.println("HikariCP Pool inicializado con éxito.");
    }

    /**
     * Obtiene una conexión del pool.
     * @return una conexión JDBC.
     * @throws SQLException si falla la obtención de la conexión.
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Cierra el pool de conexiones cuando la aplicación se detiene.
     */
    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("HikariCP Pool cerrado.");
        }
    }
}
