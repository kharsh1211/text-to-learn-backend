package com.texttolearn.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(BackendApplication.class, args);
        
        // Retrieve the key from the environment
        String youtubeKey = context.getEnvironment().getProperty("youtube.api.key");
        
        System.out.println("========================================");
        if (youtubeKey != null && !youtubeKey.isEmpty()) {
            System.out.println("✅ SUCCESS: Environment variables loaded!");
            // Optional: Print a masked version to be safe
            System.out.println("YouTube Key starts with: " + youtubeKey.substring(0, 4) + "...");
        } else {
            System.err.println("❌ ERROR: .env variables NOT detected.");
            System.err.println("Check if launch.json points to the correct .env path.");
        }
        System.out.println("========================================");
	}

}
