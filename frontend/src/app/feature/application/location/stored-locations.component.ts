import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {LocationState} from '@service/application/location-state';
import {Location} from '@model/common/location';
import * as fromRoot from '@feature/allu/reducers';
import {Store} from '@ngrx/store';
import {
  DATE_REPORTING_MODAL_CONFIG,
  DateReportingModalComponent,
  DateReportingModalData,
  ReportedDateType,
  ReporterType
} from '@feature/application/date-reporting/date-reporting-modal.component';
import {ReportLocationCustomerValidity} from '@feature/application/actions/date-reporting-actions';
import {DateReport} from '@model/application/date-report';
import {filter} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {ObjectUtil} from '@util/object.util';
import {NumberUtil} from '@util/number.util';

@Component({
  selector: 'stored-locations',
  viewProviders: [],
  templateUrl: './stored-locations.component.html',
  styleUrls: [
    './stored-locations.component.scss'
  ]
})
export class StoredLocationsComponent implements OnInit, OnDestroy {

  @Input() readonly = false;

  locations: Observable<Array<Location>>;

  constructor(
    public locationState: LocationState,
    private store: Store<fromRoot.State>,
    private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.locations = this.locationState.locations;
  }

  ngOnDestroy(): void {
  }

  districtName(id: number): Observable<string> {
    return this.store.select(fromRoot.getCityDistrictName(id));
  }

  editLocation(index: number): void {
    this.locationState.editLocation(index);
  }

  remove(index: number): void {
    this.locationState.removeLocation(index);
  }

  customerValidityReportingAvailable(location: Location): boolean {
    return NumberUtil.isExisting(location);
  }

  reportCustomerValidity(location: Location, index: number): void {
    const data: DateReportingModalData = {
      reporterType: ReporterType.CUSTOMER,
      dateType: ReportedDateType.VALIDITY,
      reportedDate: location.customerStartTime,
      reportedEndDate: location.customerEndTime,
      reportingDate: location.customerReportingTime
    };
    this.openDateReporting(data)
      .subscribe(dateReport => {
        this.store.dispatch(new ReportLocationCustomerValidity(location.id, dateReport));
        const updated: Location = ObjectUtil.clone(location);
        updated.customerStartTime = dateReport.reportedDate;
        updated.customerEndTime = dateReport.reportedEndDate;
        updated.customerReportingTime = dateReport.reportingDate;
        this.locationState.storeLocation(updated);
        this.locationState.editLocation(index);
      });
  }


  private openDateReporting(data: DateReportingModalData): Observable<DateReport> {
    return this.dialog.open(DateReportingModalComponent, {
      ...DATE_REPORTING_MODAL_CONFIG,
      data
    }).afterClosed().pipe(
      filter(result => !!result)
    );
  }
}
