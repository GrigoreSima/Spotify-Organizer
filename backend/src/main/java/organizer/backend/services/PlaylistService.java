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
import organizer.backend.domain.requests.AddPlaylistRequest;
import organizer.backend.domain.requests.GetPlaylistItemsRequest;
import organizer.backend.domain.requests.GetPlaylistRequest;
import organizer.backend.domain.requests.GetPlaylistsRequest;
import organizer.backend.domain.responses.AddItemsToPlaylistResponse;
import organizer.backend.domain.responses.PagedPlaylistsResponse;
import organizer.backend.domain.responses.PagedTracksResponse;
import organizer.backend.domain.responses.PlaylistResponse;

import java.util.*;

@Service
public class PlaylistService {
    Logger logger = LoggerFactory.getLogger(PlaylistService.class);

    private final WebClient webClient;

    public PlaylistService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.spotify.com/v1").build();
    }

    public PlaylistObject getPlaylist(String userID, String token, String playlistID) {
        if (Objects.isNull(userID) || userID.isEmpty() ||
                Objects.isNull(token) || token.isEmpty() ||
                Objects.isNull(playlistID) || playlistID.isEmpty()) {
            logger.warn("Failed to create playlist for user with id = {}!\n UserID or token = {} or playlistID = {} " +
                    "is invalid!", userID, token, playlistID);
            return null;
        }

        logger.info("Getting playlist {} for user with id = {}!", playlistID, userID);

        GetPlaylistRequest request = new GetPlaylistRequest(playlistID, "RO", "", "");

        try {

            PlaylistObject playlist = this.webClient.get()
                    .uri("/playlists/" + playlistID +
                            String.format("?market=%s&fields=%s&additional_types=%s",
                                    request.market(), request.fields(),
                                    request.additional_types()))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve().bodyToMono(PlaylistObject.class).block();

            assert playlist != null;

            logger.info("Successfully returned playlist = {} for user with token = {}", playlist, token);

            return playlist;
        } catch (WebClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SimplifiedArtistObject[] getArtistsInPlaylist(String userID, String token, String playlistID) {
        logger.info("Getting all artists in playlist {} for user with id = {}!",
                playlistID, userID);

        SimplifiedArtistObject[] artists = Arrays.stream(getAllSongsInPlaylist(userID, token, playlistID))
                .map(song -> song.track().artists())
                .flatMap(Arrays::stream)
                .distinct()
                .toArray(SimplifiedArtistObject[]::new);

        if (ArrayUtils.isEmpty(artists)) {
            return null;
        }

        logger.info("Successfully returned all artists in playlist {} for user with id = {}", playlistID, userID);

        return artists;
    }

    public PlaylistObject splitPlaylistByArtist(String userID, String spotifyID, String token,
                                                String playlistID, String artistID) {
        logger.info("Splitting playlist {} by artist {} for user with id = {}!",
                playlistID, artistID, userID);

        PlaylistTrackObject[] songs = getAllSongsInPlaylist(userID, token, playlistID);

        String[] songsToAdd = Arrays.stream(songs)
                .filter(song ->
                        Arrays.stream(song.track().artists())
                                .anyMatch(artist -> artist.id().equals(artistID)))
                .map(song -> song.track().uri())
                .toArray(String[]::new);

        ArtistObject artist = getArtist(userID, token, artistID);
        SimplifiedPlaylistObject[] playlists = getAllUserPlaylists(userID, token);

        List<String> playlistsForArtist = Arrays.stream(playlists)
                .filter(playlist -> playlist.name().equals(artist.name()))
                .map(SimplifiedPlaylistObject::id)
                .toList();

        String playlistToAddID;

        if (!playlistsForArtist.isEmpty()) {
            playlistToAddID = playlistsForArtist.getFirst();
        } else {
            playlistToAddID = addPlaylist(userID, spotifyID, token,
                    new AddPlaylistRequest(
                            artist.name(),
                            false,
                            false,
                            "This is my playlist for " + artist.name()
                    )
            ).id();
        }

        String[] songsAlreadyAdded = Arrays.stream(getAllSongsInPlaylist(userID, token, playlistToAddID))
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

            logger.info("Added {}'s songs from playlist {} to playlist {} for user with id = {}!",
                    artist.name(), playlistID, playlistToAddID, userID);

            return getPlaylist(userID, token, playlistToAddID);
        } catch (WebClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArtistObject getArtist(String userID, String token, String artistID) {
        logger.info("Getting artist {} for user with id = {}!",
                artistID, userID);

        try {
            ArtistObject response = this.webClient.get()
                    .uri("/artists/" + artistID)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve().bodyToMono(ArtistObject.class).block();

            logger.info("Got artist {} for user with id = {}!",
                    artistID, userID);

            return response;
        } catch (WebClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AddItemsToPlaylistResponse mergeTwoPlaylistsInPlace(String userID, String token,
                                                               String playlistOneID, String playlistTwoID) {
        logger.info("Adding songs from playlist {} to playlist {} for user with id = {}!",
                playlistOneID, playlistTwoID, userID);

        PlaylistTrackObject[] songs1, songs2;
        songs1 = getAllSongsInPlaylist(userID, token, playlistOneID);
        songs2 = getAllSongsInPlaylist(userID, token, playlistTwoID);

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
                    .uri("/playlists/" + playlistTwoID + "/tracks")
                    .body(BodyInserters.fromValue(body))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve().bodyToMono(AddItemsToPlaylistResponse.class).block();

            logger.info("Added {} songs from playlist {} to playlist {} for user with id = {}!",
                    songsToAdd.length, playlistOneID, playlistTwoID, userID);

            return response;
        } catch (WebClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PlaylistResponse mergeTwoPlaylists(String userID, String spotifyID, String token,
                                              String playlistOneID, String playlistTwoID,
                                              AddPlaylistRequest request) {
        logger.info("Merging playlist {} with playlist {} in a new playlist for user with id = {}!",
                playlistOneID, playlistTwoID, userID);

        PlaylistResponse playlist = this.addPlaylist(userID, spotifyID, token, request);

        PlaylistTrackObject[] songs1, songs2;
        songs1 = getAllSongsInPlaylist(userID, token, playlistOneID);
        songs2 = getAllSongsInPlaylist(userID, token, playlistTwoID);

        String[] songsToAdd = Arrays.stream(ArrayUtils.addAll(songs1, songs2))
                .distinct()
                .map(song -> song.track().uri())
                .toArray(String[]::new);

        if (ArrayUtils.isEmpty(songsToAdd)) {
            return null;
        }

        ItemsInPlaylistBody body = new ItemsInPlaylistBody(songsToAdd, 0);

        try {
            this.webClient.post()
                    .uri("/playlists/" + playlist.id() + "/tracks")
                    .body(BodyInserters.fromValue(body))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve().bodyToMono(AddItemsToPlaylistResponse.class).block();

            logger.info("Merged playlist {} with playlist {} in result playlist {} for user with id = {}!",
                    playlistOneID, playlistTwoID, playlist.id(), userID);

            return playlist;
        } catch (WebClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PlaylistTrackObject[] getAllSongsInPlaylist(String userID, String token, String playlistID) {
        logger.info("Getting all songs in playlist {} for user with id = {}!", playlistID, userID);

        PlaylistTrackObject[] songs;

        int offset = 0;
        PagedTracksResponse response = getSongsInPlaylist(userID, token, playlistID, 50, offset);

        songs = response.items();

        while (response.next() != null) {
            offset += 50;
            response = getSongsInPlaylist(userID, token, playlistID, 50, offset);
            songs = ArrayUtils.addAll(songs, response.items());
        }

        logger.info("Successfully returned all {} songs in playlist {} for user with userID = {}", songs.length, playlistID, userID);

        return songs;
    }

    public PagedTracksResponse getSongsInPlaylist(String userID, String token, String playlistID, Integer limit, Integer offset) {
        if (Objects.isNull(userID) || userID.isEmpty() ||
                Objects.isNull(token) || token.isEmpty() ||
                Objects.isNull(limit) || limit < 0 ||
                Objects.isNull(offset) || offset < 0 ||
                Objects.isNull(playlistID) || playlistID.isEmpty()) {
            logger.warn("Failed to get songs for user with id = {}!\n UserID or token = {} or playlistID = {} " +
                    "limit = {} or offset = {} is invalid!", userID, token, playlistID, limit, offset);
            return null;
        }

        logger.info("Getting songs in playlist {} with limit = {} and offset = {} for user with id = {}!",
                playlistID, limit, offset, userID);

        GetPlaylistItemsRequest request = new GetPlaylistItemsRequest(playlistID, "RO", "",
                limit, offset, "");

        try {
            PagedTracksResponse pagedTracks = this.webClient.get()
                    .uri("/playlists/" + playlistID +
                            String.format("/tracks?market=%s&fields=%s&limit=%d&offset=%d&additional_types=%s",
                                    request.market(), request.fields(),
                                    request.limit(), request.offset(), request.additional_types()))
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

    public SimplifiedPlaylistObject[] getAllUserPlaylists(String userID, String token) {
        logger.info("Getting all playlists for user with id = {}!", userID);

        SimplifiedPlaylistObject[] playlists;

        int offset = 0;
        PagedPlaylistsResponse response = getUserPlaylists(userID, token, new GetPlaylistsRequest(50, offset));

        playlists = response.items();

        while (response.next() != null) {
            offset += 50;
            response = getUserPlaylists(userID, token, new GetPlaylistsRequest(50, offset));
            playlists = ArrayUtils.addAll(playlists, response.items());
        }

        logger.info("Successfully returned all {} playlists for user with userID = {}", playlists.length, userID);

        return playlists;
    }

    public PagedPlaylistsResponse getUserPlaylists(String userID, String token, GetPlaylistsRequest request) {
        if (Objects.isNull(userID) || userID.isEmpty() ||
                Objects.isNull(token) || token.isEmpty() ||
                Objects.isNull(request)) {
            logger.warn("Failed to create playlist for user with id = {}!\n UserID or token = {} or playlist " +
                    "request = {} is invalid!", userID, token, request);
            return null;
        }

        logger.info("Getting playlists within limit = {} and offset = {} for user with id = {}!", request.limit(), request.offset(), userID);

        try {
            PagedPlaylistsResponse pagedPlaylists = this.webClient.get()
                    .uri("/me/playlists?limit=" + request.limit() + "&offset=" + request.offset())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve().bodyToMono(PagedPlaylistsResponse.class).block();

            assert pagedPlaylists != null;

            logger.info("Successfully returned pagedPlaylists = {} for user with token = {}", pagedPlaylists, token);

            return pagedPlaylists;
        } catch (WebClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PlaylistResponse addPlaylist(String userID, String spotifyID, String token, AddPlaylistRequest request) {
        if (Objects.isNull(userID) || userID.isEmpty() ||
                Objects.isNull(spotifyID) || spotifyID.isEmpty() ||
                Objects.isNull(token) || token.isEmpty() ||
                Objects.isNull(request)) {
            logger.warn("Failed to create playlist for user with id = {}!\n UserID or spotifyID = {} or token = {} or" +
                    " playlist request = {} is invalid!", userID, spotifyID, token, request);
            return null;
        }

        logger.info("Creating a new playlist for user with id = {}!", userID);

        try {
            PlaylistResponse playlist = this.webClient.post()
                    .uri("/users/" + spotifyID + "/playlists")
                    .body(BodyInserters.fromValue(request))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve().bodyToMono(PlaylistResponse.class).block();

            logger.info("Created playlist {} = {} for user with id = {} and spotifyID = {}!",
                    request.name(), playlist, userID, spotifyID);

            return playlist;
        } catch (WebClientResponseException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
