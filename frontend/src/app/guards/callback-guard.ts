import {CanActivateFn, RedirectCommand, Router} from '@angular/router';
import {SpotifyService} from '../services/spotify/spotify-service';
import {inject} from '@angular/core';

export const callbackGuard: CanActivateFn = async (route, state) => {

  let spotifyService: SpotifyService = inject(SpotifyService)
  let router: Router = inject(Router);

  if (!localStorage.getItem("access_token")) {
    let params = route.queryParamMap;
    let code = params.get('code') ?? "";
    let state = params.get('state') ?? "";

    await spotifyService.getToken(code, state);
  }

  return new RedirectCommand(router.parseUrl("/"));
};
