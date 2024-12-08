package com.example.SercuitywithRest.Service;

import com.example.SercuitywithRest.Model.UserModel;
import com.example.SercuitywithRest.Repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  public static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private UserRepository userRepository;
  /**
   * Create a new user with encrypted password.
   */
  public UserModel createUser(UserModel userModel) {
    userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
    LOGGER.info("User created successfully with email: {}", userModel.getEmail());
    return userRepository.save(userModel);
  }

  /**
   * Login a user and authenticate them.
   */
  public UserModel login(String email, String password) throws Exception {
    Optional<UserModel> userOptional = userRepository.findByEmail(email);
    if (userOptional.isPresent()) {
      UserModel user = userOptional.get();
      if (passwordEncoder.matches(password, user.getPassword())) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user, null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        LOGGER.info("User logged in successfully: {}", email);
        return user;
      } else {
        throw new Exception("Invalid Credentials");
      }
    } else {
      throw new Exception("User not found");
    }
  }

  /**
   * Retrieve all users.
   */
  public List<UserModel> getAllUser() {
    LOGGER.info("Fetching all users");
    return userRepository.findAll();
  }

  /**
   * Retrieve user by ID.
   */
  public Optional<UserModel> getById(Long id) {
    LOGGER.info("Fetching user with ID: {}", id);
    return userRepository.findById(id);
  }

  /**
   * Update the role of a user by their ID.
   */
  public UserModel updateUserRole(Long id, String newRole) throws Exception {
    Optional<UserModel> userOptional = userRepository.findById(id);
    if (userOptional.isPresent()) {
      UserModel user = userOptional.get();
      user.setRole(newRole);
      LOGGER.info("User role updated successfully for ID: {}, new role: {}", id, newRole);
      return userRepository.save(user);
    } else {
      LOGGER.error("User not found with ID: {}", id);
      throw new Exception("User not found");
    }
  }

  /**
   * Logout the current user.
   */
  public void logout() {
    SecurityContextHolder.clearContext();
    LOGGER.info("User logged out successfully");
  }
}
