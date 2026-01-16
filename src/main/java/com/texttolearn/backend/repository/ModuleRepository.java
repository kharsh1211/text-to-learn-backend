package com.texttolearn.backend.repository;

import com.texttolearn.backend.model.Module;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends MongoRepository<Module, String> {
}
