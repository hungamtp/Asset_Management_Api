package com.nashtech.rootkies.controllers;

import com.nashtech.rootkies.constants.SuccessCode;
import com.nashtech.rootkies.converter.AssetConverter;
import com.nashtech.rootkies.dto.asset.request.CreateAssetRequestDTO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import com.nashtech.rootkies.converter.LocationConverter;
import com.nashtech.rootkies.dto.asset.request.EditAssetRequest;
import com.nashtech.rootkies.dto.asset.request.SearchFilterSortAssetDTO;
import com.nashtech.rootkies.dto.asset.response.EditAssetDTO;
import com.nashtech.rootkies.dto.common.ResponseDTO;
import com.nashtech.rootkies.exception.ConvertEntityDTOException;
import com.nashtech.rootkies.exception.CreateDataFailException;
import com.nashtech.rootkies.exception.DataNotFoundException;
import com.nashtech.rootkies.exception.DeleteDataFailException;
import com.nashtech.rootkies.exception.InvalidRequestDataException;
import com.nashtech.rootkies.model.Asset;
import com.nashtech.rootkies.model.Location;
import com.nashtech.rootkies.security.jwt.JwtUtils;
import com.nashtech.rootkies.service.AssetService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/asset")
public class AssetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetController.class);

    private final AssetService assetService;

    private final AssetConverter assetConverter;

    private final LocationConverter locationConverter;

    private final JwtUtils jwtUtils;

    @Autowired
    public AssetController(AssetService assetService, LocationConverter locationConverter, JwtUtils jwtUtils,
            AssetConverter assetConverter) {
        this.assetService = assetService;
        this.locationConverter = locationConverter;
        this.assetConverter = assetConverter;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> retrieveAssets(HttpServletRequest req,
            @RequestParam(name = "page", required = true) Integer pageNum,
            @RequestParam(name = "size", required = true) Integer numOfItems) throws DataNotFoundException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        return ResponseEntity.ok(assetService
                .retrieveAsset(PageRequest.of(pageNum, numOfItems, Sort.by("assetName").ascending()), locationId));
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> countAsset(HttpServletRequest req) throws DataNotFoundException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        return ResponseEntity.ok(assetService.countAsset(locationId));
    }

    @GetMapping("/{assetCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> retrieveAssetById(HttpServletRequest req,
            @PathVariable("assetCode") String assetCode) throws DataNotFoundException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        return ResponseEntity.ok(assetService.retrieveAssetByAssetCode(locationId, assetCode));

    }

    @PostMapping("/filter-search-sort")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> retrieveAssetHavingFilterSearchSort(HttpServletRequest req,
            @RequestParam(name = "page", required = true) Integer pageNum,
            @RequestParam(name = "size", required = true) Integer numOfItems,
            @RequestBody SearchFilterSortAssetDTO searchFilterSortAssetDTO) throws DataNotFoundException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        return ResponseEntity.ok(assetService.retrieveAssetHavingFilterSearchSort(pageNum, numOfItems,
                searchFilterSortAssetDTO, locationId));
    }

    @PostMapping("/count/filter-search-sort")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> countAssetHavingFilterSearchSort(HttpServletRequest req,
            @RequestBody SearchFilterSortAssetDTO searchFilterSortAssetDTO) throws DataNotFoundException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        return ResponseEntity.ok(assetService.countAssetHavingFilterSearchSort(searchFilterSortAssetDTO, locationId));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> saveAsset(HttpServletRequest req,
            @Valid @RequestBody CreateAssetRequestDTO createAssetRequestDTO) throws ConvertEntityDTOException,
            CreateDataFailException, InvalidRequestDataException, DataNotFoundException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);

        Asset asset = assetConverter.convertCreateAssetDTOToEntity(createAssetRequestDTO, locationId);

        return ResponseEntity.ok(assetService.saveAsset(asset));
    }

    @DeleteMapping("/{assetCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> deleteAssetById(HttpServletRequest req,
            @PathVariable("assetCode") String assetCode) throws DataNotFoundException, DeleteDataFailException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        return ResponseEntity.ok(assetService.deleteAssetByAssetCode(locationId, assetCode));
    }

    // remeber to research valid only work when input or output
    // edit asset
    @PutMapping("/edit/{id}")
    public ResponseEntity<ResponseDTO> editAsset(@PathVariable("id") String id,
            @RequestBody EditAssetRequest editAssetRequest) {
        EditAssetDTO editAssetDTO = assetService.editAsset(id, editAssetRequest);
        ResponseDTO response = new ResponseDTO();
        response.setData(editAssetDTO);
        response.setSuccessCode(SuccessCode.ASSET_EDIT_SUCCESS);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/check/{assetCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> checkDeleteAssetById(HttpServletRequest req,
            @PathVariable("assetCode") String assetCode) throws DataNotFoundException, DeleteDataFailException {
        String jwt = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Long locationId = locationConverter.getLocationIdFromUsername(username);
        return ResponseEntity.ok(assetService.checkDeleteAssetByAssetCode(locationId, assetCode));
    }
}
