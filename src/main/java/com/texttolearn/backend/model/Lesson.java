package com.texttolearn.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    private String id;
    private String title;
    private String detailed_content;
    private String practical_exercise;
}