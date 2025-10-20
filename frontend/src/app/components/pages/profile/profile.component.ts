import {Component, inject, OnInit} from '@angular/core';
import {SpotifyService} from '../../../services/spotify/spotify-service';
import {ProfileResponse} from '../../../domain/responses/profile-response';
import {ImageObject} from '../../../domain/objects/image-object';
import {Card} from 'primeng/card';
import {Avatar} from 'primeng/avatar';
import {NavbarComponent} from '../../pure-components/navbar/navbar.component';
import {MenuItem} from 'primeng/api';
import {FlagPipe} from '../../../pipes/flag-pipe';
import {NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-profile',
  imports: [
    Card,
    Avatar,
    NavbarComponent,
    FlagPipe
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

  items: MenuItem[] = [];

  profile: ProfileResponse;
  userImage: ImageObject;

  spotifyService: SpotifyService = inject(SpotifyService)

  constructor() {
    this.profile = {
      country: "",
      display_name: "",
      email: "",
      explicit_content: {},
      external_urls: {
        spotify: "",
      },
      followers: {
        href: "",
        total: 0
      },
      href: "",
      id: "",
      images: [],
      product: "",
      type: "",
      uri: ""
    };

    this.userImage = {
      url: "",
      width: 300,
      height: 300,
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
      {
        label: 'Add playlist',
        icon: 'pi pi-plus',
        route: "/playlists/new",
      },
    ];
  }

  async ngOnInit() {
    this.profile = await this.spotifyService.getProfile();
    this.userImage = this.profile.images[1];
  }

  protected readonly JSON = JSON;
}
