package organizer.backend.domain.objects;

public record TracksObject(String href,
                           Integer limit,
                           String next,
                           Integer offset,
                           String previous,
                           Integer total,
                           PlaylistTrackObject[] items,
                           String type,
                           String uri
                           ) {
}
