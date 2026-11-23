package com.junnar.tourist_guide.repository;

import com.junnar.tourist_guide.model.ContactMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactMessageRepository extends MongoRepository<ContactMessage, String> {
}
