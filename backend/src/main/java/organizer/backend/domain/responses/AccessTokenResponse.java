package organizer.backend.domain.responses;

public record AccessTokenResponse(String access_token, String token_type, String scope, Integer expires_in, String refresh_token) {
}
