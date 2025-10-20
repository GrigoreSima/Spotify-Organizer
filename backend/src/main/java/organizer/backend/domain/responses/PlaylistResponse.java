package organizer.backend.domain.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import organizer.backend.domain.objects.ExternalURLObject;
import organizer.backend.domain.objects.ImageObject;
import organizer.backend.domain.objects.OwnerObject;
import organizer.backend.domain.objects.TracksObject;

public record PlaylistResponse(Boolean collaborative,
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
                               String uri
                               ) {
}
