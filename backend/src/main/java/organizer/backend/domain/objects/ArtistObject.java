package organizer.backend.domain.objects;

public record ArtistObject(ExternalURLObject external_urls,
                           FollowersObject followers,
                           String[] genres,
                           String href,
                           String id,
                           ImageObject[] images,
                           String name,
                           Integer popularity,
                           String type,
                           String uri) {
}
