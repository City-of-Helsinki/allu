import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ChargeBasisUnit} from '../../../../model/application/invoice/charge-basis-unit';
import {ChargeBasisEntry} from '../../../../model/application/invoice/charge-basis-entry';
import {ChargeBasisType} from '../../../../model/application/invoice/charge-basis-type';

export class ChargeBasisEntryForm {
  constructor(
    public type?: string,
    public unit?: string,
    public quantity?: number,
    public text?: string,
    public unitPrice?: number,
    public netPrice?: number,
    public manuallySet?: boolean,
    public tag?: string,
    public referredTag?: string,
    public explanation: string[] = []
  ) {}

  public static formGroup(fb: FormBuilder, entry: ChargeBasisEntry = new ChargeBasisEntry()): FormGroup {
    return fb.group({
      type: [ChargeBasisType[entry.type], Validators.required],
      unit: [ChargeBasisUnit[entry.unit], Validators.required],
      quantity: [entry.quantity, Validators.required],
      text: [entry.text, Validators.required],
      unitPrice: [entry.unitPriceEuro, Validators.required],
      netPrice: [{value: entry.netPriceEuro, disabled: true}],
      manuallySet: [entry.manuallySet],
      tag: [entry.tag],
      referredTag: [entry.referredTag],
      explanation: [entry.explanation]
    });
  }

  public static toChargeBasisEntry(form: ChargeBasisEntryForm): ChargeBasisEntry {
    let entry = new ChargeBasisEntry(
      ChargeBasisType[form.type],
      ChargeBasisUnit[form.unit],
      form.quantity,
      form.text
    );

    entry.unitPriceEuro = form.unitPrice;
    entry.netPriceEuro = form.netPrice;
    entry.manuallySet = form.manuallySet;
    entry.explanation = form.explanation;
    return entry;
  }

  public static toFormValue(entry: ChargeBasisEntry): ChargeBasisEntryForm {
    return new ChargeBasisEntryForm(
      ChargeBasisType[entry.type],
      ChargeBasisUnit[entry.unit],
      entry.quantity,
      entry.text,
      entry.unitPriceEuro,
      entry.netPriceEuro,
      entry.manuallySet,
      entry.tag,
      entry.referredTag,
      entry.explanation
    );
  }
}
