import {Component, inject, Input, OnInit} from '@angular/core';
import {ProfileResponse} from '../../../domain/responses/profile-response';
import {Router, RouterLink} from '@angular/router';
import {SpotifyService} from '../../../services/spotify/spotify-service';
import {MenuItem, MenuItemCommandEvent, MessageService} from 'primeng/api';
import {Menubar} from 'primeng/menubar';
import {Menu} from 'primeng/menu';
import {Avatar} from 'primeng/avatar';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  imports: [
    Menubar,
    RouterLink,
    Menu,
    Avatar
  ],
  providers: [MessageService]
})
export class NavbarComponent implements OnInit {

  profile: ProfileResponse;
  @Input() items: MenuItem[] = [];
  profileItems: MenuItem[] = [];

  private messageService: MessageService = inject(MessageService);

  private spotifyService = inject(SpotifyService);

  private router: Router = inject(Router);

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
      images: [{
        url: "",
        width: 0,
        height: 0
      }],
      product: "",
      type: "",
      uri: ""
    };

    this.profileItems = [
      {
        icon: "pi pi-user",
        label: "View profile",
        routerLink: "/profile"
      },
      {
        separator: true
      },
      {
        icon: "pi pi-sign-out",
        label: "Log out",
        command: async (event: MenuItemCommandEvent) => {
          await this.spotifyService.signOut();
          await this.router.navigate(["/login"]);
        }
      },
    ]
  }

  async ngOnInit() {
    try {
      this.profile = (await this.spotifyService.getProfile());
    } catch (e) {
      this.messageService.add({
        severity: "error",
        summary: "Register failed",
        detail: "Please verify the info you provided!",
      });
    }
  }
}
