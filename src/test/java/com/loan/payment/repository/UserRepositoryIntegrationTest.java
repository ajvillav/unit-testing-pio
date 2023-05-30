package com.loan.payment.repository;

import com.loan.payment.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    UsersRepository usersRepository;

    @Test
    void whenFindByEmailThenReturnUser() {
        //Given
        User user = new User();
        user.setUserAccount(UUID.randomUUID());
        user.setUserName("Andres Villa");
        user.setUserId(1020486384);
        testEntityManager.persist(user);
        testEntityManager.flush();

        //When
        Optional<User> foundUser = usersRepository.findById(user.getUserId());

        //Then
        assertNotNull(foundUser);
        assertAll(
                () -> assertEquals(foundUser.get().getUserId(), user.getUserId()),
                () -> assertEquals(foundUser.get().getUserName(), user.getUserName()),
                () -> assertEquals(foundUser.get().getUserAccount(), user.getUserAccount())
        );
    }


}
