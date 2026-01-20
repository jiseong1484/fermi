
package com.fermi.signaling.api.queue;

public class JoinQueueRequest {
    private String customerId; // Unique identifier for the customer

    // Default constructor for JSON deserialization
    public JoinQueueRequest() {
    }

    public JoinQueueRequest(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
