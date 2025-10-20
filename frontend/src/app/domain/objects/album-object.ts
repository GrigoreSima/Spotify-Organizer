import {ExternalURLObject} from './external-urlobject';
import {ImageObject} from './image-object';
import {RestrictionObject} from './restriction-object';
import {SimplifiedArtistObject} from './simplified-artist-object';

export interface AlbumObject {
  album_type: string;
  total_tracks: number;
  available_markets: string[];
  external_urls: ExternalURLObject;
  href: string;
  id: string;
  images: ImageObject[];
  name: string;
  release_date: string;
  release_date_precision: string;
  restrictions: RestrictionObject;
  type: string;
  uri: string;
  artists: SimplifiedArtistObject[];
}
