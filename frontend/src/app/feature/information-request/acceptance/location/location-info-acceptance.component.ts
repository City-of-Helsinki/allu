import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {InfoAcceptanceDirective} from '@feature/information-request/acceptance/info-acceptance/info-acceptance.component';
import {FieldValues} from '@feature/information-request/acceptance/field-select/field-select.component';
import {UntypedFormBuilder} from '@angular/forms';
import {findTranslation} from '@util/translations';
import {Location} from '@model/common/location';
import {ObjectUtil} from '@util/object.util';
import {PostalAddress} from '@model/common/postal-address';
import {Some} from '@util/option';
import {FieldDescription, SelectFieldType} from '@feature/information-request/acceptance/field-select/field-description';
import {NumberUtil} from '@util/number.util';

@Component({
  selector: 'location-info-acceptance',
  templateUrl: '../info-acceptance/info-acceptance.component.html',
  styleUrls: ['../info-acceptance/info-acceptance.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LocationInfoAcceptanceComponent extends InfoAcceptanceDirective<any> implements OnInit {
  @Input() oldLocation: Location;
  @Input() newLocation: Location;

  @Output() locationChanges = new EventEmitter<Location>();

  constructor(fb: UntypedFormBuilder) {
    super(fb);
  }

  ngOnInit(): void {
    this.oldValues = this.toFieldValues(this.oldLocation);
    this.oldDisplayValues = this.toDisplayValues(this.oldValues);

    this.newValues = this.toFieldValues(this.newLocation);
    this.newDisplayValues = this.toDisplayValues(this.newValues);
    const hasArea = NumberUtil.isDefined(this.newLocation.areaOverride);
    this.fieldDescriptions = this.createDescriptions(hasArea);

    super.ngOnInit();
  }

  protected resultChanges(result: FieldValues): void {
    const location = ObjectUtil.clone(this.oldLocation);
    location.geometry = result.geometry;
    location.startTime = result.startTime;
    location.endTime = result.endTime;
    location.postalAddress = new PostalAddress(result.streetAddress, result.postalCode, result.postalOffice);

    // Only set area override when selected area differs from existing locations effective area (calculated / manually set)
    // to prevent setting area override when existing calculated area was selected
    if (location.effectiveArea !== result.area) {
      location.areaOverride = result.area;
    }
    this.locationChanges.emit(location);
  }

  private toFieldValues(location: Location): FieldValues {
    if (location) {
      return {
        id: location.id,
        geometry: location.geometry,
        startTime: location.startTime,
        endTime: location.endTime,
        streetAddress: Some(location.postalAddress).map(pa => pa.streetAddress).orElse(undefined),
        postalCode: Some(location.postalAddress).map(pa => pa.postalCode).orElse(undefined),
        postalOffice: Some(location.postalAddress).map(pa => pa.city).orElse(undefined),
        area: location.effectiveArea
      };
    } else {
      return {};
    }
  }

  private toDisplayValues(fieldValues: FieldValues): FieldValues {
    return {
      ...fieldValues,
      area: Some(fieldValues.area).map(area => Math.ceil(area)).orElse(undefined)
    };
  }


  private createDescriptions(hasArea: boolean): FieldDescription[] {
    const commonFields = [
      new FieldDescription('geometry', findTranslation('location.geometry'), SelectFieldType.GEOMETRY),
      new FieldDescription('startTime', findTranslation('location.startTime')),
      new FieldDescription('endTime', findTranslation('location.endTime')),
      new FieldDescription('streetAddress', findTranslation('postalAddress.streetAddress')),
      new FieldDescription('postalCode', findTranslation('postalAddress.postalCode')),
      new FieldDescription('postalOffice', findTranslation('postalAddress.postalOffice'))
    ];

    const areaFields = hasArea
      ? [new FieldDescription('area', findTranslation('location.area'))]
      : [];

    return [...commonFields, ...areaFields];
  }
}
