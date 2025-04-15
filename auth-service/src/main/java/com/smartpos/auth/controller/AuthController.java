package com.smartpos.auth.controller;

import com.smartpos.auth.dto.JwtResponse;
import com.smartpos.auth.dto.LoginRequest;
import com.smartpos.auth.dto.MessageResponse;
import com.smartpos.auth.dto.SignupRequest;
import com.smartpos.auth.security.jwt.JwtUtils;
import com.smartpos.auth.security.services.UserDetailsImpl;
import com.smartpos.common.entity.Employee;
import com.smartpos.common.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(item -> item.getAuthority())
                .orElse("");

        return ResponseEntity.ok(new JwtResponse(jwt,
                                                userDetails.getId(), 
                                                userDetails.getEmail(), 
                                                role));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (employeeRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new employee's account
        Employee employee = new Employee();
        employee.setFirstName(signUpRequest.getFirstName());
        employee.setLastName(signUpRequest.getLastName());
        employee.setEmail(signUpRequest.getEmail());
        employee.setPasswordHash(encoder.encode(signUpRequest.getPassword()));
        employee.setRole(signUpRequest.getRole());
        employee.setIsActive(true);

        employeeRepository.save(employee);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
