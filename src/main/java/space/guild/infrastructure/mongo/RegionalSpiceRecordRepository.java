package space.guild.infrastructure.mongo;

import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.CrudRepository;
import space.guild.infrastructure.mongo.documents.RegionalSpiceRecord;

@MongoRepository
public interface RegionalSpiceRecordRepository extends CrudRepository<RegionalSpiceRecord, String> {
}
