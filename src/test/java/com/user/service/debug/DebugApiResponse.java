package com.user.service.debug;

import com.user.service.dto.response.ApiResponse;

public class DebugApiResponse {
    public static void main(String[] args) {
        System.out.println("Testing ApiResponse creation...");
        
        String testData = "Test Data";
        ApiResponse<String> response = ApiResponse.success(testData);
        
        System.out.println("Success: " + response.isSuccess());
        System.out.println("Message: " + response.getMessage());
        System.out.println("Data: " + response.getData());
        System.out.println("Timestamp: " + response.getTimestamp());
        System.out.println("Error: " + response.getError());
        
        // Test using builder directly
        System.out.println("\nTesting builder directly...");
        ApiResponse<String> directResponse = ApiResponse.<String>builder()
                .success(true)
                .message("Test Message")
                .data("Direct Test Data")
                .build();
        
        System.out.println("Direct Success: " + directResponse.isSuccess());
        System.out.println("Direct Message: " + directResponse.getMessage());
        System.out.println("Direct Data: " + directResponse.getData());
    }
}
