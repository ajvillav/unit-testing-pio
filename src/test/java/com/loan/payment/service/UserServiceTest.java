package com.loan.payment.service;

import com.loan.payment.model.User;
import com.loan.payment.repository.UsersRepository;
import com.loan.payment.service.exception.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("User Service Unit Tests")
public class UserServiceTest {

    private UsersRepository usersRepository;
    private UserService userService;

    @BeforeEach
    void init() {
        usersRepository = mock(UsersRepository.class);
        userService = new UserService(usersRepository);
    }

    @Test
    @DisplayName("deleteUser - Test method works properly")
    void deleteUser() throws UserException {
        //Given
        User user = new User();
        user.setUserId(1030456895);
        user.setUserName("Andres Villa");
        user.setUserAccount(UUID.randomUUID());

        when(usersRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        //When
        userService.deleteUser(user.getUserId());

        //Then
        verify(usersRepository, times(1)).delete(userCaptor.capture());

        assertEquals(user, userCaptor.getValue());
    }



}
