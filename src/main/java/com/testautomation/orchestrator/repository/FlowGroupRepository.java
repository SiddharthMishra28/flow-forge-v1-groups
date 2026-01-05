package com.testautomation.orchestrator.repository;

import com.testautomation.orchestrator.model.FlowGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowGroupRepository extends JpaRepository<FlowGroup, Long> {
}