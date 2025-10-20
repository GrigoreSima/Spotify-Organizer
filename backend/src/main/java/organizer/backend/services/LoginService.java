package organizer.backend.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import organizer.backend.repositories.LocalStorageRepository;
import organizer.backend.domain.responses.AccessTokenResponse;

import java.util.Base64;
import java.util.Objects;

@Service
public class LoginService {
    Logger logger = LoggerFactory.getLogger(LoginService.class);

    private final WebClient webClient;

    @Value("${spotify.client.id}")
    private String clientID;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.redirect_uri}")
    private String redirectUri;

    private final LocalStorageRepository localStorage;

    @Autowired
    public LoginService(WebClient.Builder webClientBuilder, LocalStorageRepository localStorageRepository) {
        this.webClient = webClientBuilder.baseUrl("https://accounts.spotify.com/api/token").build();
        this.localStorage = localStorageRepository;
    }

    public void disconnect(String id) {
        logger.info("Disconnected user with id = {}", id);
        localStorage.removeState(id);
    }

    public String getRandomState(String id) {
        String state = RandomStringUtils.secure().nextAlphanumeric(16);
        logger.info("Generated state = {} for user with id = {}", state, id);
        localStorage.addState(id, state);
        return state;
    }

    public AccessTokenResponse getAccessToken(String id, String code, String state) {

        if (Objects.isNull(code) || code.isEmpty()) {
            logger.warn("Failed to generate access token for user with id = {}!\n Code = {} is invalid!", id, code);
            return null;
        }

        String savedState = localStorage.getState(id);

        if (Objects.isNull(savedState) || !Objects.equals(savedState, state)) {
            logger.warn("Failed to generate access token for user with id = {}!\n Saved state = {} or State = {} is invalid!", id, savedState, state);
            return null;
        }

        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

            body.add("grant_type", "authorization_code");
            body.add("code", code);
            body.add("redirect_uri", redirectUri);

            AccessTokenResponse accessToken = this.webClient.post().bodyValue(body)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("%s:%s".formatted(clientID, clientSecret).getBytes()))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .retrieve().bodyToMono(AccessTokenResponse.class).block();

            assert accessToken != null;

            logger.info("Obtained access token = {} for user with id = {}!", accessToken, id);

            return accessToken;
        } catch (WebClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AccessTokenResponse refreshAccessToken(String id, String refreshToken) {

        if (Objects.isNull(refreshToken) || refreshToken.isEmpty()) {
            logger.warn("Failed to refresh access token for user with id = {} and refresh_token = {}!", id, refreshToken);
            return null;
        }

        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

            body.add("grant_type", "refresh_token");
            body.add("refresh_token", refreshToken);

            AccessTokenResponse accessToken = this.webClient.post().bodyValue(body)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("%s:%s".formatted(clientID, clientSecret).getBytes()))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .retrieve().bodyToMono(AccessTokenResponse.class).block();

            assert accessToken != null;

            logger.info("Obtained refreshed access token = {} for user with id = {}!", accessToken, id);
            return accessToken;
        } catch (WebClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}