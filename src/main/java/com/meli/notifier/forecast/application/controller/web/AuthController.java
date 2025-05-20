package com.meli.notifier.forecast.application.controller.web;

import com.meli.notifier.forecast.application.dto.TokenDTO;
import com.meli.notifier.forecast.application.dto.request.LoginRequestDTO;
import com.meli.notifier.forecast.application.dto.request.SignInRequestDTO;
import com.meli.notifier.forecast.domain.mapper.UserMapper;
import com.meli.notifier.forecast.domain.service.AuthService;
import com.meli.notifier.forecast.domain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "User login", description = "Authenticate with email and password to get an access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = TokenDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed")})
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginRequestDTO loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getEmail());
        TokenDTO token = authService.login(loginRequest);
        return ResponseEntity.ok(token);
    }
    
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User created successfully. No content returned"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "User already exists with the provided email or phone")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid SignInRequestDTO request) {
        log.info("Registering new user with email: {}", request.getEmail());
        userService.createUser(userMapper.toModel(request));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
