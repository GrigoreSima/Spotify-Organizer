import {Component, inject, OnInit} from '@angular/core';
import {Card} from 'primeng/card';
import {PrimeTemplate, TreeNode} from 'primeng/api';
import {OrganizationChart} from 'primeng/organizationchart';
import {SpotifyService} from '../../../services/spotify/spotify-service';
import {ProfileResponse} from '../../../domain/responses/profile-response';
import {Avatar} from 'primeng/avatar';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [
    Card,
    OrganizationChart,
    PrimeTemplate,
    Avatar,
    RouterLink
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  profile: ProfileResponse;
  data: TreeNode[] = [];
  spotifyService: SpotifyService = inject(SpotifyService);

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
  }

  async ngOnInit() {
    this.profile = await this.spotifyService.getProfile();

    this.data = [
      {
        expanded: true,
        type: 'person',
        data: {
          image: this.profile.images[0].url,
          name: this.profile.display_name,
        },
        children: [
          {
            expanded: true,
            type: 'path',
            label: 'Profile',
            data: {
              icon: 'pi pi-user',
              route: '/profile',
            }
          },
          {
            expanded: true,
            type: 'path',
            label: 'Playlists',
            data: {
              icon: 'pi pi-list',
              route: '',
            },
            children: [
              {
                expanded: true,
                type: 'path',
                label: 'Add playlist',
                data: {
                  icon: 'pi pi-plus',
                  route: '/playlists/new',
                }
              },
              {
                expanded: true,
                type: 'path',
                label: 'Merge playlists',
                data: {
                  icon: 'pi pi-table',
                  route: '/playlists/merge',
                }
              },
              {
                expanded: true,
                type: 'path',
                label: 'Split playlist',
                data: {
                  icon: 'pi pi-objects-column',
                  route: '/playlists/split',
                }
              },
            ]
          },
        ]
      }
    ];
  }
}
