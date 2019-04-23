import {AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Observable} from 'rxjs';

import {MapStore} from '@service/map/map-store';
import {Application} from '@model/application/application';
import {Location} from '@model/common/location';
import {ApplicationType} from '@model/application/type/application-type';
import {LocationState} from '@service/application/location-state';
import {applicationCanBeEdited} from '@model/application/application-status';
import {MODIFY_ROLES, RoleType} from '@model/user/role-type';
import * as fromRoot from '@feature/allu/reducers';
import {select, Store} from '@ngrx/store';
import {map} from 'rxjs/internal/operators';
import {findTranslation, findTranslationWithDefault} from '@app/util/translations';
import * as fromLocationMapLayers from '@feature/application/location/reducers';
import {MapLayer} from '@service/map/map-layer';
import {needsPaymentTariff} from '@feature/common/payment-tariff';
import {fixedLocationInfo, groupByArea} from '@model/common/fixed-location';

@Component({
  selector: 'location-details',
  viewProviders: [],
  templateUrl: './location-details.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LocationDetailsComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input() readonly: boolean;

  MODIFY_ROLES = MODIFY_ROLES.map(role => RoleType[role]);

  location: Location;
  multipleLocations = false;
  canBeEdited = true;
  selectedLayersIds$: Observable<string[]>;
  availableLayerIds$: Observable<string[] | number[]>;
  selectedLayers$: Observable<MapLayer[]>;
  availableLayers$: Observable<MapLayer[]>;
  fixedLocationInfo$: Observable<string>;

  private _application: Application;

  constructor(private mapStore: MapStore,
              private locationState: LocationState,
              private store: Store<fromRoot.State>) {
  }

  @Input() set application(application: Application) {
    this._application = application;

    this.location = this._application.firstLocation;
    this.canBeEdited = applicationCanBeEdited(this._application);
    this.locationState.initLocations(this._application.locations);
    this.multipleLocations = this._application.type === ApplicationType[ApplicationType.AREA_RENTAL];

    this.fixedLocationInfo$ = this.store.pipe(
      select(fromRoot.getFixedLocationsByIds(this.location.fixedLocationIds)),
      map(fls => groupByArea(fls)),
      map(grouped => Object.keys(grouped).map(key => fixedLocationInfo(key, grouped[key]))),
      map(info => info.join(', '))
    );

    this.mapStore.editedLocation.subscribe(loc => this.editLocation(loc));
  }

  get application() {
    return this._application;
  }

  ngOnInit(): void {
    this.availableLayerIds$ = this.store.pipe(select(fromLocationMapLayers.getLayerIds));
    this.selectedLayersIds$ = this.store.pipe(select(fromLocationMapLayers.getSelectedLayerIds));
    this.availableLayers$ = this.store.pipe(select(fromLocationMapLayers.getAllLayers));
    this.selectedLayers$ = this.store.pipe(select(fromLocationMapLayers.getSelectedLayers));
  }

  ngAfterViewInit(): void {
    this.mapStore.selectedApplicationChange(this.application);
  }

  ngOnDestroy(): void {
    this.mapStore.reset();
  }

  get showPaymentTariff(): boolean {
    if (this.application) {
      return needsPaymentTariff(this.application.type, this.application.kinds);
    } else {
      return false;
    }
  }

  paymentTariff(): string {
    return this.paymentTariffText(this.location.paymentTariff);
  }

  paymentTariffOverride(): string {
    return this.paymentTariffText(this.location.paymentTariffOverride);
  }

  paymentTariffText(paymentTariff: string): string {
    if (paymentTariff) {
      if (paymentTariff === 'undefined') {
        return findTranslation('location.paymentTariffUndefined');
      } else {
        return findTranslationWithDefault('location.paymentTariffValue', 'tariff', paymentTariff);
      }
    } else {
      return '';
    }
  }

  districtName(id: number): Observable<string> {
    return this.store.select(fromRoot.getCityDistrictName(id));
  }

  private editLocation(loc: Location): void {
    if (!!loc) {
      this.location = loc;
    }
  }
}
