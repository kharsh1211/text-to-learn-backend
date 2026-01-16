package com.texttolearn.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Module {
    private String id;
    private String title;
    private List<String> learning_objectives;
    private List<Lesson> lessons;
    
    // NEW FIELDS for module-wise videos
    private String moduleVideoQuery; // AI creates this
    private String moduleVideoUrl;   // YoutubeService fills this
}