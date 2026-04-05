package com.junnar.tourist_guide.repository;

import com.junnar.tourist_guide.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByMonth(String month);
    List<Event> findByTag(String tag);
}