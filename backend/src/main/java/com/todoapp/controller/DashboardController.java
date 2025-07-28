package com.todoapp.controller;

import com.todoapp.dto.DashboardStatisticsDTO;
import com.todoapp.dto.TaskDTO;
import com.todoapp.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for Dashboard operations.
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
@Tag(name = "Dashboard", description = "Dashboard and analytics endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(
        summary = "Get dashboard statistics",
        description = "Retrieves comprehensive statistics for the dashboard"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
            content = @Content(schema = @Schema(implementation = DashboardStatisticsDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/statistics")
    public ResponseEntity<DashboardStatisticsDTO> getStatistics(Authentication authentication) {
        String userEmail = authentication.getName();
        DashboardStatisticsDTO statistics = dashboardService.getStatistics(userEmail);
        return ResponseEntity.ok(statistics);
    }

    @Operation(
        summary = "Get today's tasks",
        description = "Retrieves all tasks due today"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Today's tasks retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/today")
    public ResponseEntity<List<TaskDTO>> getTodayTasks(Authentication authentication) {
        String userEmail = authentication.getName();
        List<TaskDTO> tasks = dashboardService.getTodayTasks(userEmail);
        return ResponseEntity.ok(tasks);
    }

    @Operation(
        summary = "Get upcoming tasks",
        description = "Retrieves tasks due in the next 7 days"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Upcoming tasks retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/upcoming")
    public ResponseEntity<List<TaskDTO>> getUpcomingTasks(Authentication authentication) {
        String userEmail = authentication.getName();
        List<TaskDTO> tasks = dashboardService.getUpcomingTasks(userEmail);
        return ResponseEntity.ok(tasks);
    }

    @Operation(
        summary = "Get overdue tasks",
        description = "Retrieves all overdue tasks"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Overdue tasks retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDTO>> getOverdueTasks(Authentication authentication) {
        String userEmail = authentication.getName();
        List<TaskDTO> tasks = dashboardService.getOverdueTasks(userEmail);
        return ResponseEntity.ok(tasks);
    }

    @Operation(
        summary = "Get recent activity",
        description = "Retrieves recent user activity and task changes"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recent activity retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/activity")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivity(Authentication authentication) {
        String userEmail = authentication.getName();
        List<Map<String, Object>> activity = dashboardService.getRecentActivity(userEmail);
        return ResponseEntity.ok(activity);
    }
} 