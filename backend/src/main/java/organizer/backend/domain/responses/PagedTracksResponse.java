package organizer.backend.domain.responses;

import organizer.backend.domain.objects.PlaylistTrackObject;

public record PagedTracksResponse(String href,
                                  Integer limit,
                                  String next,
                                  Integer offset,
                                  String previous,
                                  Integer total,
                                  PlaylistTrackObject[] items) {
}
