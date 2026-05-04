package com.example.model.dto;

public class OtpRequest {
    private String operationId;
    private String deliveryChannel;
    private String destination;

    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }
    public String getDeliveryChannel() { return deliveryChannel; }
    public void setDeliveryChannel(String deliveryChannel) { this.deliveryChannel = deliveryChannel; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
}