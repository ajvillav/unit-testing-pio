package com.loan.payment.transformer;


import com.loan.payment.dto.UserDto;
import com.loan.payment.model.User;

import java.util.List;
import java.util.stream.Collectors;

public  class UserTransformer {

    public static UserDto transformUserToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUserDtoId(user.getUserId());
        userDto.setUserDtoName(user.getUserName());
        return userDto;
    }

    public static List<UserDto> transformUserListToDto(List<User> users) {
        return users.stream()
                .map(UserTransformer::transformUserToDto)
                .collect(Collectors.toList());
    }

    public static User transformDtoToUser(UserDto userDto) {
        User user = new User();
        user.setUserId(userDto.getUserDtoId());
        user.setUserName(userDto.getUserDtoName());
        return user;
    }
}
