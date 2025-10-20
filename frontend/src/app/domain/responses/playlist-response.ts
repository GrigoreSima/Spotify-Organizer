import {ExternalURLObject} from '../objects/external-urlobject';
import {ImageObject} from '../objects/image-object';
import { OwnerObject } from "../objects/owner-object";
import {TracksObject} from '../objects/tracks-object';

export interface PlaylistResponse {
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
  tracks: TracksObject;
  type: string;
  uri: string;
}
