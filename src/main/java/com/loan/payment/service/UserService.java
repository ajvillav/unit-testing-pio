package com.loan.payment.service;

import com.loan.payment.dto.UserDto;
import com.loan.payment.model.User;
import com.loan.payment.repository.UsersRepository;
import com.loan.payment.transformer.UserTransformer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UsersRepository usersRepository;

    public UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public List<User> getAllUsers() {
        return usersRepository.findAll();
    }

    public List<UserDto> getUserDtoList() throws Exception {
        List<User> userList = usersRepository.findAll();
        if(userList.isEmpty()) {
            throw new Exception("No users found");
        }
        return UserTransformer.transformUserListToDto(userList);
    }

    public UserDto createUser(UserDto userdto) throws Exception {
        if(userdto.getUserDtoId() == null || userdto.getUserDtoName() == null || userdto.getUserDtoName().isEmpty()) {
            throw new Exception("User id or user name invalid!");
        }

        User userToSave = UserTransformer.transformDtoToUser(userdto);
        userToSave.setUserAccount(UUID.randomUUID());

        return UserTransformer.transformUserToDto(usersRepository.save(userToSave));
    }
}
