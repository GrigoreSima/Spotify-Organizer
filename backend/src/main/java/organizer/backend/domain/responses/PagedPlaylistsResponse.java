package organizer.backend.domain.responses;

import organizer.backend.domain.objects.SimplifiedPlaylistObject;

public record PagedPlaylistsResponse(String href,
                                     Integer limit,
                                     String next,
                                     Integer offset,
                                     String previous,
                                     Integer total,
                                     SimplifiedPlaylistObject[] items) {
}
