import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddPlaylistForm } from './add-playlist-form';

describe('AddPlaylistForm', () => {
  let component: AddPlaylistForm;
  let fixture: ComponentFixture<AddPlaylistForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddPlaylistForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddPlaylistForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
