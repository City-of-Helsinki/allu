import {Component, Input, OnInit} from '@angular/core';
import {getEffectivePaymentTariff, Location} from '@model/common/location';
import {MapFeature} from '@feature/map/map-feature';
import {findTranslation, findTranslationWithDefault} from '@util/translations';
import {ApplicationType} from '@model/application/type/application-type';

@Component({
  selector: 'supervision-task-location',
  templateUrl: './supervision-task-location.component.html',
  styleUrls: ['./supervision-task-location.component.scss']
})
export class SupervisionTaskLocationComponent implements OnInit {
  @Input() taskId: number;
  @Input() applicationType: ApplicationType;
  @Input() relatedLocation: Location;
  @Input() locations: Location[] = [];

  mapFeatures: MapFeature[] = [];
  showPaymentTariff = false;
  displayMap = false;

  ngOnInit(): void {
    this.mapFeatures = this.locations.map(loc => ({id: loc.id, geometry: loc.geometry}));
    this.showPaymentTariff = [ApplicationType.EXCAVATION_ANNOUNCEMENT, ApplicationType.AREA_RENTAL]
      .indexOf(this.applicationType) >= 0;

    // fixes the map not always being displayed
    setTimeout(() => {
      this.displayMap = true;
    });
  }

  get paymentTariff() {
    const paymentTariff = getEffectivePaymentTariff(this.relatedLocation);
    if (paymentTariff === undefined) {
      return findTranslation('location.paymentTariffUndefined');
    } else {
      return findTranslationWithDefault('location.paymentTariffValue', 'tariff', paymentTariff);
    }
  }
}
