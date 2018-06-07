import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ChargeBasisUnit} from '../../../../model/application/invoice/charge-basis-unit';
import {ChargeBasisEntry} from '../../../../model/application/invoice/charge-basis-entry';
import {ChargeBasisType, manualChargeBasisTypes} from '../../../../model/application/invoice/charge-basis-type';
import {StringUtil} from '../../../../util/string.util';
import {ComplexValidator} from '../../../../util/complex-validator';

const EMPTY = '';

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
    public referredTag: string = EMPTY,
    public explanation: string[] = [],
    public manualExplanation: string = EMPTY
  ) {
    this.referredTag = referredTag || EMPTY;
  }

  public static formGroup(fb: FormBuilder, entry: ChargeBasisEntry = new ChargeBasisEntry()): FormGroup {
    const formValue = ChargeBasisEntryForm.toFormValue(entry);
    return fb.group({
      type: [formValue.type, Validators.required],
      unit: [formValue.unit, Validators.required],
      quantity: [formValue.quantity, Validators.required],
      text: [formValue.text, [Validators.required, Validators.maxLength(70)]],
      unitPrice: [formValue.unitPrice, Validators.required],
      netPrice: [{value: formValue.netPrice, disabled: true}],
      manuallySet: [formValue.manuallySet],
      tag: [formValue.tag],
      referredTag: [formValue.referredTag],
      explanation: [formValue.explanation],
      manualExplanation: [formValue.manualExplanation, [ComplexValidator.maxRows(5), ComplexValidator.maxRowLength(70)]]
    });
  }

  public static toChargeBasisEntry(form: ChargeBasisEntryForm): ChargeBasisEntry {
    const entry = new ChargeBasisEntry(
      ChargeBasisType[form.type],
      ChargeBasisUnit[form.unit]
    );

    entry.uiQuantity = form.quantity;
    entry.text = form.text;
    entry.unitPriceEuro = form.unitPrice;
    entry.manuallySet = form.manuallySet;
    if (manualChargeBasisTypes.includes(ChargeBasisType[form.type])) {
      entry.explanation = this.splitExplanation(form.manualExplanation);
    } else {
      entry.explanation = form.explanation;
    }
    entry.tag = form.tag;
    entry.referredTag = StringUtil.isEmpty(form.referredTag) ? undefined : form.referredTag;
    return entry;
  }

  public static toFormValue(entry: ChargeBasisEntry): ChargeBasisEntryForm {
    return new ChargeBasisEntryForm(
      ChargeBasisType[entry.type],
      ChargeBasisUnit[entry.unit],
      entry.uiQuantity,
      entry.text,
      entry.unitPriceEuro,
      entry.netPriceEuro,
      entry.manuallySet,
      entry.tag,
      entry.referredTag,
      entry.explanation,
      entry.explanation ? entry.explanation.join('\n') : undefined
    );
  }

  private static splitExplanation(explanation: string): string[] {
    if (!explanation) {
      return undefined;
    }
    return explanation.split('\n').filter(line => line.trim().length > 0);
  }
}
