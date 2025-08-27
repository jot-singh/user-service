package com.user.service.error;

import com.user.service.dto.response.ApiResponse;

/**
 * Simple test to debug ApiResponse construction
 */
public class SimpleApiResponseTest {
    
    public static void main(String[] args) {
        // Test the success method
        String testData = "Test Data";
        ApiResponse<String> response = ApiResponse.success(testData);
        
        System.out.println("Success: " + response.isSuccess());
        System.out.println("Message: " + response.getMessage());
        System.out.println("Data: " + response.getData());
        System.out.println("Error: " + response.getError());
        System.out.println("Timestamp: " + response.getTimestamp());
        System.out.println("Path: " + response.getPath());
        
        // Simple assertion
        if (testData.equals(response.getData())) {
            System.out.println("✅ SUCCESS: Data matches");
        } else {
            System.out.println("❌ FAILURE: Data doesn't match. Expected: " + testData + ", Got: " + response.getData());
        }
    }
}
