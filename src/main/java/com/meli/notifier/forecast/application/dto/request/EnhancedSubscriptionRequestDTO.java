package com.meli.notifier.forecast.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EnhancedSubscriptionRequestDTO {
    @NotNull(message = "City ID is required")
    @Schema(description = "The ID of the city to subscribe for notifications", example = "244")
    private Long cityId;
    
    @Schema(description = "Timezone for the notification schedule (defaults to system timezone if not specified)", 
           example = "America/Sao_Paulo", nullable = true)
    private String timezone;
    
    @Schema(description = "Predefined schedule type", 
           example = "DAILY", 
           allowableValues = {"HOURLY", "DAILY", "WEEKLY", "MONTHLY", "CUSTOM"})
    private ScheduleType scheduleType;
    
    // Fields for HOURLY schedule
    @Schema(description = "For HOURLY schedule: interval in hours (e.g. 2 for every 2 hours)", 
           example = "3", nullable = true)
    private Integer hourlyInterval;
    
    @Schema(description = "For HOURLY schedule: starting from hour (0-23)", 
           example = "9", nullable = true)
    private Integer hourlyStartingHour;
    
    // Fields for DAILY schedule
    @Schema(description = "For DAILY schedule: times of day to receive notifications (in hours, 0-23)", 
           example = "[8, 12, 18]", nullable = true)
    private List<Integer> dailyHours;
    
    @Schema(description = "For DAILY schedule: whether to include weekends", 
           example = "true", nullable = true)
    private Boolean includeWeekends;
    
    // Fields for WEEKLY schedule
    @Schema(description = "For WEEKLY schedule: days of week (1-7, where 1 is Monday)", 
           example = "[1, 3, 5]", nullable = true)
    private List<Integer> weeklyDays;
    
    @Schema(description = "For WEEKLY schedule: hour of day (0-23)", 
           example = "9", nullable = true)
    private Integer weeklyHour;
    
    // Fields for MONTHLY schedule
    @Schema(description = "For MONTHLY schedule: days of month (1-31)", 
           example = "[1, 15]", nullable = true)
    private List<Integer> monthlyDays;
    
    @Schema(description = "For MONTHLY schedule: hour of day (0-23)", 
           example = "10", nullable = true)
    private Integer monthlyHour;
    
    // Fields for advanced users who want to specify a custom cron pattern
    @Schema(description = "For CUSTOM schedule: full cron expression (all 6 parts)", 
           example = "0 0 12 * * *", nullable = true)
    private String customCronExpression;
    
    // Legacy fields for backward compatibility
    @Schema(description = "Legacy field: Cron expression for minute repetition (0-59)", example = "0", nullable = true)
    @Pattern(regexp = "^$|^(\\*|([0-9]|[1-5][0-9])(\\-|\\/|\\,)?)+(\\*|[0-9]|[1-5][0-9])?$", 
             message = "Invalid cron expression for minute (0-59)")
    private String minuteRepetition;
    
    @Schema(description = "Legacy field: Cron expression for hour repetition (0-23)", example = "8,12,18", nullable = true)
    @Pattern(regexp = "^$|^(\\*|([0-9]|1[0-9]|2[0-3])(\\-|\\/|\\,)?)+(\\*|[0-9]|1[0-9]|2[0-3])?$", 
             message = "Invalid cron expression for hour (0-23)")
    private String hourRepetition;
    
    @Schema(description = "Legacy field: Cron expression for day of week repetition (1-7 or MON-SUN)", 
           example = "MON-FRI", nullable = true)
    @Pattern(regexp = "^$|^(\\*|\\?|([1-7]|MON|TUE|WED|THU|FRI|SAT|SUN)(\\-|\\/|\\,|L|#)?)+(\\*|[1-7]|MON|TUE|WED|THU|FRI|SAT|SUN)?$", 
             message = "Invalid cron expression for day of week (1-7 or MON-SUN)")
    private String dayOfWeekRepetition;
    
    @Schema(description = "Legacy field: Cron expression for day of month repetition (1-31)", 
           example = "1,15", nullable = true)
    @Pattern(regexp = "^$|^(\\*|\\?|([1-9]|[12][0-9]|3[01])(\\-|\\/|\\,|L|W)?)+(\\*|[1-9]|[12][0-9]|3[01])?$", 
             message = "Invalid cron expression for day of month (1-31)")
    private String dayOfMonthRepetition;
    
    @Schema(description = "Legacy field: Cron expression for month repetition (1-12)", example = "*", nullable = true)
    @Pattern(regexp = "^$|^(\\*|([1-9]|1[0-2])(\\-|\\/|\\,)?)+(\\*|[1-9]|1[0-2])?$", 
             message = "Invalid cron expression for month (1-12)")
    private String monthRepetition;
    
    @Schema(description = "Legacy field: Cron expression for year repetition", example = "*", nullable = true)
    @Pattern(regexp = "^$|^(\\*|([0-9]{4})(\\-|\\/|\\,)?)+(\\*|([0-9]{4}))?$", 
             message = "Invalid cron expression for year")
    private String yearRepetition;
    
    public enum ScheduleType {
        HOURLY, 
        DAILY, 
        WEEKLY, 
        MONTHLY, 
        CUSTOM
    }
    
    /**
     * Validates that the required fields for the selected schedule type are present
     */
    @JsonIgnore
    public boolean isValid() {
        if (scheduleType == null) {
            if (minuteRepetition != null || hourRepetition != null || dayOfWeekRepetition != null ||
                dayOfMonthRepetition != null || monthRepetition != null || yearRepetition != null) {
                // Legacy mode with at least one cron field specified
                return true;
            }
            return false;
        }
        
        switch (scheduleType) {
            case HOURLY:
                return hourlyInterval != null && hourlyInterval > 0;
            case DAILY:
                return dailyHours != null && !dailyHours.isEmpty();
            case WEEKLY:
                return weeklyDays != null && !weeklyDays.isEmpty() && weeklyHour != null;
            case MONTHLY:
                return monthlyDays != null && !monthlyDays.isEmpty() && monthlyHour != null;
            case CUSTOM:
                return customCronExpression != null && !customCronExpression.isEmpty();
            default:
                return false;
        }
    }
}
