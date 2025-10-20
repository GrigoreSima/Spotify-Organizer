package organizer.backend.services;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import organizer.backend.domain.objects.*;
import organizer.backend.domain.requests.*;
import organizer.backend.domain.responses.AddItemsToPlaylistResponse;
import organizer.backend.domain.responses.PagedTracksResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class SavedSongsService {
    Logger logger = LoggerFactory.getLogger(SavedSongsService.class);

    private final WebClient webClient;

    private final PlaylistService playlistService;

    public SavedSongsService(WebClient.Builder webClientBuilder, PlaylistService playlistService) {
        this.webClient = webClientBuilder.baseUrl("https://api.spotify.com/v1").build();
        this.playlistService = playlistService;
    }

    public PlaylistObject splitSavedSongsByArtist(String userID, String spotifyID, String token, String artistID) {
        logger.info("Splitting saved songs by artist {} for user with id = {}!",
                artistID, userID);

        PlaylistTrackObject[] songs = getAllSavedSongs(userID, token);

        String[] songsToAdd = Arrays.stream(songs)
                .filter(song ->
                        Arrays.stream(song.track().artists())
                                .anyMatch(artist -> artist.id().equals(artistID)))
                .map(song -> song.track().uri())
                .toArray(String[]::new);

        ArtistObject artist = playlistService.getArtist(userID, token, artistID);
        SimplifiedPlaylistObject[] playlists = playlistService.getAllUserPlaylists(userID, token);

        List<String> playlistsForArtist = Arrays.stream(playlists)
                .filter(playlist -> playlist.name().equals(artist.name()))
                .map(SimplifiedPlaylistObject::id)
                .toList();

        String playlistToAddID;

        if (!playlistsForArtist.isEmpty()) {
            playlistToAddID = playlistsForArtist.getFirst();
        } else {
            playlistToAddID = playlistService.addPlaylist(userID, spotifyID, token,
                    new AddPlaylistRequest(
                            artist.name(),
                            false,
                            false,
                            "This is my playlist for " + artist.name()
                    )
            ).id();
        }

        String[] songsAlreadyAdded = Arrays.stream(playlistService.getAllSongsInPlaylist(userID, token, playlistToAddID))
                .filter(song ->
                        Arrays.stream(song.track().artists())
                                .anyMatch(artistInPlaylist -> artistInPlaylist.id().equals(artistID)))
                .map(song -> song.track().uri())
                .toArray(String[]::new);

        songsToAdd = Arrays.stream(songsToAdd)
                .filter(song -> !Arrays.asList(songsAlreadyAdded).contains(song))
                .toArray(String[]::new);

        if (ArrayUtils.isEmpty(songsToAdd)) {
            return null;
        }

        ItemsInPlaylistBody body = new ItemsInPlaylistBody(songsToAdd, 0);

        try {
            this.webClient.post()
                    .uri("/playlists/" + playlistToAddID + "/tracks")
                    .body(BodyInserters.fromValue(body))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve().bodyToMono(AddItemsToPlaylistResponse.class).block();

            logger.info("Added {}'s songs from saved songs to playlist {} for user with id = {}!",
                    artist.name(), playlistToAddID, userID);

            return playlistService.getPlaylist(userID, token, playlistToAddID);
        } catch (WebClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PlaylistTrackObject[] getAllSavedSongs(String userID, String token) {
        logger.info("Getting all saved songs for user with id = {}!", userID);

        PlaylistTrackObject[] songs;

        int offset = 0;
        PagedTracksResponse response = getSavedSongs(userID, token, 50, offset);

        songs = response.items();

        while (response.next() != null) {
            offset += 50;
            response = getSavedSongs(userID, token, 50, offset);
            songs = ArrayUtils.addAll(songs, response.items());
        }

        logger.info("Successfully returned all {} saved songs for user with userID = {}", songs.length, userID);

        return songs;
    }

    public PagedTracksResponse getSavedSongs(String userID, String token, Integer limit, Integer offset) {
        if (Objects.isNull(userID) || userID.isEmpty() ||
                Objects.isNull(token) || token.isEmpty() ||
                Objects.isNull(limit) || limit < 0 ||
                Objects.isNull(offset) || offset < 0) {
            logger.warn("Failed to get saved songs for user with id = {}!\n UserID or token = {} " +
                    "limit = {} or offset = {} is invalid!", userID, token, limit, offset);
            return null;
        }

        logger.info("Getting saved songs with limit = {} and offset = {} for user with id = {}!",
                limit, offset, userID);

        GetPlaylistItemsRequest request = new GetPlaylistItemsRequest(null, "RO", "",
                limit, offset, "");

        try {
            PagedTracksResponse pagedTracks = this.webClient.get()
                    .uri("/me" +
                            String.format("/tracks?market=%s&limit=%d&offset=%d",
                                    request.market(), request.limit(), request.offset()))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve().bodyToMono(PagedTracksResponse.class).block();

            assert pagedTracks != null;

            logger.info("Successfully returned pagedTracks = {} for user with token = {}", pagedTracks, token);

            return pagedTracks;
        } catch (WebClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeAllSavedSongs(String userID, String token) {
        logger.info("Removing all saved songs for user with id = {}!", userID);

        try {
            PlaylistTrackObject[] songs = getAllSavedSongs(userID, token);

            int offset = 0;

            while (offset < songs.length) {
                String[] songsToRemove = Arrays.stream(songs)
                        .skip(offset)
                        .limit(50)
                        .map(song -> song.track().id())
                        .toArray(String[]::new);

                if(ArrayUtils.isEmpty(songsToRemove)) {
                    break;
                }

                this.webClient.delete()
                        .uri("/me/tracks?ids=" + String.join(",", songsToRemove))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .retrieve().bodyToMono(String[].class).block();

                offset += 50;
            }

            logger.info("Removed all saved songs for user with id = {}!", userID);

        } catch (WebClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SimplifiedArtistObject[] getArtistsInSavedPlaylist(String userID, String token) {
        logger.info("Getting all artists in saved songs for user with id = {}!", userID);

        SimplifiedArtistObject[] artists = Arrays.stream(getAllSavedSongs(userID, token))
                .map(song -> song.track().artists())
                .flatMap(Arrays::stream)
                .distinct()
                .toArray(SimplifiedArtistObject[]::new);

        if (ArrayUtils.isEmpty(artists)) {
            return null;
        }

        logger.info("Successfully returned all artists in saved songs for user with id = {}", userID);

        return artists;
    }

    public AddItemsToPlaylistResponse addSavedSongsToPlaylist(String userID, String token,
                                                               String playlistID) {
        logger.info("Adding saved songs to playlist {} for user with id = {}!",
                playlistID, userID);

        PlaylistTrackObject[] songs1, songs2;
        songs1 = getAllSavedSongs(userID, token);
        songs2 = playlistService.getAllSongsInPlaylist(userID, token, playlistID);

        String[] songsToAdd = Arrays.stream(songs1)
                .filter(song -> !Arrays.asList(songs2).contains(song))
                .map(song -> song.track().uri())
                .toArray(String[]::new);

        if (ArrayUtils.isEmpty(songsToAdd)) {
            return null;
        }

        ItemsInPlaylistBody body = new ItemsInPlaylistBody(songsToAdd, 0);

        try {
            AddItemsToPlaylistResponse response = this.webClient.post()
                    .uri("/playlists/" + playlistID + "/tracks")
                    .body(BodyInserters.fromValue(body))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve().bodyToMono(AddItemsToPlaylistResponse.class).block();

            logger.info("Added saved songs to playlist {} for user with id = {}!",
                    playlistID, userID);

            return response;
        } catch (WebClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AddItemsToPlaylistResponse addPlaylistToSavedSongs(String userID, String token,
                                                              String playlistID) {
        logger.info("Adding songs from playlist {} to saved songs for user with id = {}!",
                playlistID, userID);

        PlaylistTrackObject[] songs1, songs2;
        songs1 = playlistService.getAllSongsInPlaylist(userID, token, playlistID);
        songs2 = getAllSavedSongs(userID, token);

        String[] songsToAdd = Arrays.stream(songs1)
                .filter(song -> !Arrays.asList(songs2).contains(song))
                .map(song -> song.track().id())
                .toArray(String[]::new);

        if (ArrayUtils.isEmpty(songsToAdd)) {
            return null;
        }

        SavedTracksBody body = new SavedTracksBody(songsToAdd, null);

        try {
            AddItemsToPlaylistResponse response = this.webClient.put()
                    .uri("/me/tracks")
                    .body(BodyInserters.fromValue(body))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve().bodyToMono(AddItemsToPlaylistResponse.class).block();

            logger.info("Added {} songs from playlist {} to saved songs for user with id = {}!",
                    songsToAdd.length, playlistID, userID);

            return response;
        } catch (WebClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
