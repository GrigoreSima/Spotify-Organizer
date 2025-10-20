import {AlbumObject} from './album-object';
import {SimplifiedArtistObject} from './simplified-artist-object';
import {ExternalIDsObject} from './external-ids-object';
import {ExternalURLObject} from './external-urlobject';
import {RestrictionObject} from './restriction-object';

export interface TrackObject {
  album: AlbumObject;
  artists: SimplifiedArtistObject[];
  available_markets: string[];
  disc_number: number;
  duration_ms: number;
  explicit: boolean;
  external_ids: ExternalIDsObject;
  external_urls: ExternalURLObject;
  href: string;
  id: string;
  is_playable: boolean;
  restrictions: RestrictionObject;
  name: string;
  popularity: number;
  preview_url: string;
  track_number: number;
  type: string;
  uri: string;
  is_local: boolean;
}
