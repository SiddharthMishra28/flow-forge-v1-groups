package com.testautomation.orchestrator.service;

import com.testautomation.orchestrator.dto.FlowExecutionDto;
import com.testautomation.orchestrator.dto.FlowGroupDto;
import com.testautomation.orchestrator.model.FlowGroup;
import com.testautomation.orchestrator.repository.FlowGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlowGroupService {

    private static final Logger logger = LoggerFactory.getLogger(FlowGroupService.class);

    @Autowired
    private FlowGroupRepository flowGroupRepository;

    @Autowired
    private FlowExecutionService flowExecutionService;

    public FlowGroupDto createFlowGroup(FlowGroupDto flowGroupDto) {
        logger.info("Creating new flow group: {}", flowGroupDto.getFlowGroupName());

        FlowGroup flowGroup = new FlowGroup(flowGroupDto.getFlowGroupName(), flowGroupDto.getFlows());
        FlowGroup saved = flowGroupRepository.save(flowGroup);

        return convertToDto(saved);
    }

    public Optional<FlowGroupDto> getFlowGroupById(Long id) {
        logger.debug("Fetching flow group with ID: {}", id);

        return flowGroupRepository.findById(id)
                .map(this::convertToDto);
    }

    public Page<FlowGroupDto> getAllFlowGroups(Pageable pageable) {
        logger.debug("Fetching all flow groups with pagination");

        return flowGroupRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    public FlowGroupDto updateFlowGroup(Long id, FlowGroupDto flowGroupDto) {
        logger.info("Updating flow group with ID: {}", id);

        FlowGroup existing = flowGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FlowGroup not found with ID: " + id));

        existing.setFlowGroupName(flowGroupDto.getFlowGroupName());
        existing.setFlows(flowGroupDto.getFlows());

        FlowGroup updated = flowGroupRepository.save(existing);
        return convertToDto(updated);
    }

    public void deleteFlowGroup(Long id) {
        logger.info("Deleting flow group with ID: {}", id);

        if (!flowGroupRepository.existsById(id)) {
            throw new IllegalArgumentException("FlowGroup not found with ID: " + id);
        }

        flowGroupRepository.deleteById(id);
    }

    public Map<String, Object> executeFlowGroup(Long flowGroupId) {
        logger.info("Executing flow group with ID: {}", flowGroupId);

        FlowGroup flowGroup = flowGroupRepository.findById(flowGroupId)
                .orElseThrow(() -> new IllegalArgumentException("FlowGroup not found with ID: " + flowGroupId));

        List<Long> flowIds = flowGroup.getFlows();
        if (flowIds == null || flowIds.isEmpty()) {
            throw new IllegalArgumentException("FlowGroup has no flows to execute");
        }

        String flowIdsStr = flowIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        Map<String, Object> result = flowExecutionService.executeMultipleFlows(flowIdsStr);

        // Start async execution for all accepted flows - this happens after we have the response ready
        @SuppressWarnings("unchecked")
        List<FlowExecutionDto> acceptedExecutions = (List<FlowExecutionDto>) result.get("accepted");
        logger.info("FlowGroup execution created {} accepted flow executions", acceptedExecutions != null ? acceptedExecutions.size() : 0);
        if (acceptedExecutions != null) {
            for (FlowExecutionDto executionDto : acceptedExecutions) {
                flowExecutionService.executeFlowAsync(executionDto.getId());
                logger.info("Started async execution for flow ID: {} with execution ID: {}",
                           executionDto.getFlowId(), executionDto.getId());
            }
        }

        return result;
    }

    private FlowGroupDto convertToDto(FlowGroup flowGroup) {
        return new FlowGroupDto(
                flowGroup.getId(),
                flowGroup.getFlowGroupName(),
                flowGroup.getFlows(),
                flowGroup.getCreatedAt(),
                flowGroup.getUpdatedAt()
        );
    }
}