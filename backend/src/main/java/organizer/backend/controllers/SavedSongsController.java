package organizer.backend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import organizer.backend.domain.objects.PlaylistObject;
import organizer.backend.domain.objects.PlaylistTrackObject;
import organizer.backend.domain.objects.SimplifiedArtistObject;
import organizer.backend.domain.responses.AddItemsToPlaylistResponse;
import organizer.backend.services.SavedSongsService;

@RestController
@RequestMapping("/saved")
class SavedSongsController {
    private final SavedSongsService savedSongsService;
    Logger logger = LoggerFactory.getLogger(SavedSongsController.class);

    @Autowired
    SavedSongsController(SavedSongsService savedSongsService) {
        this.savedSongsService = savedSongsService;
    }

    @GetMapping("/tracks")
    public PlaylistTrackObject[] getAllSavedSongs(@RequestParam String userID, @RequestParam String token) {
        logger.info("Accessed GET /saved/tracks with data: userID = {} and token = {}",
                userID, token);
        return this.savedSongsService.getAllSavedSongs(userID, token);
    }

    @DeleteMapping("/tracks")
    public void removeAllSavedSongs(@RequestParam String userID, @RequestParam String token) {
        logger.info("Accessed DELETE /saved/tracks with data: userID = {} and token = {}",
                userID, token);
        this.savedSongsService.removeAllSavedSongs(userID, token);
    }

    @GetMapping("/artists")
    public SimplifiedArtistObject[] getSavedPlaylistArtists(@RequestParam String userID, @RequestParam String token) {
        logger.info("Accessed GET /saved/artists with data: userID = {} and token = {}", userID, token);
        return this.savedSongsService.getArtistsInSavedPlaylist(userID, token);
    }

    @PostMapping("/artists/{artistID}")
    public PlaylistObject splitSavedPlaylistByArtist(@RequestParam String userID,
                                                @RequestParam String spotifyID,
                                                @RequestParam String token,
                                                @PathVariable String artistID) {
        logger.info("Accessed POST /saved/artists/{artistID} with data: userID = {}, artistID = {} " +
                "and token = {}", userID, artistID, token);
        return this.savedSongsService.splitSavedSongsByArtist(userID, spotifyID, token, artistID);
    }

    @PutMapping("/{playlistID}")
    public AddItemsToPlaylistResponse addPlaylistToSavedSongs(@RequestParam String userID, @RequestParam String token,
                                                            @PathVariable String playlistID) {
        logger.info("Accessed PUT /saved/{playlistID} with data: userID = {}" +
                ", playlistID = {} and token = {}", userID, playlistID, token);
        return this.savedSongsService.addPlaylistToSavedSongs(userID, token, playlistID);
    }
}
