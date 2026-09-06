package com.ecommerce.user.service;


import com.ecommerce.user.dto.AddressDTO;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.entities.Address;
import com.ecommerce.user.entities.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


   // private final List<User> users = new ArrayList<>();

   // private Long id = 0L;

    public List<UserResponse> getAllUsers(){

        List<UserResponse> userResponses = userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .toList();

        return userResponses;
    }

    public void addUser(UserRequest userRequest){
//        user.setId(++id);
//        users.add(user);
        User user = new User();
        updateUserFromRequest(user, userRequest);
        userRepository.save(user);

    }

    public Optional<UserResponse> getUserById(String id){
//        return users.stream()
//                .filter(u -> u.getId().equals(id))
//                .findFirst();
       return userRepository.findById(id)
               .map(this::mapToUserResponse);
    }

    public boolean updateUser(String id, UserRequest userRequest){
//        return users.stream()
//                .filter(u -> u.getId().equals(id))
//                .findFirst()
//                .map(existingUser -> {
//                    existingUser.setLastName(user.getLastName());
//                    existingUser.setFirstName(user.getFirstName());
//                    return true;
//                })
//                .orElse(false);

        return userRepository.findById(id)
                .map(existingUser -> {
                    updateUserFromRequest(existingUser, userRequest);
                    userRepository.save(existingUser);
                    return true;
                })
                .orElse(false);
    }

    private UserResponse mapToUserResponse(User user){
        UserResponse response = new UserResponse();

        response.setId(String.valueOf(user.getId()));
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        if(user.getAddress() != null){
            AddressDTO address = new AddressDTO();
            address.setStreet(user.getAddress().getStreet());
            address.setCity(user.getAddress().getCity());
            address.setState(user.getAddress().getState());
            address.setCountry(user.getAddress().getCountry());
            address.setZipcode(user.getAddress().getZipcode());
            response.setAddress(address);
        }

        return response;
    }

    private void updateUserFromRequest(User user, UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());

        if(userRequest.getAddress() != null){
            Address address = new Address();
            address.setStreet(userRequest.getAddress().getStreet());
            address.setCity(userRequest.getAddress().getCity());
            address.setState(userRequest.getAddress().getState());
            address.setCountry(userRequest.getAddress().getCountry());
            address.setZipcode(userRequest.getAddress().getZipcode());
            user.setAddress(address);
        }
    }
}
