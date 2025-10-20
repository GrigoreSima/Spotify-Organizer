package organizer.backend.domain.objects;

public record OwnerObject(ExternalURLObject external_urls,
                          String href,
                          String id,
                          String type,
                          String uri,
                          String display_name) {
}
