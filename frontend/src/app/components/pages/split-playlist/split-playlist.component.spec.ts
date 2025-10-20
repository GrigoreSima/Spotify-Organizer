import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SplitPlaylistComponent } from './split-playlist.component';

describe('SplitPlaylistComponent', () => {
  let component: SplitPlaylistComponent;
  let fixture: ComponentFixture<SplitPlaylistComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SplitPlaylistComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SplitPlaylistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
