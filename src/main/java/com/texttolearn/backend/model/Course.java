package com.texttolearn.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
public class Course {
    @Id
    private String id;
    private String title;
    private String description;
    
    // List of modules which now contain their own videos
    private List<Module> modules;

    // Optional: Keep this if you still want overall course videos

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicVideo {
        private String title;
        private String videoUrl;
    }
}