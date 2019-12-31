import {Component, OnInit} from '@angular/core';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromConfiguration from '@feature/admin/configuration/reducers';
import {Observable} from 'rxjs';
import {Configuration} from '@model/config/configuration';
import {User} from '@model/user/user';
import {Load} from '@feature/allu/actions/user-actions';

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
    this.store.dispatch(new Load());
    this.configurations$ = this.store.pipe(select(fromConfiguration.getEditableConfigurations));
    this.users$ = this.store.pipe(select(fromRoot.getActiveUsers));
  }
}
