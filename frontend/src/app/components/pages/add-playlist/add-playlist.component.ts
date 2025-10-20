import {Component, inject} from '@angular/core';
import {Card} from 'primeng/card';
import {PlaylistRequest} from '../../../domain/requests/playlist-request';
import {FormsModule} from '@angular/forms';
import {Button} from 'primeng/button';
import {SpotifyService} from '../../../services/spotify/spotify-service';
import {AddPlaylistForm} from '../../pure-components/add-playlist-form/add-playlist-form';
import {NavbarComponent} from '../../pure-components/navbar/navbar.component';
import {MenuItem} from 'primeng/api';

@Component({
  selector: 'app-add-playlist',
  imports: [
    Card,
    FormsModule,
    Button,
    AddPlaylistForm,
    NavbarComponent
  ],
  templateUrl: './add-playlist.component.html',
  styleUrl: './add-playlist.component.css'
})
export class AddPlaylist {
  spotifyService: SpotifyService = inject(SpotifyService);
  playlist: PlaylistRequest;
  items: MenuItem[] = [];


  constructor() {
    this.playlist = {
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
        label: 'Merge playlists',
        icon: 'pi pi-table',
        route: "/playlists/merge",
      },
    ];
  }

  async addPlaylist() {
    if (this.playlist.name == "" || this.playlist.description == "") {
      console.log("No such playlist");
      return;
    }
    try {
      await this.spotifyService.addPlaylist(this.playlist);
    } catch (e) {
    } finally {
    }
  }

  updatePlaylist(event: any) {
    this.playlist = event;
  }
}
