package com.texttolearn.backend.repository;

import com.texttolearn.backend.model.Lesson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends MongoRepository<Lesson, String> {
}
