package organizer.backend.domain.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlaylistObject(Boolean collaborative,
                             String description,
                             ExternalURLObject external_urls,
                             String href,
                             String id,
                             ImageObject[] images,
                             String name,
                             OwnerObject owner,
                             @JsonProperty("public") Boolean publicValue,
                             String snapshot_id,
                             TracksObject tracks,
                             String type,
                             String uri) {
}
