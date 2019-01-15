import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {InfoAcceptanceComponent} from '@feature/information-request/acceptance/info-acceptance/info-acceptance.component';
import {FieldValues} from '@feature/information-request/acceptance/field-select/field-select.component';
import {FormBuilder} from '@angular/forms';
import {findTranslation} from '@util/translations';
import {Location} from '@model/common/location';
import {ObjectUtil} from '@util/object.util';
import {PostalAddress} from '@model/common/postal-address';
import {Some} from '@util/option';
import {FieldDescription, SelectFieldType} from '@feature/information-request/acceptance/field-select/field-description';

@Component({
  selector: 'location-info-acceptance',
  templateUrl: '../info-acceptance/info-acceptance.component.html',
  styleUrls: ['../info-acceptance/info-acceptance.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LocationInfoAcceptanceComponent extends InfoAcceptanceComponent<any> implements OnInit {
  @Input() oldLocation: Location;
  @Input() newLocation: Location;
  @Input() readonly: boolean;

  @Output() locationChanges = new EventEmitter<Location>();

  constructor(fb: FormBuilder) {
    super(fb);
  }

  ngOnInit(): void {
    this.oldValues = this.toFieldValues(this.oldLocation);
    this.oldDisplayValues = this.toDisplayValues(this.oldValues);

    this.newValues = this.toFieldValues(this.newLocation);
    this.newDisplayValues = this.toDisplayValues(this.newValues);
    this.fieldDescriptions = this.createDescriptions();

    super.ngOnInit();
  }

  protected resultChanges(result: FieldValues): void {
    const location = ObjectUtil.clone(this.oldLocation);
    location.geometry = result.geometry;
    location.startTime = result.startTime;
    location.endTime = result.endTime;
    location.postalAddress = Some(location.postalAddress)
      .map(pa => new PostalAddress(result.streetAddress, pa.postalCode, pa.postalOffice))
      .orElseGet(() => new PostalAddress(result.streetAddress));
    this.locationChanges.emit(location);
  }

  private toFieldValues(location: Location): FieldValues {
    if (location) {
      return {
        id: location.id,
        geometry: location.geometry,
        startTime: location.startTime,
        endTime: location.endTime,
        streetAddress: location.postalAddress ? location.postalAddress.streetAddress : undefined,
        area: location.areaOverride
      };
    } else {
      return {};
    }
  }

  private toDisplayValues(fieldValues: FieldValues): FieldValues {
    return {...fieldValues};
  }


  private createDescriptions(): FieldDescription[] {
    return [
      new FieldDescription('geometry', findTranslation('location.geometry'), SelectFieldType.GEOMETRY),
      new FieldDescription('startTime', findTranslation('location.startTime')),
      new FieldDescription('endTime', findTranslation('location.endTime')),
      new FieldDescription('streetAddress', findTranslation('postalAddress.streetAddress'))
    ];
  }
}
