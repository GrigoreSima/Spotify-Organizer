import {Component, inject, OnInit} from '@angular/core';
import {Button} from 'primeng/button';
import {Card} from 'primeng/card';
import {Select} from 'primeng/select';
import {SimplifiedPlaylistObject} from '../../../domain/objects/simplified-playlist-object';
import {SpotifyService} from '../../../services/spotify/spotify-service';
import {SimplifiedArtistObject} from '../../../domain/objects/simplified-artist-object';
import {FormsModule} from '@angular/forms';
import {NavbarComponent} from '../../pure-components/navbar/navbar.component';
import {MenuItem} from 'primeng/api';

@Component({
  selector: 'app-split-playlist',
  imports: [
    Button,
    Card,
    Select,
    FormsModule,
    NavbarComponent
  ],
  templateUrl: './split-playlist.component.html',
  styleUrl: './split-playlist.component.css'
})
export class SplitPlaylistComponent implements OnInit {
  items: MenuItem[] = [];

  spotifyService: SpotifyService = inject(SpotifyService);
  playlists: SimplifiedPlaylistObject[] = [];
  playlist: SimplifiedPlaylistObject;
  artists: SimplifiedArtistObject[] = [];
  artist: SimplifiedArtistObject;

  constructor() {
    this.playlist = {
      collaborative: false,
      description: "",
      external_urls: {
        spotify: ""
      },
      href: "",
      id: "",
      images: [],
      name: "",
      owner: {
        external_urls: {
          spotify: ""
        },
        href: "",
        id: "",
        type: "",
        uri: "",
        display_name: "",
      },
      public: false,
      snapshot_id: "",
      tracks: {
        href: "",
        total: 0
      },
      type: "",
      uri: "",
    }

    this.artist = {
      external_urls: {
        spotify: ""
      },
      href: "",
      id: "",
      name: "",
      type: "",
      uri: ""
    }

    this.items = [
      {
        label: 'Home',
        icon: 'pi pi-home',
        route: "/",
      },
      {
        label: 'Merge playlists',
        icon: 'pi pi-table',
        route: "/playlists/merge",
      },
      {
        label: 'Add playlist',
        icon: 'pi pi-plus',
        route: "/playlists/new",
      },
    ];
  }

  async ngOnInit() {
    this.playlists = [{
      collaborative: false,
      description: "",
      external_urls: {
        spotify: ""
      },
      href: "",
      id: "-1",
      images: [],
      name: "Liked Songs",
      owner: {
        external_urls: {
          spotify: ""
        },
        href: "",
        id: "",
        type: "",
        uri: "",
        display_name: "",
      },
      public: false,
      snapshot_id: "",
      tracks: {
        href: "",
        total: 0
      },
      type: "",
      uri: "",
    }]
    this.playlists.push(...(await this.spotifyService.getPlaylists({limit: 20, offset: 0})).items)
  }

  async selectedPlaylist() {
    if(this.playlist.name == "Liked Songs"){
      this.artists = (await this.spotifyService.getLikedArtists());
    }
    else {
      this.artists = (await this.spotifyService.getArtists(this.playlist));
    }
  }

  async splitPlaylist() {
    if (this.playlist.id == "" || this.artist.id == "") {
      return;
    }

    if(this.playlist.name == "Liked Songs"){
      await this.spotifyService.splitLikedPlaylistByArtist(this.artist);
    }
    else {
      await this.spotifyService.splitPlaylistByArtist(this.playlist, this.artist);
    }
  }
}
