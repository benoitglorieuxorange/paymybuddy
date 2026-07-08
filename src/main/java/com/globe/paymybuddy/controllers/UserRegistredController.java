package com.globe.paymybuddy.controllers;


import com.globe.paymybuddy.dtos.UserRegisterResponseDto;
import com.globe.paymybuddy.services.UserAdminService;
import com.globe.paymybuddy.services.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loged")
public class UserRegistredController {

    private final UserAdminService userAdminService;

    public UserRegistredController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }


    @GetMapping("/users")
    public ResponseEntity<List<UserRegisterResponseDto>> getAllUsers() {
        List<UserRegisterResponseDto> users = userAdminService.getAll();
        return ResponseEntity.ok(users);
    }

}