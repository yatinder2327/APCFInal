package com.yourcompany.bankmanagement.service;

import com.yourcompany.bankmanagement.data.User;
import java.util.List;

public interface UserServiceInterface {
    User createUser(User user);
    User findByUsername(String username);
    User findByUserId(String userId);
    User findByBankPersonId(String bankPersonId);
    List<User> findByRole(String role);
    List<User> getAllUsers();
    void deleteUser(String userId);
}




