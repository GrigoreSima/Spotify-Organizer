import { Routes } from '@angular/router';
import {LoginComponent} from './components/pages/login/login.component';
import {ProfileComponent} from './components/pages/profile/profile.component';
import {callbackGuard} from './guards/callback-guard';
import {HomeComponent} from './components/pages/home/home.component';
import {MergePlaylistsComponent} from './components/pages/merge-playlists/merge-playlists.component';
import {SplitPlaylistComponent} from './components/pages/split-playlist/split-playlist.component';
import {AddPlaylist} from './components/pages/add-playlist/add-playlist.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'callback',
    component: HomeComponent,
    canActivate: [callbackGuard],
  },
  {
    path: '',
    component: HomeComponent,
  },
  {
    path: 'playlists/merge',
    component: MergePlaylistsComponent,
  },
  {
    path: 'playlists/split',
    component: SplitPlaylistComponent,
  },
  {
    path: 'playlists/new',
    component: AddPlaylist,
  },
  {
    path: 'profile',
    component: ProfileComponent,
  },
];
