import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ChargeBasisUnit} from '../../../../model/application/invoice/charge-basis-unit';
import {ChargeBasisEntry} from '../../../../model/application/invoice/charge-basis-entry';

export class ChargeBasisEntryForm {
  constructor(
    public unit?: string,
    public quantity?: number,
    public text?: string,
    public unitPrice?: number,
    public netPrice?: number,
    public manuallySet?: boolean,
    public tag?: string,
    public referredTag?: string
  ) {}

  public static formGroup(fb: FormBuilder, entry: ChargeBasisEntry = new ChargeBasisEntry()): FormGroup {
    return fb.group({
      unit: [ChargeBasisUnit[entry.unit], Validators.required],
      quantity: [entry.quantity, Validators.required],
      text: [entry.text, Validators.required],
      unitPrice: [entry.unitPriceEuro, Validators.required],
      netPrice: [{value: entry.netPriceEuro, disabled: true}],
      manuallySet: [entry.manuallySet],
      tag: [entry.tag],
      referredTag: [entry.referredTag]
    });
  }

  public static toChargeBasisEntry(form: ChargeBasisEntryForm): ChargeBasisEntry {
    let entry = new ChargeBasisEntry(
      ChargeBasisUnit[form.unit],
      form.quantity,
      form.text
    );

    entry.unitPriceEuro = form.unitPrice;
    entry.netPriceEuro = form.netPrice;
    entry.manuallySet = form.manuallySet;
    return entry;
  }

  public static toFormValue(entry: ChargeBasisEntry): ChargeBasisEntryForm {
    return new ChargeBasisEntryForm(
      ChargeBasisUnit[entry.unit],
      entry.quantity,
      entry.text,
      entry.unitPriceEuro,
      entry.netPriceEuro,
      entry.manuallySet,
      entry.tag,
      entry.referredTag
    );
  }
}
