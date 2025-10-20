import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { callbackGuard } from './callback-guard';

describe('callbackGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => callbackGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
