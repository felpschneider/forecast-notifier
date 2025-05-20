package com.meli.notifier.forecast.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SubscriptionRequestDTO {
    @NotNull(message = "City ID is required")
    @Schema(description = "The ID of the city to subscribe for notifications", example = "244")
    private Long cityId;
    
    @Pattern(regexp = "^$|^(\\*|([0-9]|[1-5][0-9])(\\-|\\/|\\,)?)+(\\*|[0-9]|[1-5][0-9])?$", 
             message = "Invalid cron expression for minute (0-59)")
    @Schema(description = "Cron expression for minute repetition (0-59)", example = "0", nullable = true)
    private String minuteRepetition;
    
    @Pattern(regexp = "^$|^(\\*|([0-9]|1[0-9]|2[0-3])(\\-|\\/|\\,)?)+(\\*|[0-9]|1[0-9]|2[0-3])?$", 
             message = "Invalid cron expression for hour (0-23)")
    @Schema(description = "Cron expression for hour repetition (0-23)", example = "8,12,18", nullable = true)
    private String hourRepetition;
    
    @Pattern(regexp = "^$|^(\\*|\\?|([1-7]|MON|TUE|WED|THU|FRI|SAT|SUN)(\\-|\\/|\\,|L|#)?)+(\\*|[1-7]|MON|TUE|WED|THU|FRI|SAT|SUN)?$", 
             message = "Invalid cron expression for day of week (1-7 or MON-SUN)")
    @Schema(description = "Cron expression for day of week repetition (1-7 or MON-SUN)", example = "MON-FRI", nullable = true)
    private String dayOfWeekRepetition;
    
    @Pattern(regexp = "^$|^(\\*|\\?|([1-9]|[12][0-9]|3[01])(\\-|\\/|\\,|L|W)?)+(\\*|[1-9]|[12][0-9]|3[01])?$", 
             message = "Invalid cron expression for day of month (1-31)")
    @Schema(description = "Cron expression for day of month repetition (1-31)", example = "1,15", nullable = true)
    private String dayOfMonthRepetition;
    
    @Pattern(regexp = "^$|^(\\*|([1-9]|1[0-2])(\\-|\\/|\\,)?)+(\\*|[1-9]|1[0-2])?$", 
             message = "Invalid cron expression for month (1-12)")
    @Schema(description = "Cron expression for month repetition (1-12)", example = "*", nullable = true)
    private String monthRepetition;
    
    @Pattern(regexp = "^$|^(\\*|([0-9]{4})(\\-|\\/|\\,)?)+(\\*|([0-9]{4}))?$", 
             message = "Invalid cron expression for year")
    @Schema(description = "Cron expression for year repetition", example = "*", nullable = true)
    private String yearRepetition;
}
