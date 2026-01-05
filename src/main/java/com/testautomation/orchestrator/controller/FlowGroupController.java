package com.testautomation.orchestrator.controller;

import com.testautomation.orchestrator.dto.FlowGroupDto;
import com.testautomation.orchestrator.service.FlowGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/flow-groups")
@Tag(name = "Flow Groups", description = "Flow Group Management API")
public class FlowGroupController {

    private static final Logger logger = LoggerFactory.getLogger(FlowGroupController.class);

    @Autowired
    private FlowGroupService flowGroupService;

    @PostMapping
    @Operation(summary = "Create a new flow group", description = "Create a new flow group with associated flows")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Flow group created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<FlowGroupDto> createFlowGroup(
            @Valid @RequestBody FlowGroupDto flowGroupDto) {
        logger.info("Creating new flow group: {}", flowGroupDto.getFlowGroupName());

        try {
            FlowGroupDto created = flowGroupService.createFlowGroup(flowGroupDto);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to create flow group: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get flow group by ID", description = "Retrieve a specific flow group by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flow group found"),
            @ApiResponse(responseCode = "404", description = "Flow group not found")
    })
    public ResponseEntity<FlowGroupDto> getFlowGroupById(
            @Parameter(description = "Flow group ID") @PathVariable Long id) {
        logger.debug("Fetching flow group with ID: {}", id);

        return flowGroupService.getFlowGroupById(id)
                .map(flowGroup -> ResponseEntity.ok(flowGroup))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all flow groups", description = "Retrieve all flow groups with pagination and sorting")
    @ApiResponse(responseCode = "200", description = "Flow groups retrieved successfully")
    public ResponseEntity<Page<FlowGroupDto>> getAllFlowGroups(
            @Parameter(description = "Page number (0-based)") @RequestParam(required = false) Integer page,
            @Parameter(description = "Page size") @RequestParam(required = false) Integer size,
            @Parameter(description = "Sort by field (e.g., 'id', 'flowGroupName', 'createdAt', 'updatedAt')") @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)") @RequestParam(required = false) String sortDirection) {

        logger.debug("Fetching flow groups with page: {}, size: {}, sortBy: {}, sortDirection: {}",
                    page, size, sortBy, sortDirection);

        // If pagination parameters are provided, use pagination
        if (page != null || size != null) {
            int pageNumber = page != null ? page : 0;
            int pageSize = size != null ? size : 20; // default page size

            Sort sort;
            if (sortBy != null && !sortBy.trim().isEmpty()) {
                Sort.Direction direction = sortDirection != null ?
                    Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
                sort = Sort.by(direction, sortBy);
            } else {
                sort = Sort.by(Sort.Direction.DESC, "updatedAt");
            }

            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
            Page<FlowGroupDto> flowGroupsPage = flowGroupService.getAllFlowGroups(pageable);

            return ResponseEntity.ok(flowGroupsPage);
        } else {
            // Return all data without pagination (backward compatibility)
            Sort sort;
            if (sortBy != null && !sortBy.trim().isEmpty()) {
                Sort.Direction direction = sortDirection != null ?
                    Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
                sort = Sort.by(direction, sortBy);
            } else {
                sort = Sort.by(Sort.Direction.DESC, "updatedAt");
            }

            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
            Page<FlowGroupDto> flowGroupsPage = flowGroupService.getAllFlowGroups(pageable);

            return ResponseEntity.ok(flowGroupsPage);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update flow group", description = "Update an existing flow group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flow group updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Flow group not found")
    })
    public ResponseEntity<FlowGroupDto> updateFlowGroup(
            @Parameter(description = "Flow group ID") @PathVariable Long id,
            @Valid @RequestBody FlowGroupDto flowGroupDto) {
        logger.info("Updating flow group with ID: {}", id);

        try {
            FlowGroupDto updated = flowGroupService.updateFlowGroup(id, flowGroupDto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to update flow group: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete flow group", description = "Delete a flow group by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Flow group deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Flow group not found")
    })
    public ResponseEntity<Void> deleteFlowGroup(
            @Parameter(description = "Flow group ID") @PathVariable Long id) {
        logger.info("Deleting flow group with ID: {}", id);

        try {
            flowGroupService.deleteFlowGroup(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Failed to delete flow group: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{flowGroupId}/execute")
    @Operation(summary = "Execute a flow group", description = "Execute all flows in the specified flow group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Flow group execution started successfully"),
            @ApiResponse(responseCode = "404", description = "Flow group not found"),
            @ApiResponse(responseCode = "400", description = "Invalid flow group or no flows to execute"),
            @ApiResponse(responseCode = "503", description = "Thread pool at capacity - some flows rejected")
    })
    public ResponseEntity<?> executeFlowGroup(
            @Parameter(description = "Flow group ID to execute") @PathVariable Long flowGroupId) {
        logger.info("Executing flow group with ID: {}", flowGroupId);

        try {
            Map<String, Object> result = flowGroupService.executeFlowGroup(flowGroupId);
            return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to execute flow group: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logger.error("Unexpected error executing flow group: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}