import {Component, Input} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {EnumUtil} from '../../../../../util/enum.util';
import {ChargeBasisUnit} from '../../../../../model/application/invoice/charge-basis-unit';

@Component({
  selector: 'charge-basis-fee',
  templateUrl: './charge-basis-fee.component.html',
  styleUrls: []
})
export class ChargeBasisFeeComponent {

  @Input() form: FormGroup;
  unitTypes = EnumUtil.enumValues(ChargeBasisUnit);
}
