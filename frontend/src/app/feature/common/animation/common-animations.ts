import {animate, state, style, transition, trigger} from '@angular/animations';

export const shrinkFadeInOut = trigger('shrinkFadeInOut', [
  state('visible', style({height: '*'})),
  state('void', style({height: 0, opacity: 0})),
  transition('visible <=> void', [
    animate('250ms ease-out')
  ])
]);
