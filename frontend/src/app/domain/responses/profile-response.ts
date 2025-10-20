import {ImageObject} from '../objects/image-object';
import {ExternalURLObject} from '../objects/external-urlobject';
import {FollowersObject} from '../objects/followers-object';

export interface ProfileResponse {
  country: string;
  display_name: string;
  email: string;
  explicit_content: object;
  external_urls: ExternalURLObject;
  followers: FollowersObject;
  href: string;
  id: string;
  images: ImageObject[];
  product: string;
  type: string;
  uri: string;
}
