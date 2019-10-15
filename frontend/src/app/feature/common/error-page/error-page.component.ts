import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {select, Store} from '@ngrx/store';
import {Observable} from 'rxjs';
import {ErrorInfo} from '@service/error/error-info';
import * as fromNotification from '@feature/notification/reducers';
import {filter, map} from 'rxjs/operators';
import {APP_CONFIG_URL, AppConfig, LocalLoaderService} from '@feature/common/local-loader/local-loader.service';

@Component({
  selector: 'error-page',
  templateUrl: './error-page.component.html',
  styleUrls: ['./error-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ErrorPageComponent implements OnInit {
  constructor(public store: Store<fromNotification.State>, private localLoader: LocalLoaderService) {}

  error$: Observable<ErrorInfo>;
  contactInfo$: Observable<string>;

  ngOnInit(): void {
    this.error$ = this.store.pipe(
      select(fromNotification.getError),
      filter(error => !!error)
    );

    this.contactInfo$ = this.localLoader.load<AppConfig>(APP_CONFIG_URL).pipe(
      map(config => config.contactInfo)
    );
  }
}
