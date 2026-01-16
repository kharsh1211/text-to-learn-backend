package com.texttolearn.backend.repository;

import com.texttolearn.backend.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> { 
    
}
