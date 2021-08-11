package com.nashtech.rootkies.repository;

import java.util.List;

import com.nashtech.rootkies.model.Asset;
import com.nashtech.rootkies.model.Assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    
    List<Assignment> findByAsset(Asset asset);
}