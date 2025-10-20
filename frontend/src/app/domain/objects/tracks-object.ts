import { PlaylistTrackObject } from "./playlist-track-object";

export interface TracksObject {
  href: string;
  limit: number;
  next: string;
  offset: number;
  previous: string;
  total: number;
  items: PlaylistTrackObject[];
}
