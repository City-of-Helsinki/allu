import {Component} from '@angular/core';

export enum AlluCardBorderSide {
  left = 'left',
  right = 'right',
  top = 'top',
  bottom = 'bottom',
}

@Component({
  selector: 'allu-card',
  templateUrl: './allu-card.component.html',
  styleUrls: ['./allu-card.component.scss']
})
export class AlluCardComponent {}
