package com.texttolearn.backend.controller;

import com.texttolearn.backend.model.Course;
import com.texttolearn.backend.service.CourseService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:4200")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }
    @PostMapping
    public ResponseEntity<Object> createCourse(@RequestBody Map<String, String> payload) {
        String topic = payload.get("topic");
        
        try {
            Course course = courseService.generateCourseFromTopic(topic);
            return ResponseEntity.ok(course);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body("Manual Debug Info: Failed to parse or generate. Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable String id) {
        return courseService.getCourseById(id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }
}