import {Component, OnInit} from '@angular/core';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Observable} from 'rxjs';
import {Configuration} from '@model/config/configuration';
import {User} from '@model/user/user';

@Component({
  selector: 'configuration',
  templateUrl: './configuration.component.html',
  styleUrls: ['./configuration.component.scss']
})
export class ConfigurationComponent implements OnInit {

  configurations$: Observable<Configuration[]>;
  users$: Observable<User[]>;

  constructor(private store: Store<fromRoot.State>) {
  }

  ngOnInit(): void {
    this.configurations$ = this.store.pipe(select(fromRoot.getAllConfigurations));
    this.users$ = this.store.pipe(select(fromRoot.getActiveUsers));
  }
}
