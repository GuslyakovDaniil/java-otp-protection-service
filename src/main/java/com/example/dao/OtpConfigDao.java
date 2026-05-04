package com.example.dao;

import com.example.model.OtpConfig;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class OtpConfigDao {
    private final DataSource dataSource;

    public OtpConfigDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public OtpConfig getConfig() {
        String sql = "SELECT * FROM otp_config WHERE id = 1";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return new OtpConfig(rs.getInt("id"), rs.getInt("code_length"), rs.getInt("ttl_seconds"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting OTP config", e);
        }
        return new OtpConfig(1, 6, 300); // Default fallback
    }

    public void updateConfig(int length, int ttl) {
        String sql = "UPDATE otp_config SET code_length = ?, ttl_seconds = ? WHERE id = 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, length);
            pstmt.setInt(2, ttl);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating OTP config", e);
        }
    }
}