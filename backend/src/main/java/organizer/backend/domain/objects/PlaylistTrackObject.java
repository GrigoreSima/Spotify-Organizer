package organizer.backend.domain.objects;

import java.util.Objects;

public record PlaylistTrackObject(String added_at,
                                  AddedByObject added_by,
                                  Boolean is_local,
                                  TrackObject track) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistTrackObject that = (PlaylistTrackObject) o;
        return Objects.equals(track(), that.track());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(track());
    }
}
