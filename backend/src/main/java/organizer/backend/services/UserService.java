package organizer.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import organizer.backend.domain.responses.ProfileResponse;

import java.util.Objects;

@Service
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    private final WebClient webClient;

    @Autowired
    public UserService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.spotify.com/v1/me").build();
    }

    public ProfileResponse getUserProfile(String token) {
        if (Objects.isNull(token) || token.isEmpty()) {
            logger.warn("Failed to return user profile for user with token = {}!\n Token is invalid!", token);
            return null;
        }

        try {
            ProfileResponse profile = this.webClient.get()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve().bodyToMono(ProfileResponse.class).block();

            assert profile != null;

            logger.info("Successfully returned profile = {} for user with token = {}", profile, token);

            return profile;
        } catch (WebClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
