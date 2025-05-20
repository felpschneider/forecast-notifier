package com.meli.notifier.forecast.application.controller.web;

import com.meli.notifier.forecast.application.dto.request.SubscriptionRequestDTO;
import com.meli.notifier.forecast.domain.exception.NotFoundException;
import com.meli.notifier.forecast.domain.exception.ValidationException;
import com.meli.notifier.forecast.domain.model.database.Subscription;
import com.meli.notifier.forecast.domain.model.database.User;
import com.meli.notifier.forecast.domain.service.AuthContextService;
import com.meli.notifier.forecast.domain.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/v1/subscriptions")
@Tag(name = "Subscriptions", description = "Subscription management endpoints")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final AuthContextService authContextService;
    private final SubscriptionService subscriptionService;

    @Operation(summary = "Create a new subscription",
               description = "Creates a new subscription for the authenticated user to receive weather forecast notifications for a specific city")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Subscription created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "404", description = "City not found"),
        @ApiResponse(responseCode = "409", description = "Subscription already exists for this city")
    })
    @PostMapping
    public ResponseEntity<Subscription> createSubscription(
            @Parameter(description = "Subscription details", required = true)
            @Valid @RequestBody SubscriptionRequestDTO requestDTO) {

        User user = authContextService.getCurrentUser();
        try {
            Subscription subscription = subscriptionService.createSubscription(user, requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
        } catch (ValidationException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Failed to create subscription: " + e.getMessage());
        }
    }

//    @Operation(summary = "Get all user subscriptions",
//               description = "Retrieves all active subscriptions for the authenticated user")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully"),
//        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
//    })
//    @GetMapping
//    public ResponseEntity<List<Subscription>> getSubscriptions() {
//        User user = authContextService.getCurrentUser();
//        List<Subscription> subscriptions = subscriptionService.getSubscriptions(user);
//        return ResponseEntity.ok(subscriptions);
//    }
//
//    @Operation(summary = "Get a specific subscription",
//               description = "Retrieves a specific subscription by ID for the authenticated user")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Subscription retrieved successfully"),
//        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
//        @ApiResponse(responseCode = "404", description = "Subscription not found")
//    })
//    @GetMapping("/{subscriptionId}")
//    public ResponseEntity<Subscription> getSubscription(
//            @Parameter(description = "Subscription ID", required = true)
//            @PathVariable Long subscriptionId) {
//
//        User user = authContextService.getCurrentUser();
//        return subscriptionService.getSubscription(user, subscriptionId)
//                .map(ResponseEntity::ok)
//                .orElseThrow(() -> new NotFoundException("Subscription not found with ID: " + subscriptionId));
//    }
//
//    @Operation(summary = "Update a subscription",
//               description = "Updates an existing subscription for the authenticated user")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Subscription updated successfully"),
//        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
//        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
//        @ApiResponse(responseCode = "404", description = "Subscription or city not found")
//    })
//    @PutMapping("/{subscriptionId}")
//    public ResponseEntity<Subscription> updateSubscription(
//            @Parameter(description = "Subscription ID", required = true)
//            @PathVariable Long subscriptionId,
//            @Parameter(description = "Updated subscription details", required = true)
//            @Valid @RequestBody EnhancedSubscriptionRequestDTO requestDTO) {
//
//        User user = authContextService.getCurrentUser();
//        try {
//            Subscription subscription = subscriptionService.updateSubscription(user, subscriptionId, requestDTO);
//            return ResponseEntity.ok(subscription);
//        } catch (ValidationException | NotFoundException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new ValidationException("Failed to update subscription: " + e.getMessage());
//        }
//    }
//
//    @Operation(summary = "Delete a subscription",
//               description = "Deletes a specific subscription by ID for the authenticated user")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "204", description = "Subscription deleted successfully"),
//        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
//        @ApiResponse(responseCode = "404", description = "Subscription not found")
//    })
//    @DeleteMapping("/{subscriptionId}")
//    public ResponseEntity<Void> deleteSubscription(
//            @Parameter(description = "Subscription ID", required = true)
//            @PathVariable Long subscriptionId) {
//
//        User user = authContextService.getCurrentUser();
//        subscriptionService.deleteSubscription(user, subscriptionId);
//        return ResponseEntity.noContent().build();
//    }
}
