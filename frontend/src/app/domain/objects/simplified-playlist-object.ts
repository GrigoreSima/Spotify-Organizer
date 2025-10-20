import {ExternalURLObject} from './external-urlobject';
import {ImageObject} from './image-object';
import {OwnerObject} from './owner-object';
import {FollowersObject} from './followers-object';

export interface SimplifiedPlaylistObject {
  collaborative: boolean;
  description: string;
  external_urls: ExternalURLObject;
  href: string;
  id: string;
  images: ImageObject[];
  name: string;
  owner: OwnerObject;
  public: boolean;
  snapshot_id: string;
  tracks: FollowersObject;
  type: string;
  uri: string;
}
