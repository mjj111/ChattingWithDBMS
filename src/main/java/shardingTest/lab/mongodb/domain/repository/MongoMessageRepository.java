package shardingTest.lab.mongodb.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import shardingTest.lab.mongodb.domain.MongoMessageEntity;

@Repository
public interface MongoMessageRepository extends MongoRepository<MongoMessageEntity, String>, CustomMessageRepository {
}