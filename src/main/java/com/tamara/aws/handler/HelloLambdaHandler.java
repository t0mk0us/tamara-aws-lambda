package com.tamara.aws.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Map;

public class HelloLambdaHandler implements RequestHandler<Map<String, String>, String> {

    @Override
    public String handleRequest(Map<String, String> input, Context context) {
        // Obtain the logger instance from CloudWatch context
        LambdaLogger logger = context.getLogger();
        
        logger.log("Function name: " + context.getFunctionName());
        logger.log("Remaining execution time: " + context.getRemainingTimeInMillis() + "ms");

        // Extract value from the incoming JSON payload
        String name = input.getOrDefault("name", "World");
        
        String greeting = "Hello, " + name + "! Welcome to AWS Lambda using Java.";
        logger.log("Response sent: " + greeting);

        return greeting;
    }
}
