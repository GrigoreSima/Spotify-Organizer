package organizer.backend.domain.responses;

import organizer.backend.domain.objects.ExplicitContentObject;
import organizer.backend.domain.objects.ExternalURLObject;
import organizer.backend.domain.objects.FollowersObject;
import organizer.backend.domain.objects.ImageObject;

public record ProfileResponse(
        String country,
        String display_name,
        String email,
        ExplicitContentObject explicit_content,
        ExternalURLObject external_urls,
        FollowersObject followers,
        String href,
        String id,
        ImageObject[] images,
        String product,
        String type,
        String uri) {
}
