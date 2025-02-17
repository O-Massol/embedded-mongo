package space.guild.testing;

import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.StateID;
import de.flapdoodle.reverse.TransitionWalker;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;
import java.util.Optional;

@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class EmbeddedMongoTest implements TestPropertyProvider {

    private TransitionWalker.ReachedState<RunningMongodProcess> running;

    @AfterAll
    void teardown() {
        Optional.ofNullable(running).ifPresent(TransitionWalker.ReachedState::close);
    }

    @Override
    public @NonNull Map<String, String> getProperties() {
        try {
            var transitions = Mongod.builder()
                    .persistentBaseDir(EmbeddedMongoHelper.provideBaseDir())
                    .build().transitions(Version.Main.V7_0);
            if (running == null) {
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
