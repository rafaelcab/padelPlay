package com.padelplay.server.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Controlador temporal para ver datos de la base de datos H2.
 * Accesible en: http://localhost:8080/api/db
 */
@RestController
@RequestMapping("/api/db")
@CrossOrigin(origins = "*")
public class H2DatabaseController {

    @Autowired
    private DataSource dataSource;

    /**
     * Lista todas las tablas de la base de datos
     * GET http://localhost:8080/api/db/tables
     */
    @GetMapping("/tables")
    public ResponseEntity<List<String>> getTables() {
        List<String> tables = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, "PUBLIC", "%", new String[]{"TABLE"});
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(tables);
    }

    /**
     * Ejecuta una consulta SQL y devuelve los resultados
     * GET http://localhost:8080/api/db/query?sql=SELECT * FROM USUARIOS
     */
    @GetMapping("/query")
    public ResponseEntity<List<Map<String, Object>>> executeQuery(@RequestParam String sql) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        } catch (SQLException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            results.add(error);
        }
        return ResponseEntity.ok(results);
    }

    /**
     * Muestra todos los usuarios
     * GET http://localhost:8080/api/db/usuarios
     */
    @GetMapping("/usuarios")
    public ResponseEntity<List<Map<String, Object>>> getUsuarios() {
        return executeQuery("SELECT * FROM USUARIOS");
    }

    /**
     * Muestra todos los perfiles de jugador
     * GET http://localhost:8080/api/db/perfiles
     */
    @GetMapping("/perfiles")
    public ResponseEntity<List<Map<String, Object>>> getPerfiles() {
        return executeQuery("SELECT * FROM PERFILES_JUGADOR");
    }

    /**
     * Muestra los detalles técnicos
     * GET http://localhost:8080/api/db/detalles
     */
    @GetMapping("/detalles")
    public ResponseEntity<List<Map<String, Object>>> getDetalles() {
        return executeQuery("SELECT * FROM DETALLES_TECNICOS");
    }

    /**
     * Muestra estructura de una tabla
     * GET http://localhost:8080/api/db/describe?table=USUARIOS
     */
    @GetMapping("/describe")
    public ResponseEntity<List<Map<String, Object>>> describeTable(@RequestParam String table) {
        return executeQuery("SHOW COLUMNS FROM " + table);
    }
}
