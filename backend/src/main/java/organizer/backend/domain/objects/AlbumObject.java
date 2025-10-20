package organizer.backend.domain.objects;

public record AlbumObject(String album_type,
                          Integer total_tracks,
                          String[] available_markets,
                          ExternalURLObject external_urls,
                          String href,
                          String id,
                          ImageObject[] images,
                          String name,
                          String release_date,
                          String release_date_precision,
                          RestrictionsObject restrictions,
                          String type,
                          String uri,
                          SimplifiedArtistObject[] artists) {
}
