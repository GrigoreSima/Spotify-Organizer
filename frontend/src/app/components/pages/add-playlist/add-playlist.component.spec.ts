import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddPlaylist } from './add-playlist.component';

describe('PlaylistForm', () => {
  let component: AddPlaylist;
  let fixture: ComponentFixture<AddPlaylist>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddPlaylist]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddPlaylist);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
