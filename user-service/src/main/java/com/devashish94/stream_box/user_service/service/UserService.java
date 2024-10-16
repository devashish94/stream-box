package com.devashish94.stream_box.user_service.service;

import com.devashish94.stream_box.user_service.dto.UserDto;
import com.devashish94.stream_box.user_service.entity.User;
import com.devashish94.stream_box.user_service.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public User createUser(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        user.setChannelName(generateChannelName(user.getGivenName(), user.getFamilyName()));
        user.setSubscriberCount(0L);
        user = userRepository.save(user);
        System.out.println(userDto + " " + user);
        return user;
    }

    private String generateChannelName(String giveName, String familyName) {
        return giveName + " " + familyName;
    }
}
