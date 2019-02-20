import {Component, Input} from '@angular/core';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Add} from '@feature/project/actions/application-basket-actions';

@Component({
  selector: 'map-popup',
  templateUrl: './map-popup.component.html',
  styleUrls: ['./map-popup.component.scss']
})
export class MapPopupComponent {
  @Input() header;
  @Input() contentRows: MapPopupContentRow[];

  constructor(private store: Store<fromRoot.State>) {}

  addToBasket(applicationId: number): void {
    this.store.dispatch(new Add(applicationId));
  }
}

export interface MapPopupContentRow {
  content: string;
  link?: string;
  class?: string;
  idForBasket?: number;
}
