import {SimplifiedPlaylistObject} from '../objects/simplified-playlist-object';

export interface PagedPlaylistsResponse {
  href: string;
  limit: number;
  next: string;
  offset: number;
  previous: string;
  total: number;
  items: SimplifiedPlaylistObject[];
}
