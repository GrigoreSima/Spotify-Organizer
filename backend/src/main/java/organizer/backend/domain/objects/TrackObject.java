package organizer.backend.domain.objects;

import java.util.Arrays;
import java.util.Objects;

public record TrackObject(AlbumObject album,
                          SimplifiedArtistObject[] artists,
                          String[] available_markets,
                          Integer disc_number,
                          Integer duration_ms,
                          Boolean explicit,
                          ExternalIDsObject external_ids,
                          ExternalURLObject external_urls,
                          String href,
                          String id,
                          Boolean is_playable,
                          RestrictionsObject restrictions,
                          String name,
                          Integer popularity,
                          String preview_url,
                          Integer track_number,
                          String type,
                          String uri,
                          Boolean is_local) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TrackObject that = (TrackObject) o;
        return Objects.equals(name(), that.name()) && Objects.equals(duration_ms(), that.duration_ms()) && Objects.deepEquals(artists(), that.artists());
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(artists()), duration_ms(), name());
    }
}
