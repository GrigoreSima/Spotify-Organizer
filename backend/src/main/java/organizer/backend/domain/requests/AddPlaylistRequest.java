package organizer.backend.domain.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddPlaylistRequest(String name, @JsonProperty("public") Boolean publicValue, Boolean collaborative, String description) {
}
