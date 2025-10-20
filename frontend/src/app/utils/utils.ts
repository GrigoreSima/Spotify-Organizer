import {lastValueFrom, Observable, take} from 'rxjs';

export function promiseFromObservable<T>(observable: Observable<T>): Promise<T> {
  return lastValueFrom(observable.pipe(take(1)));
}

export  function generateRandomString(length: number): string {
  const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  let result = "";
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}
