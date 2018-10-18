import {Component, ElementRef, Input} from '@angular/core';
import {AlluThemeColor} from '@model/common/allu-theme-color';

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
export class AlluCardComponent {

  constructor(private elementRef: ElementRef) {}

  @Input() set borderSide(side: AlluCardBorderSide) {
    if (side) {
      const borderSideClass = `allu-card-border-${side}`;
      this.elementRef.nativeElement.classList.add(borderSideClass);
    }
  }

  @Input() set borderColor(color: AlluThemeColor) {
    if (color) {
      const borderColorClass = `allu-${color}`;
      this.elementRef.nativeElement.classList.add(borderColorClass);
    }
  }
}
