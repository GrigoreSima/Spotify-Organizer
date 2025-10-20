package organizer.backend.domain.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SimplifiedPlaylistObject(Boolean collaborative,
                                       String description,
                                       ExternalURLObject external_urls,
                                       String href,
                                       String id,
                                       ImageObject[] images,
                                       String name,
                                       OwnerObject owner,
                                       @JsonProperty("public") Boolean publicValue,
                                       String snapshot_id,
                                       FollowersObject tracks,
                                       String type,
                                       String uri) {
}
