import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {generateRandomString, promiseFromObservable} from '../../utils/utils';
import {SERVER_URL} from '../../configs/config';
import {LinkResponse} from '../../domain/responses/link-response';
import {TokenResponse} from '../../domain/responses/token-response';
import {ProfileResponse} from '../../domain/responses/profile-response';
import {PlaylistRequest} from '../../domain/requests/playlist-request';
import {PagedPlaylistsResponse} from '../../domain/responses/paged-playlists-response';
import {GetPlaylistsRequest} from '../../domain/requests/get-playlists-request';
import {SimplifiedPlaylistObject} from '../../domain/objects/simplified-playlist-object';
import {AddItemsToPlaylistResponse} from '../../domain/responses/add-items-to-playlist-response';
import {PlaylistResponse} from '../../domain/responses/playlist-response';
import {SimplifiedArtistObject} from '../../domain/objects/simplified-artist-object';

@Injectable({
  providedIn: 'root'
})
export class SpotifyService {

  private URL = `${SERVER_URL}`;

  private http = inject(HttpClient);

  constructor() {
    this.setID();
  }

  async signIn() {
    const headers = new HttpHeaders({"Access-Control-Allow-Origin": "*"});
    let id = localStorage.getItem("unique_id")??this.setID();
    window.location.href = (await promiseFromObservable(this.http.get<LinkResponse>(`${this.URL}/login?id=${id}`, {headers: headers}))).link;
  }

  async signOut() {
    localStorage.removeItem("unique_id");
    localStorage.removeItem("access_token");
    localStorage.removeItem("access_token_response");

    const headers = new HttpHeaders({"Access-Control-Allow-Origin": "*"});
    let id = localStorage.getItem("unique_id")??this.setID();
    await promiseFromObservable(this.http.get<void>(`${this.URL}/login/disconnect?id=${id}`, {headers: headers}));
  }

  async getToken(code: string, state: string) {
    const headers = new HttpHeaders({"Access-Control-Allow-Origin": "*"});
    let id = localStorage.getItem("unique_id")??this.setID();
    let token = await promiseFromObservable(this.http.get<TokenResponse>(`${this.URL}/login/token/new?id=${id}&code=${code}&state=${state}`, {headers: headers}));

    localStorage.setItem("access_token_response", JSON.stringify(token));
    localStorage.setItem("access_token", token.access_token);
    localStorage.setItem("refresh_token", token.refresh_token);

    setTimeout(() => {
      this.refreshToken()
    }, token.expires_in * 1000);
  }

  async refreshToken() {
    const headers = new HttpHeaders({"Access-Control-Allow-Origin": "*"});
    let id = localStorage.getItem("unique_id")??this.setID();
    let refresh_token = localStorage.getItem("refresh_token")??"";
    let newToken = await promiseFromObservable(this.http.get<TokenResponse>(
      `${this.URL}/login/token/refresh?id=${id}&refresh_token=${refresh_token}`,
      {headers: headers}));

    localStorage.setItem("access_token_response", JSON.stringify(newToken));
    localStorage.setItem("access_token", newToken.access_token);

    setTimeout(()=> {
      this.refreshToken()
    }, newToken.expires_in * 1000);
  }

  async getProfile() {
    const headers = new HttpHeaders({"Access-Control-Allow-Origin": "*"});
    let token = localStorage.getItem("access_token")??this.setID();
    let profile: ProfileResponse = await promiseFromObservable(this.http.get<ProfileResponse>(
      `${this.URL}/profile?token=${token}`,
      {headers: headers}));
    localStorage.setItem("profile", JSON.stringify(profile));
    return profile;
  }

  private setID(): string {
    if (!localStorage.getItem("unique_id")) {
      let state = generateRandomString(10);
      localStorage.setItem("unique_id", state);
    }

    return localStorage.getItem("unique_id")??"";
  }

  async getPlaylists(request: GetPlaylistsRequest) {
    const headers = new HttpHeaders({"Access-Control-Allow-Origin": "*"});
    let id = localStorage.getItem("unique_id")??this.setID();
    let token= localStorage.getItem("access_token")?? "";

    return promiseFromObservable(this.http.get<PagedPlaylistsResponse>(
      `${this.URL}/playlists?userID=${id}&token=${token}&limit=${request.limit}&offset=${request.offset}`
      , {headers: headers}))
  }

  async getArtists(playlist: SimplifiedPlaylistObject) {
    const headers = new HttpHeaders({"Access-Control-Allow-Origin": "*"});
    let id = localStorage.getItem("unique_id")??this.setID();
    let token= localStorage.getItem("access_token")?? "";

    return promiseFromObservable(this.http.get<SimplifiedArtistObject[]>(
      `${this.URL}/playlists/${playlist.id}/artists?userID=${id}&token=${token}`, {headers: headers}))
  }

  async getSavedArtists() {
    const headers = new HttpHeaders({"Access-Control-Allow-Origin": "*"});
    let id = localStorage.getItem("unique_id")??this.setID();
    let token= localStorage.getItem("access_token")?? "";

    return promiseFromObservable(this.http.get<SimplifiedArtistObject[]>(
      `${this.URL}/saved/artists?userID=${id}&token=${token}`, {headers: headers}))
  }

  async splitPlaylistByArtist(playlist: SimplifiedPlaylistObject, artist: SimplifiedArtistObject) {
    const headers = new HttpHeaders({"Access-Control-Allow-Origin": "*"});
    let id = localStorage.getItem("unique_id")??this.setID();
    let token= localStorage.getItem("access_token")?? "";
    let profile: ProfileResponse = JSON.parse(localStorage.getItem("profile")??"") ?? await this.getProfile();

    return promiseFromObservable(this.http.post<PlaylistResponse>(
      `${this.URL}/playlists/${playlist.id}/artists/${artist.id}?userID=${id}&spotifyID=${profile.id}&token=${token}`,
      {}, {headers: headers}))
  }

  async splitSavedPlaylistByArtist(artist: SimplifiedArtistObject) {
    const headers = new HttpHeaders({"Access-Control-Allow-Origin": "*"});
    let id = localStorage.getItem("unique_id")??this.setID();
    let token= localStorage.getItem("access_token")?? "";
    let profile: ProfileResponse = JSON.parse(localStorage.getItem("profile")??"") ??
      await this.getProfile();

    return promiseFromObservable(this.http.post<PlaylistResponse>(
      `${this.URL}/saved/artists/${artist.id}?userID=${id}&spotifyID=${profile.id}&token=${token}`,
      {}, {headers: headers}))
  }

  async mergePlaylistsInPlace(playlistOne: SimplifiedPlaylistObject, playlistTwo: SimplifiedPlaylistObject) {
    const headers = new HttpHeaders({"Access-Control-Allow-Origin": "*"});
    let id = localStorage.getItem("unique_id")??this.setID();
    let token= localStorage.getItem("access_token")?? "";

    return promiseFromObservable(this.http.put<AddItemsToPlaylistResponse>(
      `${this.URL}/playlists/${playlistTwo.id}?userID=${id}&token=${token}&playlistOneID=${playlistOne.id}`,
      {headers: headers}))
  }

  async addSavedSongsToPlaylist(playlist: SimplifiedPlaylistObject) {
    const headers = new HttpHeaders({"Access-Control-Allow-Origin": "*"});
    let id = localStorage.getItem("unique_id")??this.setID();
    let token= localStorage.getItem("access_token")?? "";

    return promiseFromObservable(this.http.put<AddItemsToPlaylistResponse>(
      `${this.URL}/playlists/${playlist.id}/saved?userID=${id}&token=${token}`,
      {headers: headers}))
  }

  async addPlaylistToSavedSongs(playlist: SimplifiedPlaylistObject) {
    const headers = new HttpHeaders({"Access-Control-Allow-Origin": "*"});
    let id = localStorage.getItem("unique_id")??this.setID();
    let token= localStorage.getItem("access_token")?? "";
    return promiseFromObservable(this.http.put<AddItemsToPlaylistResponse>(
      `${this.URL}/saved/${playlist.id}?userID=${id}&token=${token}`,
      {}, {headers: headers}))
  }

  async mergePlaylists(playlistOne: SimplifiedPlaylistObject, playlistTwo: SimplifiedPlaylistObject, request: PlaylistRequest) {
    const headers = new HttpHeaders({"Access-Control-Allow-Origin": "*"});
    let id = localStorage.getItem("unique_id")??this.setID();
    let token= localStorage.getItem("access_token")?? "";
    let profile: ProfileResponse = JSON.parse(localStorage.getItem("profile")??"") ?? await this.getProfile();

    return promiseFromObservable(this.http.post<PlaylistResponse>(
      `${this.URL}/playlists/merge?userID=${id}&spotifyID=${profile.id}&token=${token}&
      playlistOneID=${playlistOne.id}&playlistTwoID=${playlistTwo.id}`,
      request, {headers: headers}))
  }

  async addPlaylist(playlist: PlaylistRequest) {
    const headers = new HttpHeaders({"Access-Control-Allow-Origin": "*"});
    let id = localStorage.getItem("unique_id")??this.setID();
    let token= localStorage.getItem("access_token")?? "";
    let profile: ProfileResponse = JSON.parse(localStorage.getItem("profile")??"") ?? await this.getProfile();

    return promiseFromObservable(this.http.post<PlaylistResponse>(
      `${this.URL}/playlists/new?userID=${id}&spotifyID=${profile.id}&token=${token}`,
      playlist, {headers: headers}))
  }

}
