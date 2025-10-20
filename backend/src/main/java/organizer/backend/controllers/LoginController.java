package organizer.backend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import organizer.backend.domain.responses.AccessTokenResponse;
import organizer.backend.domain.responses.LinkResponse;
import organizer.backend.services.LoginService;

@RestController
@RequestMapping("/login")
public class LoginController {
    Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${spotify.client.id}")
    private String clientID;

    @Value("${spotify.scope}")
    private String scope;

    @Value("${spotify.redirect_uri}")
    private String redirectUri;

    private final LoginService loginService;

    @Autowired
    LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping
    public LinkResponse login(@RequestParam String id) {
        logger.info("Accessed GET /login with data: id = {}", id);
        return new LinkResponse("https://accounts.spotify.com/authorize?response_type=code&client_id=%s&scope=%s&redirect_uri=%s&state=%s"
                .formatted(this.clientID, this.scope, this.redirectUri, loginService.getRandomState(id)));
    }

    @GetMapping("/token/new")
    public AccessTokenResponse getToken(@RequestParam String id, @RequestParam String code, @RequestParam String state) {
        logger.info("Accessed GET /login/token/new with data: id = {}, code = {}, state = {}", id, code, state);
        return loginService.getAccessToken(id, code, state);
    }

    @GetMapping("/token/refresh")
    public AccessTokenResponse refreshToken(@RequestParam String id, @RequestParam String refresh_token) {
        logger.info("Accessed GET /login/token/refresh with data: id = {}, refresh_token = {}", id, refresh_token);
        return loginService.refreshAccessToken(id, refresh_token);
    }

    @GetMapping("/disconnect")
    public void disconnect(@RequestParam String id) {
        logger.info("Accessed GET /login/disconnect with data: id = {}", id);
        loginService.disconnect(id);
    }
}