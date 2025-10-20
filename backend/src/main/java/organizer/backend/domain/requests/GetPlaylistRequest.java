package organizer.backend.domain.requests;

public record GetPlaylistRequest(String playlist_id,
                                 String market,
                                 String fields,
                                 String additional_types) {
}
