package com.globe.paymybuddy.controllers;


import com.globe.paymybuddy.dtos.UserRegisterRequestDto;
import com.globe.paymybuddy.dtos.UserRegisterResponseDto;
import com.globe.paymybuddy.services.UserAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    private final UserAdminService userAdminService;
    @Autowired
    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userAdminService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public UserRegisterResponseDto updateUser(@PathVariable long id, @RequestBody UserRegisterRequestDto userDto) {
        return userAdminService.updateUser(id, userDto);
    }


}
