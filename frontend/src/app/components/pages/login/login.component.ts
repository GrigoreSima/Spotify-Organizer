import {Component, inject, OnInit} from '@angular/core';
import {Button} from 'primeng/button';
import {SpotifyService} from '../../../services/spotify/spotify-service';
import {Avatar} from 'primeng/avatar';
import {Card} from 'primeng/card';
import {FlagPipe} from '../../../pipes/flag-pipe';
import {NavbarComponent} from '../../pure-components/navbar/navbar.component';

@Component({
  selector: 'app-login',
  imports: [
    Button,
    Avatar,
    Card,
    FlagPipe,
    NavbarComponent
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  spotifyService : SpotifyService = inject(SpotifyService);

  async ngOnInit() {
    await this.spotifyService.signOut();
  }

  async signIn() {
    await this.spotifyService.signIn();
  }
}
