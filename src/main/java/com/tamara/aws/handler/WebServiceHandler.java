package com.tamara.aws.handler;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamara.aws.model.User;

import java.util.HashMap;
import java.util.Map;

public class WebServiceHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    // Reuse the ObjectMapper instance across executions for better performance
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        String httpMethod = request.getHttpMethod();
        String path = request.getPath();
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.setHeaders(headers);

        try {
            if ("GET".equalsIgnoreCase(httpMethod) && "/users".equals(path)) {
                response.setStatusCode(200);
                response.setBody("{\"message\": \"Fetching all users\"}");
            } 
            else if ("POST".equalsIgnoreCase(httpMethod) && "/users".equals(path)) {
                String requestBody = request.getBody();
                
                // Parse JSON string into User object
                User newUser = objectMapper.readValue(requestBody, User.class);
                
                // Process data (e.g., save to a database)
                context.getLogger().log("Created user: " + newUser.getName() + " (" + newUser.getEmail() + ")");
                
                // Create a success response object map
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("status", "success");
                responseMap.put("userId", "usr_" + System.currentTimeMillis());
                
                // Serialize response map back to a JSON string
                response.setStatusCode(201);
                response.setBody(objectMapper.writeValueAsString(responseMap));
            } 
            else {
                response.setStatusCode(404);
                response.setBody("{\"error\": \"Resource Not Found\"}");
            }
        } catch (Exception e) {
            context.getLogger().log("Error processing request: " + e.getMessage());
            response.setStatusCode(400);
            response.setBody("{\"error\": \"Invalid request payload format\"}");
        }

        return response;
    }
}