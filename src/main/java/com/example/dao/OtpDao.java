package com.example.dao;

import com.example.model.OtpCode;
import com.example.model.OtpStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class OtpDao {
    private final DataSource dataSource;

    public OtpDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(OtpCode otpCode) {
        String sql = "INSERT INTO otp_codes (user_id, operation_id, code, status, expires_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, otpCode.getUserId());
            pstmt.setString(2, otpCode.getOperationId());
            pstmt.setString(3, otpCode.getCode());
            pstmt.setString(4, otpCode.getStatus().name());
            pstmt.setTimestamp(5, otpCode.getExpiresAt());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving OTP code", e);
        }
    }

    public OtpCode findActiveByOperationId(String operationId, int userId) {
        String sql = "SELECT * FROM otp_codes WHERE operation_id = ? AND user_id = ? AND status = 'ACTIVE' ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, operationId);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new OtpCode(rs.getInt("id"), rs.getInt("user_id"),
                            rs.getString("operation_id"), rs.getString("code"),
                            OtpStatus.valueOf(rs.getString("status")),
                            rs.getTimestamp("expires_at"), rs.getTimestamp("created_at"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding OTP code", e);
        }
        return null;
    }

    public void updateStatus(int id, OtpStatus status) {
        String sql = "UPDATE otp_codes SET status = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating OTP status", e);
        }
    }

    public void expireOutdatedCodes() {
        String sql = "UPDATE otp_codes SET status = 'EXPIRED' WHERE status = 'ACTIVE' AND expires_at < CURRENT_TIMESTAMP";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            int updated = stmt.executeUpdate(sql);
            if (updated > 0) {
                System.out.println("Scheduler: Marked " + updated + " codes as EXPIRED.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error expiring codes", e);
        }
    }
}
