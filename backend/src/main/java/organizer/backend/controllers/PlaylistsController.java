package organizer.backend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import organizer.backend.domain.objects.PlaylistObject;
import organizer.backend.domain.objects.PlaylistTrackObject;
import organizer.backend.domain.objects.SimplifiedArtistObject;
import organizer.backend.domain.requests.AddPlaylistRequest;
import organizer.backend.domain.requests.GetPlaylistsRequest;
import organizer.backend.domain.responses.AddItemsToPlaylistResponse;
import organizer.backend.domain.responses.PagedPlaylistsResponse;
import organizer.backend.domain.responses.PlaylistResponse;
import organizer.backend.services.SavedSongsService;
import organizer.backend.services.PlaylistService;

@RestController
@RequestMapping("/playlists")
class PlaylistsController {
    Logger logger = LoggerFactory.getLogger(PlaylistsController.class);

    private final PlaylistService playlistService;
    private final SavedSongsService savedSongsService;

    @Autowired
    PlaylistsController(PlaylistService playlistService, SavedSongsService savedSongsService) {
        this.savedSongsService = savedSongsService;
        this.playlistService = playlistService;
    }

    @GetMapping
    public PagedPlaylistsResponse getUserPlaylists(@RequestParam String userID, @RequestParam String token,
                                                   @RequestParam Integer limit, @RequestParam Integer offset) {
        GetPlaylistsRequest request = new GetPlaylistsRequest(limit, offset);
        logger.info("Accessed GET /playlists with data: userID = {}, request = {} and token = {}", userID,
                request, token);
        return this.playlistService.getUserPlaylists(userID, token, request);
    }

    @PostMapping("/new")
    public PlaylistResponse addPlaylist(@RequestParam String userID, @RequestParam String spotifyID,
                                        @RequestParam String token, @RequestBody AddPlaylistRequest playlist) {
        logger.info("Accessed POST /playlists/new with data: userID = {}, spotifyID = {}, playlist = {} and token = {}",
                userID, spotifyID, playlist, token);
        return this.playlistService.addPlaylist(userID, spotifyID, token, playlist);
    }

    @GetMapping("/{playlistID}")
    public PlaylistObject getPlaylist(@RequestParam String userID, @RequestParam String token,
                                      @PathVariable String playlistID) {
        logger.info("Accessed GET /playlists/{playlistID} with data: playlistID = {}", playlistID);
        return this.playlistService.getPlaylist(userID, token, playlistID);
    }

    @GetMapping("/{playlistID}/tracks")
    public PlaylistTrackObject[] getAllSongsInPlaylist(@RequestParam String userID, @RequestParam String token,
                                                       @PathVariable String playlistID) {
        logger.info("Accessed GET /playlists/{playlistID}/tracks with data: userID = {}, playlistID = {}  and token = {}",
                userID, playlistID, token);
        return this.playlistService.getAllSongsInPlaylist(userID, token, playlistID);
    }

    @GetMapping("/{playlistID}/artists")
    public SimplifiedArtistObject[] getArtistsInPlaylist(@RequestParam String userID, @RequestParam String token,
                                                       @PathVariable String playlistID) {
        logger.info("Accessed GET /playlists/{playlistID}/artists with data: userID = {}, playlistID = {} and token = {}", userID, playlistID, token);
        return this.playlistService.getArtistsInPlaylist(userID, token, playlistID);
    }

    @PostMapping("/{playlistID}/artists/{artistID}")
    public PlaylistObject splitPlaylistByArtist(@RequestParam String userID,
                                                @RequestParam String spotifyID,
                                                @RequestParam String token,
                                                @PathVariable String playlistID,
                                                @PathVariable String artistID) {
        logger.info("Accessed POST /playlists/{playlistID}/artists/{artistID} with data: userID = {}, playlistID = {}, artistID = {} " +
                "and token = {}", userID, playlistID, artistID, token);
        return this.playlistService.splitPlaylistByArtist(userID, spotifyID, token, playlistID, artistID);
    }

    @PutMapping("/{playlistTwoID}")
    public AddItemsToPlaylistResponse mergePlaylistsInPlace(@RequestParam String userID, @RequestParam String token,
                                                            @RequestParam String playlistOneID,
                                                            @PathVariable String playlistTwoID) {
        logger.info("Accessed PUT /playlists/{playlistTwoID} with data: userID = {}" +
                ", playlistOneID = {}, playlistTwoID = {} and token = {}", userID, playlistOneID, playlistTwoID, token);
        return this.playlistService.mergeTwoPlaylistsInPlace(userID, token, playlistOneID, playlistTwoID);
    }

    @PostMapping("/merge")
    public PlaylistResponse mergePlaylists(@RequestParam String userID,
                                           @RequestParam String spotifyID,
                                           @RequestParam String token,
                                           @RequestParam String playlistOneID,
                                           @RequestParam String playlistTwoID,
                                           @RequestBody AddPlaylistRequest request) {
        logger.info("Accessed POST /playlists/merge with data: userID = {}" +
                        ", spotifyID = {}, playlistOneID = {}, playlistTwoID = {}, request = {} and token = {}",
                userID, spotifyID, playlistOneID, playlistTwoID, request, token);
        return this.playlistService.mergeTwoPlaylists(userID, spotifyID, token, playlistOneID, playlistTwoID, request);
    }

    @PutMapping("/{playlistID}/saved")
    public AddItemsToPlaylistResponse addSavedSongsToPlaylist(@RequestParam String userID, @RequestParam String token,
                                                            @PathVariable String playlistID) {
        logger.info("Accessed PUT /playlists/{playlistID}/saved with data: userID = {}" +
                ", playlistID = {} and token = {}", userID, playlistID, token);
        return this.savedSongsService.addSavedSongsToPlaylist(userID, token, playlistID);
    }
}
