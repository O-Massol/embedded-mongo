package space.guild.infrastructure.mongo;

import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import space.guild.domain.models.ArrakisRegion;
import space.guild.infrastructure.mongo.documents.RegionalSpiceRecord;
import space.guild.testing.EmbeddedMongoTest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RegionalSpiceRecordRepositoryTest extends EmbeddedMongoTest {

    private ZonedDateTime YEAR10117_DAY56_UTC = ZonedDateTime.of(LocalDate.ofYearDay(10117, 56).atStartOfDay(), ZoneId.of("Z"));

    @Inject
    RegionalSpiceRecordRepository repository;

    @Test
    void save_newSpiceRecord_recordExists() {
        //given
        var record = new RegionalSpiceRecord(null, ArrakisRegion.SHIELD_WALL, YEAR10117_DAY56_UTC.toInstant(), 7439);
        //when
        var savedRecord = repository.save(record);
        //then
        var foundRecord = repository.findById(savedRecord.id()).orElseThrow();
        assertThat(foundRecord).usingRecursiveComparison().ignoringFields("id").isEqualTo(record);
    }
}