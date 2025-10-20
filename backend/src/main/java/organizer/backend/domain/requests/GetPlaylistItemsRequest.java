package organizer.backend.domain.requests;

public record GetPlaylistItemsRequest(String playlist_id,
                                      String market,
                                      String fields,
                                      Integer limit,
                                      Integer offset,
                                      String additional_types) {
}
