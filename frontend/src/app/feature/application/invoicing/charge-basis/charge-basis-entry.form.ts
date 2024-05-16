import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {ChargeBasisUnit} from '@model/application/invoice/charge-basis-unit';
import {ChargeBasisEntry} from '@model/application/invoice/charge-basis-entry';
import {ChargeBasisType, manualChargeBasisTypes} from '@model/application/invoice/charge-basis-type';
import {StringUtil} from '@util/string.util';
import {ComplexValidator} from '@util/complex-validator';
import {NumberUtil} from '@util/number.util';

const EMPTY = '';

export class ChargeBasisEntryForm {
  constructor(
    public id?: number,
    public type?: ChargeBasisType,
    public unit?: ChargeBasisUnit,
    public quantity?: number,
    public text?: string,
    public unitPrice?: number,
    public netPrice?: number,
    public manuallySet?: boolean,
    public tag?: string,
    public referredTag: string = EMPTY,
    public explanation: string[] = [],
    public manualExplanation: string = EMPTY,
    public locked?: boolean,
    public invoicable: boolean = true,
    public invoicingPeriodId?: number
  ) {
    this.referredTag = referredTag || EMPTY;
  }

  public static formGroup(fb: UntypedFormBuilder, entry: ChargeBasisEntry = new ChargeBasisEntry()): UntypedFormGroup {
    const formValue = ChargeBasisEntryForm.toFormValue(entry);
    return fb.group({
      id: [formValue.id],
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
      manualExplanation: [formValue.manualExplanation, [ComplexValidator.maxRows(5), ComplexValidator.maxRowLength(70)]],
      locked: [formValue.locked],
      invoicable: [formValue.invoicable],
      invoicingPeriodId: [formValue.invoicingPeriodId]
    });
  }

  public static toChargeBasisEntry(form: ChargeBasisEntryForm): ChargeBasisEntry {
    const entry = new ChargeBasisEntry(form.id, form.type, form.unit);
    entry.quantity = this.quantity(form.quantity, form.type, form.unit);
    entry.text = form.text;
    entry.unitPrice = this.unitPriceToCents(form.unitPrice, form.type, form.unit);
    entry.netPrice = entry.quantity * entry.unitPrice;
    entry.manuallySet = form.manuallySet;
    entry.explanation = this.explanationFromForm(form);
    entry.tag = form.tag;
    entry.referredTag = StringUtil.isEmpty(form.referredTag) ? undefined : form.referredTag;
    entry.invoicable = form.invoicable;
    entry.invoicingPeriodId = form.invoicingPeriodId;
    return entry;
  }

  public static toFormValue(entry: ChargeBasisEntry): ChargeBasisEntryForm {
    return new ChargeBasisEntryForm(
      entry.id,
      entry.type,
      entry.unit,
      this.quantity(entry.quantity, entry.type, entry.unit),
      entry.text,
      this.unitPriceToEuros(entry.unitPrice, entry.type, entry.unit),
      NumberUtil.toEuros(entry.netPrice),
      entry.manuallySet,
      entry.tag,
      entry.referredTag,
      entry.explanation,
      entry.explanation ? entry.explanation.join('\n') : undefined,
      entry.locked,
      entry.invoicable,
      entry.invoicingPeriodId
    );
  }

  private static explanationFromForm(form: ChargeBasisEntryForm): string[] {
    if (manualChargeBasisTypes.includes(form.type)) {
      return this.splitExplanation(form.manualExplanation);
    } else {
      return form.explanation;
    }
  }

  private static splitExplanation(explanation: string): string[] {
    if (!explanation) {
      return undefined;
    }
    return explanation.split('\n').filter(line => line.trim().length > 0);
  }

  private static quantity(quantity: number, type: ChargeBasisType, unit: ChargeBasisUnit): number {
    return this.negateQuantity(type, unit) ? -quantity : quantity;
  }

  private static negateQuantity(type: ChargeBasisType, unit: ChargeBasisUnit): boolean {
    return type === ChargeBasisType.DISCOUNT && unit === ChargeBasisUnit.PERCENT;
  }

  private static unitPriceToEuros(priceInCents: number, type: ChargeBasisType, unit: ChargeBasisUnit) {
    const unitPrice = NumberUtil.toEuros(priceInCents);
    return this.negatePrice(type, unit) ? -unitPrice : unitPrice;
  }

  private static unitPriceToCents(priceInEuros: number, type: ChargeBasisType, unit: ChargeBasisUnit) {
    const unitPrice = NumberUtil.toCents(priceInEuros);
    return this.negatePrice(type, unit) ? -unitPrice : unitPrice;
  }

  private static negatePrice(type: ChargeBasisType, unit: ChargeBasisUnit) {
    return type === ChargeBasisType.DISCOUNT && unit === ChargeBasisUnit.PIECE;
  }
}
