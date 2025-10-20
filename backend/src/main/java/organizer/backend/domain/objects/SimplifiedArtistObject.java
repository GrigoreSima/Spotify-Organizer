package organizer.backend.domain.objects;

public record SimplifiedArtistObject(ExternalURLObject external_urls,
                                     String href,
                                     String id,
                                     String name,
                                     String type,
                                     String uri) {
}
