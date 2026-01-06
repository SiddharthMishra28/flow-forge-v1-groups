package com.testautomation.orchestrator.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class FlowGroupDetailsDto {

    private Map<String, List<FlowSummaryDto>> flowGroups;

    // Constructors
    public FlowGroupDetailsDto() {}

    public FlowGroupDetailsDto(Map<String, List<FlowSummaryDto>> flowGroups) {
        this.flowGroups = flowGroups;
    }

    // Getters and Setters
    public Map<String, List<FlowSummaryDto>> getFlowGroups() {
        return flowGroups;
    }

    public void setFlowGroups(Map<String, List<FlowSummaryDto>> flowGroups) {
        this.flowGroups = flowGroups;
    }

    public static class FlowSummaryDto {
        private Long flowId;
        private String squashTestCase;
        private Integer squashTestCaseId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public FlowSummaryDto() {}

        public FlowSummaryDto(Long flowId, String squashTestCase, Integer squashTestCaseId, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.flowId = flowId;
            this.squashTestCase = squashTestCase;
            this.squashTestCaseId = squashTestCaseId;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        // Getters and Setters
        public Long getFlowId() {
            return flowId;
        }

        public void setFlowId(Long flowId) {
            this.flowId = flowId;
        }

        public String getSquashTestCase() {
            return squashTestCase;
        }

        public void setSquashTestCase(String squashTestCase) {
            this.squashTestCase = squashTestCase;
        }

        public Integer getSquashTestCaseId() {
            return squashTestCaseId;
        }

        public void setSquashTestCaseId(Integer squashTestCaseId) {
            this.squashTestCaseId = squashTestCaseId;
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
}