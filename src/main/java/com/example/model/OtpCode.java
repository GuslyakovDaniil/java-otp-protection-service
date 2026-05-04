package com.example.model;

import java.sql.Timestamp;

public class OtpCode {
    private int id;
    private int userId;
    private String operationId;
    private String code;
    private OtpStatus status;
    private Timestamp expiresAt;
    private Timestamp createdAt;

    public OtpCode() {}

    public OtpCode(int id, int userId, String operationId, String code, OtpStatus status, Timestamp expiresAt, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.operationId = operationId;
        this.code = code;
        this.status = status;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public OtpStatus getStatus() { return status; }
    public void setStatus(OtpStatus status) { this.status = status; }
    public Timestamp getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Timestamp expiresAt) { this.expiresAt = expiresAt; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}