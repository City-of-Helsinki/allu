import {AfterViewInit, ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {UntypedFormArray, UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {Location} from '@model/common/location';
import {ArrayUtil} from '@util/array-util';
import {Subject} from 'rxjs/internal/Subject';
import {takeUntil} from 'rxjs/operators';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {SetLocation} from '@feature/information-request/actions/information-request-result-actions';

const DEFAULT_LOCATION_KEY = 1;

@Component({
  selector: 'locations-acceptance',
  templateUrl: './locations-acceptance.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LocationsAcceptanceComponent implements OnInit, AfterViewInit {
  @Input() parentForm: UntypedFormGroup;
  @Input() oldLocations: Location[] = [];
  @Input() newLocations: Location[] = [];
  @Input() readonly: boolean;
  @Input() hideExisting = false;

  locationKeys: number[];
  oldLocationsByKey: {[key: number]: Location} = {};
  newLocationsByKey: {[key: number]: Location} = {};
  locationForms: UntypedFormArray;

  private locationChanges$ = new Subject<Location>();
  private destroy: Subject<boolean> = new Subject<boolean>();

  constructor(private fb: UntypedFormBuilder, private store: Store<fromRoot.State>) {
  }

  ngOnInit(): void {
    this.locationForms = this.fb.array([]);
    this.parentForm.addControl('geometry', this.locationForms);
    this.oldLocationsByKey = this.byKey(this.oldLocations);
    this.newLocationsByKey = this.byKey(this.newLocations);
    this.locationKeys = this.getLocationKeys();
  }

  ngAfterViewInit(): void {
    this.locationChanges$.pipe(
      takeUntil(this.destroy)
    ).subscribe(location => this.store.dispatch(new SetLocation(location)));
  }

  onLocationChange(location: Location) {
    this.locationChanges$.next(location);
  }

  private getLocationKeys(): number[] {
    const oldKeys = Object.keys(this.oldLocationsByKey);
    const newKeys = Object.keys(this.newLocationsByKey);
    return oldKeys.concat(newKeys)
      .filter(ArrayUtil.unique)
      .map(key => Number(key));
  }

  private byKey(locations: Location[] = []): {[key: number]: Location} {
    return locations.reduce((prev: {[key: number]: Location}, cur: Location) => {
      const key = locations.length === 1 ?  DEFAULT_LOCATION_KEY : cur.locationKey;
      prev[key] = cur;
      return prev;
    }, {});
  }
}
