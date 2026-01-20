
package com.fermi.signaling.api.matching;

public class MatchNextRequest {
    private String agentId;

    // Default constructor for JSON deserialization
    public MatchNextRequest() {
    }

    public MatchNextRequest(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
}
