package com.nashtech.rootkies.controllers;

import javax.servlet.http.HttpServletRequest;

import com.nashtech.rootkies.converter.LocationConverter;
import com.nashtech.rootkies.dto.assignment.request.SearchFilterSortAssignmentDTO;
import com.nashtech.rootkies.dto.common.ResponseDTO;
import com.nashtech.rootkies.exception.DataNotFoundException;
import com.nashtech.rootkies.exception.DeleteDataFailException;
import com.nashtech.rootkies.security.jwt.JwtUtils;
import com.nashtech.rootkies.service.AssignmentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/assignment")
public class AssignmentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssignmentController.class);

    private final AssignmentService assignmentService;

    LocationConverter locationConverter;

    private final JwtUtils jwtUtils;

    @Autowired
    public AssignmentController(AssignmentService assignmentService, LocationConverter locationConverter,
            JwtUtils jwtUtils) {
        this.assignmentService = assignmentService;
        this.locationConverter = locationConverter;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> retrieveAssignments(HttpServletRequest req,
            @RequestParam(name = "page", required = true) Integer pageNum,
            @RequestParam(name = "size", required = true) Integer numOfItems) throws DataNotFoundException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        return ResponseEntity.ok(assignmentService.retrieveAssignments(
                PageRequest.of(pageNum, numOfItems, Sort.by("assignedDate").descending()), locationId));
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> countAssignment(HttpServletRequest req) throws DataNotFoundException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        return ResponseEntity.ok(assignmentService.countAssignment(locationId));
    }

    @GetMapping("/{assignmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ResponseDTO> retrieveAssignmentById(HttpServletRequest req,
            @PathVariable("assignmentId") Long assignmentId) throws DataNotFoundException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        return ResponseEntity.ok(assignmentService.retrieveAssignmentByAssignmentId(locationId, assignmentId));

    }

    @PostMapping("/filter-search-sort")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ResponseDTO> retrieveAssignmentHavingFilterSearchSort(HttpServletRequest req,
            @RequestParam(name = "page", required = true) Integer pageNum,
            @RequestParam(name = "size", required = true) Integer numOfItems,
            @RequestBody SearchFilterSortAssignmentDTO searchFilterSortAssignmentDTO) throws DataNotFoundException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        return ResponseEntity.ok(assignmentService.retrieveAssignmentHavingFilterSearchSort(pageNum, numOfItems,
                searchFilterSortAssignmentDTO, locationId));
    }

    @PostMapping("/count/filter-search-sort")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ResponseDTO> countAssignmentHavingFilterSearchSort(HttpServletRequest req,
            @RequestBody SearchFilterSortAssignmentDTO searchFilterSortAssignmentDTO) throws DataNotFoundException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        return ResponseEntity
                .ok(assignmentService.countAssignmentHavingFilterSearchSort(searchFilterSortAssignmentDTO, locationId));
    }

    @DeleteMapping("/{assignmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> deleteAssignmentById(HttpServletRequest req,
            @PathVariable("assignmentId") Long assignmentId) throws DataNotFoundException, DeleteDataFailException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        return ResponseEntity.ok(assignmentService.deleteAssetByAssignmentId(locationId, assignmentId));
    }

}