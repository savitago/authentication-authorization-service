package com.example.demo.controller;


import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserWithOutPassword;
import com.example.demo.fegin.UserFeign;
import com.example.demo.model.JWTRequest;
import com.example.demo.model.JWTResponse;
import com.example.demo.repo.AuthorisationRepo;
import com.example.demo.service.UserService;
import com.example.demo.utility.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtility jwtUtility;

    @Autowired
    private UserService userService;

    @Autowired
    UserFeign userFeign;

    @Autowired
    AuthorisationRepo authorisationRepo;

    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@RequestBody LoginRequest loginRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new Exception("Invalid credentials", ex);
        }

        final UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());
        final String token = jwtUtility.generateToken(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(new JWTResponse(token,userFeign.getUserDetailsByEmail(loginRequest.getEmail())));
    }

    @PostMapping("/signup")
    public ResponseEntity<JWTResponse> signup(@RequestBody UserDto userDto) throws Exception {
        JWTRequest jwtRequest = new JWTRequest();
        jwtRequest.setEmail(userDto.getEmail());
        jwtRequest.setPassword(userDto.getPassword());
        authorisationRepo.save(jwtRequest);
        UserWithOutPassword userWithOutPassword = userFeign.createUser(userDto);
        final UserDetails userDetails = new User(userDto.getEmail(),userDto.getPassword(), new ArrayList<>());
        final String token = jwtUtility.generateToken(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(new JWTResponse(token,userWithOutPassword));
    }
}