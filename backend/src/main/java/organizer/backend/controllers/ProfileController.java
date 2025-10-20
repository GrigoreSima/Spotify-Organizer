package organizer.backend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import organizer.backend.domain.responses.ProfileResponse;
import organizer.backend.services.UserService;

@RestController                                             
@RequestMapping("/profile")
class ProfileController {
    Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ProfileResponse getProfile(@RequestParam String token) {
        logger.info("Accessed GET /profile with data: token = {}", token);
        return userService.getUserProfile(token);
    }
}
