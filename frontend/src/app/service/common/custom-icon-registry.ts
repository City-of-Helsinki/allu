import {Injectable} from '@angular/core';
import {MatIconRegistry} from '@angular/material';
import {DomSanitizer} from '@angular/platform-browser';

const ICON_BASE_PATH = 'assets/icon';

@Injectable()
export class CustomIconRegistry {
  constructor(private iconRegistry: MatIconRegistry, private sanitizer: DomSanitizer) {
    this.addIcon('bring_from_basket', 'bring_from_basket.svg');
  }

  private addIcon(name: string, fileName: string): void {
    const url = `${ICON_BASE_PATH}/${fileName}`;
    this.iconRegistry.addSvgIcon(name, this.sanitizer.bypassSecurityTrustResourceUrl(url));
  }
}
