import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {Application} from '@model/application/application';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Observable} from 'rxjs';
import {Location} from '@model/common/location';
import {filter, map} from 'rxjs/operators';

@Component({
  selector: 'application-basic-info',
  templateUrl: './application.basic-info.component.html',
  styleUrls: ['./application.basic-info.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApplicationBasicInfoComponent implements OnInit {
  @Input() application: Application;

  location: Location;
  cityDistrict$: Observable<string>;

  constructor(private store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    this.location = this.application.firstLocation;

    this.cityDistrict$ = this.store.pipe(
      select(fromRoot.getCityDistrictById(this.location.effectiveCityDistrictId)),
      filter(district => !!district),
      map(district => district.name)
    );
  }
}
