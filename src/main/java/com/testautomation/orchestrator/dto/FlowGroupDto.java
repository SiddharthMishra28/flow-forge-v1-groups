package com.testautomation.orchestrator.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FlowGroupDto {

    private Long id;
    private String flowGroupName;
    private List<Long> flows;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public FlowGroupDto() {}

    public FlowGroupDto(Long id, String flowGroupName, List<Long> flows, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.flowGroupName = flowGroupName;
        this.flows = flows;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlowGroupName() {
        return flowGroupName;
    }

    public void setFlowGroupName(String flowGroupName) {
        this.flowGroupName = flowGroupName;
    }

    public List<Long> getFlows() {
        return flows;
    }

    public void setFlows(List<Long> flows) {
        this.flows = flows;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}