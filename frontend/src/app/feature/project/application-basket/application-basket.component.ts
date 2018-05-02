import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Application} from '../../../model/application/application';
import {Store} from '@ngrx/store';
import * as fromProject from '../reducers';
import {Clear, Remove} from '../actions/application-basket-actions';

@Component({
  selector: 'application-basket',
  templateUrl: './application-basket.component.html',
  styleUrls: [
    './application-basket.component.scss'
  ]
})
export class ApplicationBasketComponent implements OnInit {

  applications$: Observable<Application[]>;

  constructor(private store: Store<fromProject.State>) {}

  ngOnInit(): void {
    this.applications$ = this.store.select(fromProject.getAllApplicationsInBasket);
  }

  removeFromBasket(id: number): void {
    this.store.dispatch(new Remove(id));
  }

  clearBasket(event: MouseEvent): void {
    event.stopPropagation();
    this.store.dispatch(new Clear());
  }
}
