import {Component, inject, OnInit} from '@angular/core';
import {Button} from 'primeng/button';
import {Card} from 'primeng/card';
import {Checkbox} from 'primeng/checkbox';
import {FormsModule} from '@angular/forms';
import {SimplifiedPlaylistObject} from '../../../domain/objects/simplified-playlist-object';
import {Select} from 'primeng/select';
import {SpotifyService} from '../../../services/spotify/spotify-service';
import {AddPlaylistForm} from '../../pure-components/add-playlist-form/add-playlist-form';
import {PlaylistRequest} from '../../../domain/requests/playlist-request';
import {NavbarComponent} from '../../pure-components/navbar/navbar.component';
import {MenuItem} from 'primeng/api';

@Component({
  selector: 'app-merge-playlists',
  imports: [
    Button,
    Card,
    Checkbox,
    FormsModule,
    Select,
    AddPlaylistForm,
    NavbarComponent
  ],
  templateUrl: './merge-playlists.component.html',
  styleUrl: './merge-playlists.component.css'
})
export class MergePlaylistsComponent implements OnInit {
  items: MenuItem[] = [];

  spotifyService: SpotifyService = inject(SpotifyService);

  playlists: SimplifiedPlaylistObject[] = [];

  playlistsListOne: SimplifiedPlaylistObject[] = [];
  playlistsListTwo: SimplifiedPlaylistObject[] = [];

  playlistOne: SimplifiedPlaylistObject;
  playlistTwo: SimplifiedPlaylistObject;
  toANewPlaylist: boolean = false;

  mergedPlaylist: PlaylistRequest;

  constructor() {
    this.playlistOne = this.playlistTwo = {
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

    this.mergedPlaylist = {
      name: "",
      public: false,
      collaborative: false,
      description: "",
    }

    this.items = [
      {
        label: 'Home',
        icon: 'pi pi-home',
        route: "/",
      },
      {
        label: 'Split playlist',
        icon: 'pi pi-objects-column',
        route: "/playlists/split",
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

    this.playlists.push(...(await this.spotifyService.getPlaylists({limit: 20, offset: 0})).items);
    this.playlistsListOne = this.playlists;
    this.playlistsListTwo = this.playlists;
  }

  async mergePlaylists() {
    if ((this.toANewPlaylist && (this.mergedPlaylist.name == "" || this.mergedPlaylist.description == "")) ||
      this.playlistOne == null || this.playlistTwo == null) {
      return;
    }

    if (this.playlistOne.name == "Liked Songs") {
      await this.spotifyService.addLikedSongsToPlaylist(this.playlistTwo)
    } else {
      if (this.playlistTwo.name == "Liked Songs") {
        await this.spotifyService.addPlaylistToLikedSongs(this.playlistOne)
      } else if (this.toANewPlaylist) {
        await this.spotifyService.mergePlaylists(this.playlistOne, this.playlistTwo, this.mergedPlaylist);
      } else {
        await this.spotifyService.mergePlaylistsInPlace(this.playlistOne, this.playlistTwo);
      }
    }
  }

  selectedFirstPlaylist() {
    this.playlistsListTwo = this.playlists.filter(playlist => playlist.name !== this.playlistOne.name);
  }

  selectedSecondPlaylist() {
    this.playlistsListOne = this.playlists.filter(playlist => playlist.name !== this.playlistTwo.name);
  }

  updatePlaylist(event: any) {
    this.mergedPlaylist = event;
  }
}
