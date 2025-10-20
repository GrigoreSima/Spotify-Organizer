import {AddedByObject} from './added-by-object';
import {TrackObject} from './track-object';

export interface PlaylistTrackObject {
  added_at: string;
  added_by: AddedByObject;
  is_local: boolean;
  track: TrackObject;
}
