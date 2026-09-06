package com.ecommerce.user.controllers;


import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    //private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        //return ResponseEntity.ok(userService.getAllUsers()) ; One way of defining ResponseEntity.

        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);

    }

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest){
         userService.addUser(userRequest);
         return ResponseEntity.ok("User Added Successfully");
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable String id, @RequestBody UserRequest userRequest){
        boolean isUpdated = userService.updateUser(id, userRequest);
        if(isUpdated){
            return ResponseEntity.ok("User updated successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id){

        log.info("Request received for user : {}", id);

        log.trace("This is trace level - very detailed logs");
        log.debug("This is debug level - Used for development debugging");
        log.warn("This is warning level - Something might be wrong but not that much of priority");
        log.error("This is error level - Something failed");
        log.info("This is info level - General System Information");

        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
//        if(user == null){
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(user);
    }
}
