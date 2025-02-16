package space.guild.infrastructure.mongo;

import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.StateID;
import de.flapdoodle.reverse.TransitionWalker;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import space.guild.domain.models.ArrakisRegion;
import space.guild.infrastructure.mongo.documents.RegionalSpiceRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegionalSpiceRecordRepositoryTest implements TestPropertyProvider {

    private ZonedDateTime YEAR10117_DAY56_UTC = ZonedDateTime.of(LocalDate.ofYearDay(10117, 56).atStartOfDay(), ZoneId.of("Z"));

    private TransitionWalker.ReachedState<RunningMongodProcess> running;

    @AfterAll
    void teardown(){
        Optional.ofNullable(running).ifPresent(TransitionWalker.ReachedState::close);
    }

    @Inject
    RegionalSpiceRecordRepository repository;

    @Inject
    MongoClient mongoClient;

    @Test
    void testMongoConnection() {
        var database = mongoClient.getDatabase("testdb");
        assertThat(database).isNotNull().returns("testdb", MongoDatabase::getName);
    }

    @Test
    void xxx() {
        //given
        var record = new RegionalSpiceRecord(null, ArrakisRegion.SHIELD_WALL, YEAR10117_DAY56_UTC.toInstant(), 7439);
        //when
        var savedRecord = repository.save(record);
        //then
        var foundRecord = repository.findById(savedRecord.id()).orElseThrow();
        assertThat(foundRecord).usingRecursiveComparison().ignoringFields("id").isEqualTo(record);
    }

    @Override
    public @NonNull Map<String, String> getProperties() {
        try {
            var transitions = Mongod.instance().transitions(Version.Main.V7_0);
            if(running == null) {
                running = transitions.walker()
                        .initState(StateID.of(RunningMongodProcess.class));
            }
            var currentAddress = running.current().getServerAddress();
            return Map.of("mongodb.uri", "mongodb://" + currentAddress.toString() + "/SPACEGUILD?retryWrites=false");
        } catch (Exception e) {
            throw new RuntimeException("Échec du démarrage de MongoDB Embedded", e);
        }
    }
}