package com.example.SercuitywithRest.Controller;

import com.example.SercuitywithRest.Model.UserModel;
import com.example.SercuitywithRest.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/login/api")
public class UserControllerApi {
  @Autowired private UserService userService;

  @Operation(summary = "Create a new user")
  @PostMapping("/create")
  public ResponseEntity<String> createUser(@RequestBody UserModel user) {
    try {
      userService.createUser(user);
      return ResponseEntity.ok("User created successfully");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Error creating user: " + e.getMessage());
    }
  }

  @Operation(summary = "Login a user")
  @PostMapping("/userLogin")
  public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
    try {
      userService.login(email, password);
      return ResponseEntity.ok("Login successful");
    } catch (Exception e) {
      return ResponseEntity.status(401).body("Login failed: " + e.getMessage());
    }
  }

  @Operation(summary = "Get user by ID")
  @GetMapping("/user/{id}")
  public ResponseEntity<?> getUserById(@PathVariable long id) {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (principal instanceof UserModel) {
      UserModel loggedInUser = (UserModel) principal;

      if (loggedInUser.getId().equals(id) || "ADMIN".equals(loggedInUser.getRole())) {
        Optional<UserModel> user = userService.getById(id);
        if (user.isPresent()) {
          return ResponseEntity.ok(user.get());
        } else {
          return ResponseEntity.status(404).body("User not found");
        }
      } else {
        return ResponseEntity.status(403).body("Access denied");
      }
    } else {
      return ResponseEntity.status(401).body("Access denied. Please log in.");
    }
  }

  @Operation(summary = "Update user role")
  @PutMapping("/user/{id}/role")
  public ResponseEntity<String> updateUserRole(@PathVariable long id, @RequestParam String role) {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserModel) {
      UserModel loggedInUser = (UserModel) principal;

      // Ensure the user is an admin before updating the role
      if ("ADMIN".equals(loggedInUser.getRole())) {
        // Validate the role
        if (role.equals("ADMIN") || role.equals("USER")) {
          try {
            userService.updateUserRole(id, role);
            return ResponseEntity.ok("User role updated successfully");
          } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating role: " + e.getMessage());
          }
        } else {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                  .body("Invalid role provided");
        }
      } else {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied");
      }
    } else {
      return ResponseEntity.status( HttpStatus.UNAUTHORIZED)
              .body("Access denied. Please log in.");
    }
  }

}
