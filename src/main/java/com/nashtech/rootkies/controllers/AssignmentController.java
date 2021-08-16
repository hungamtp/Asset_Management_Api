package com.nashtech.rootkies.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.constants.SuccessCode;
import com.nashtech.rootkies.converter.AssignmentConverter;
import com.nashtech.rootkies.converter.LocationConverter;
import com.nashtech.rootkies.dto.assignment.request.CreateAssignmentDTO;
import com.nashtech.rootkies.converter.AssignmentConverter;
import com.nashtech.rootkies.converter.LocationConverter;
import com.nashtech.rootkies.dto.assignment.request.EditAssignmentDTO;
import com.nashtech.rootkies.dto.assignment.request.SearchFilterSortAssignmentDTO;
import com.nashtech.rootkies.dto.assignment.response.ViewAssignmentDTO;
import com.nashtech.rootkies.dto.common.ResponseDTO;
import com.nashtech.rootkies.exception.DataNotFoundException;
import com.nashtech.rootkies.exception.DeleteDataFailException;
import com.nashtech.rootkies.exception.InvalidRequestDataException;
import com.nashtech.rootkies.exception.UpdateDataFailException;
import com.nashtech.rootkies.model.Assignment;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/assignment")
@Tag(name = "ASSIGNMENT", description = "ASSIGNMENT API")
public class AssignmentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssignmentController.class);

    private final AssignmentService assignmentService;

    private final LocationConverter locationConverter;

    private final JwtUtils jwtUtils;

    private final AssignmentConverter assignmentConverter;

    @Autowired
    public AssignmentController(AssignmentService assignmentService, LocationConverter locationConverter,
            JwtUtils jwtUtils, AssignmentConverter assignmentConverter) {
        this.assignmentService = assignmentService;
        this.locationConverter = locationConverter;
        this.jwtUtils = jwtUtils;
        this.assignmentConverter = assignmentConverter;
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

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> createAssignment(@Valid @RequestBody CreateAssignmentDTO dto){

        ResponseDTO response = new ResponseDTO();
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            if(!(LocalDateTime.parse(dto.getAssignedDate() , formatter).toLocalDate().isEqual(LocalDate.now()) ||
                    LocalDateTime.parse(dto.getAssignedDate() , formatter).toLocalDate().isAfter(LocalDate.now()))){
                response.setErrorCode(ErrorCode.ERR_ASSIGNED_DATE_IN_PAST);
                return ResponseEntity.badRequest().body(response);
            }

            Assignment assignment= assignmentService.createAssignment(dto);
            response.setSuccessCode(SuccessCode.CREATE_ASSIGNMENT_SUCCESS);
            return ResponseEntity.ok().body(response);

        }catch (Exception ex){

            response.setErrorCode(ErrorCode.ERR_CREATE_ASSIGNMENT);
            return ResponseEntity.badRequest().body(response);
        }

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

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> editAssignment(HttpServletRequest req,
            @Valid @RequestBody EditAssignmentDTO EditAssignmentDTO)
            throws DataNotFoundException, InvalidRequestDataException, UpdateDataFailException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        Assignment assignment = assignmentConverter.convertEditAssignmentDTOToEntity(locationId, EditAssignmentDTO,
                username);
        return ResponseEntity.ok(assignmentService.editAssignment(assignment, EditAssignmentDTO.getAssetCode()));
    }

    @PutMapping("/accept/{assignmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ResponseDTO> acceptAssignment(HttpServletRequest req,
            @PathVariable("assignmentId") Long assignmentId)
            throws DataNotFoundException, InvalidRequestDataException, UpdateDataFailException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        return ResponseEntity.ok(assignmentService.acceptAssignment(locationId, assignmentId , username));
    }

    @PutMapping("/decline/{assignmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ResponseDTO> declineAssignment(HttpServletRequest req,
            @PathVariable("assignmentId") Long assignmentId)
            throws DataNotFoundException, InvalidRequestDataException, UpdateDataFailException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        return ResponseEntity.ok(assignmentService.declineAssignment(locationId, assignmentId , username));
    }
}
