import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'flagPipe'
})
export class FlagPipe implements PipeTransform {

  transform(value: string): string {
    let img: string = 'flags/';

    switch (value) {
      case 'RO':
        img += 'ro.svg';
        break;

      case 'ES':
        img += 'es.svg';
        break;

      case 'UK':
        img += 'uk.svg';
        break;

      case 'FR':
        img += 'fr.svg';
        break;

      default:
        img = 'unknowImage.svg';
        break;
    }

    return img;
  }

}
