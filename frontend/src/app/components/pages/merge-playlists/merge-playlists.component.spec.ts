import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MergePlaylistsComponent } from './merge-playlists.component';

describe('MergePlaylistsComponent', () => {
  let component: MergePlaylistsComponent;
  let fixture: ComponentFixture<MergePlaylistsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MergePlaylistsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MergePlaylistsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
