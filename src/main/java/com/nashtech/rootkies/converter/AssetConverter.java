package com.nashtech.rootkies.converter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.constants.State;
import com.nashtech.rootkies.dto.asset.request.CreateAssetRequestDTO;
import com.nashtech.rootkies.dto.asset.response.CreateAssetResponseDTO;
import com.nashtech.rootkies.dto.asset.response.DetailAssetDTO;
import com.nashtech.rootkies.dto.asset.response.ViewAssetDTO;
import com.nashtech.rootkies.exception.ConvertEntityDTOException;
import com.nashtech.rootkies.exception.InvalidRequestDataException;
import com.nashtech.rootkies.model.Asset;
import com.nashtech.rootkies.model.Category;
import com.nashtech.rootkies.model.Location;
import com.nashtech.rootkies.repository.CategoryRepository;
import com.nashtech.rootkies.repository.LocationRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class AssetConverter {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    LocationRepository locationRepository;

    public DetailAssetDTO convertToDetailDTO(Asset asset) throws ConvertEntityDTOException {
        try {
            return modelMapper.map(asset, DetailAssetDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_DTO_ENTITY_FAIL);
        }
    }

    public List<ViewAssetDTO> convertToListDTO(Page<Asset> assets) throws ConvertEntityDTOException {
        try {
            return assets.stream().map(asset -> modelMapper.map(asset, ViewAssetDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_DTO_ENTITY_FAIL);
        }
    }

    public List<ViewAssetDTO> convertToListDTO(List<Asset> assets) throws ConvertEntityDTOException {
        try {
            return assets.stream().map(asset -> modelMapper.map(asset, ViewAssetDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_DTO_ENTITY_FAIL);
        }
    }

    public Asset convertCreateAssetDTOToEntity(CreateAssetRequestDTO createAssetRequestDTO, Long locationId)
            throws ConvertEntityDTOException, InvalidRequestDataException {

        if (createAssetRequestDTO.getState() != State.AVAILABLE
                && createAssetRequestDTO.getState() != State.NOT_AVAILABLE) {
            throw new InvalidRequestDataException(ErrorCode.ERR_ASSET_STATE_NOT_CORRECT);
        }

        Optional<Category> category = categoryRepository.findById(createAssetRequestDTO.getCategoryCode());
        if (!category.isPresent()) {
            throw new InvalidRequestDataException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
        }

        Optional<Location> location = locationRepository.findById(locationId);
        if (!location.isPresent()) {
            throw new InvalidRequestDataException(ErrorCode.ERR_LOCATION_NOT_FAIL);
        }

        Asset asset;
        try {
            asset = modelMapper.map(createAssetRequestDTO, Asset.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_DTO_ENTITY_FAIL);
        }
        asset.setCategory(category.get());

        asset.setLocation(location.get());

        asset.setIsDeleted(false);

        return asset;

    }

    public CreateAssetResponseDTO convertToCreateResponseDTO(Asset asset) throws ConvertEntityDTOException {
        try {
            return modelMapper.map(asset, CreateAssetResponseDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_DTO_ENTITY_FAIL);
        }
    }
}