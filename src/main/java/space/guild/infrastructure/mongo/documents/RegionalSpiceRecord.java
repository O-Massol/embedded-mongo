package space.guild.infrastructure.mongo.documents;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import space.guild.domain.models.ArrakisRegion;

import java.time.Instant;

@MappedEntity("regional_spice_record_xxx")
public record RegionalSpiceRecord (
        @Id @GeneratedValue String id,
        ArrakisRegion region,
        Instant receivedAt,
        int spiceAmountInTons
) {

}
