package com.nashtech.rootkies.controllers;

import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.constants.SuccessCode;
import com.nashtech.rootkies.converter.UserConverter;
import com.nashtech.rootkies.dto.auth.JwtResponse;
import com.nashtech.rootkies.dto.common.ResponseDTO;
import com.nashtech.rootkies.dto.user.UserDTO;
import com.nashtech.rootkies.dto.user.request.EditUserDTO;
import com.nashtech.rootkies.dto.user.request.PasswordRequest;
import com.nashtech.rootkies.exception.DataNotFoundException;
import com.nashtech.rootkies.exception.UpdateDataFailException;
import com.nashtech.rootkies.exception.UserNotFoundException;
import com.nashtech.rootkies.model.User;
import com.nashtech.rootkies.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
// @Api( tags = "User")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/home")
    public String getHome() {
        return "<h1>USER Home Page</h1>";
    }

    @PutMapping("/password/first")
    public ResponseEntity<ResponseDTO> changePasswordFirstLogin(@RequestBody PasswordRequest passwordRequest){
        JwtResponse response = userService.changePasswordFirstLogin(passwordRequest);
        ResponseDTO dto = new ResponseDTO();
        dto.setData(response);
        dto.setSuccessCode(SuccessCode.CHANGE_PASSWORD_SUCCESS);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("")
    public ResponseEntity<ResponseDTO> getAllUser() throws UserNotFoundException {
        ResponseDTO response = new ResponseDTO();
        List<ResponseDTO> responseDTO = new ArrayList<>();

        List<User> users = userService.retrieveUsers();
        List list = Collections.synchronizedList(new ArrayList(users));

        if (responseDTO.addAll(list) == true) {
            response.setData(userConverter.toListDto(users));
        }
        response.setSuccessCode(SuccessCode.USER_LOADED_SUCCESS);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{staffcode}")
    public ResponseEntity<ResponseDTO> findUser(@PathVariable("staffcode") String staffCode) throws DataNotFoundException {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Optional<User> user = userService.getUser(staffCode);

            responseDTO.setData(userConverter.convertToDto(user.get()));
            responseDTO.setSuccessCode(SuccessCode.FIND_USER_SUCCESS);
        } catch (Exception e){
            throw new DataNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
        }
        return ResponseEntity.ok(responseDTO);
    }


    @PutMapping("/update/{staffcode}")
    public ResponseEntity<ResponseDTO> updateUser(@PathVariable(value = "staffcode") String staffcode,
                                                  @Valid @RequestBody EditUserDTO editUserDTO) throws UpdateDataFailException {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            User user = userConverter.convertEditUserDTOtoEntity(editUserDTO);
            User updateUser = userService.updateUser(staffcode, user);
            responseDTO.setData(userConverter.convertToDto(updateUser));
            responseDTO.setSuccessCode(SuccessCode.USER_UPDATED_SUCCESS);
        } catch (Exception e){
            throw new UpdateDataFailException(ErrorCode.ERR_UPDATE_USER_FAIL);
        }
        return ResponseEntity.ok(responseDTO);
    }


     @Autowired
     UserConverter userConverter;
     private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
}
