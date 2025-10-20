import {Component, EventEmitter, Output} from '@angular/core';
import {FloatLabel} from 'primeng/floatlabel';
import {Checkbox} from 'primeng/checkbox';
import {FormsModule} from '@angular/forms';
import {InputText} from 'primeng/inputtext';
import {Textarea} from 'primeng/textarea';
import {PlaylistRequest} from '../../../domain/requests/playlist-request';

@Component({
  selector: 'app-add-playlist-form',
  imports: [
    FloatLabel,
    Checkbox,
    FormsModule,
    InputText,
    Textarea
  ],
  templateUrl: './add-playlist-form.html',
  styleUrl: './add-playlist-form.css'
})
export class AddPlaylistForm {
  @Output() playlistEmitter = new EventEmitter<PlaylistRequest>();
  playlist: PlaylistRequest;

  constructor() {
    this.playlist = {
      name: "",
      public: false,
      collaborative: false,
      description: "",
    }
  }

  updated() {
    this.playlistEmitter.emit(this.playlist);
  }
}
