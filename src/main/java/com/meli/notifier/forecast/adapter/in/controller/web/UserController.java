package com.meli.notifier.forecast.adapter.in.controller.web;

import com.meli.notifier.forecast.application.port.in.AuthContextService;
import com.meli.notifier.forecast.application.port.in.UserService;
import com.meli.notifier.forecast.domain.dto.request.OptInRequestDTO;
import com.meli.notifier.forecast.domain.model.database.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@Tag(name = "Users", description = "User management endpoints")
@RequiredArgsConstructor
public class UserController {

    private final AuthContextService authContextService;
    private final UserService userService;

    @Operation(summary = "Update opt-in status",
            description = "Updates the opt-in status of the current authenticated user. When a request body is provided, sets the status to the specified value. When no body is provided, toggles the current status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Opt-in status updated successfully",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication")
    })
    @PutMapping("/optin")
    public ResponseEntity<Boolean> updateOptinStatus(@RequestBody @Valid OptInRequestDTO request) {
        User user = authContextService.getCurrentUser();

        return ResponseEntity.ok(userService.setOptInStatus(user, request.getOptIn()));
    }
}

