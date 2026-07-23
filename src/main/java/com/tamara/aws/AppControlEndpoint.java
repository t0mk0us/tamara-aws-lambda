package com.tamara.aws;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Component
@Endpoint(id = "awslambdacontrol")
public class AppControlEndpoint {

    private final String SCRIPTS_DIR = "C:\\Users\\JavaProjects\\PS_scripts\\";

    @WriteOperation
    public Map<String, String> triggerScriptAction(@org.springframework.boot.actuate.endpoint.annotation.Selector String actionName) {
        Map<String, String> response = new HashMap<>();
        String scriptName;
        
        switch (actionName.toLowerCase().trim()) {
            case "start": scriptName = "start_awsLambda.ps1"; break;
            case "stop": scriptName = "stop_awsLambda.ps1"; break;
            case "restart": scriptName = "restart_Lambda.ps1"; break;
            default:
                response.put("status", "error");
                response.put("message", "Invalid entry! Type either: start, stop, or restart.");
                return response;
        }

        // ASYNC EXECUTION: This sends the HTTP response FIRST, preventing the SBA crash log!
        CompletableFuture.runAsync(() -> {
            try {
                // Brief pause so the Tomcat server thread has time to send the response back to SBA safely
                Thread.sleep(500); 
                
                ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-ExecutionPolicy", "Bypass", "-File", SCRIPTS_DIR + scriptName);
                pb.directory(new File(SCRIPTS_DIR));
                pb.start();
            } catch (Exception e) {
                System.err.println("Background execution failed: " + e.getMessage());
            }
        });

        // This returns immediately to SBA before the script kills the application
        response.put("status", "success");
        response.put("message", "Command '" + actionName + "' accepted. Running sequence in background...");
        return response;
    }
}