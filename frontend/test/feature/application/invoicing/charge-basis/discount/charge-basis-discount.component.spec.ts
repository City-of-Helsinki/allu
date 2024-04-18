import {DebugElement} from '@angular/core';
import {UntypedFormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {ChargeBasisDiscountComponent} from '@feature/application/invoicing/charge-basis/discount/charge-basis-discount.component';
import {ChargeBasisEntryForm} from '@feature/application/invoicing/charge-basis/charge-basis-entry.form';
import {ChargeBasisEntry} from '@model/application/invoice/charge-basis-entry';
import {ChargeBasisType} from '@model/application/invoice/charge-basis-type';
import {ChargeBasisUnit} from '@model/application/invoice/charge-basis-unit';
import {findTranslation} from '@util/translations';
import * as fromApplication from '@feature/application/reducers';
import * as fromInvoicing from '@feature/application/invoicing/reducers';
import {combineReducers, Store, StoreModule} from '@ngrx/store';
import {LoadSuccess} from '@feature/application/invoicing/actions/charge-basis-actions';

describe('ChargeBasisDiscountComponent', () => {
  let comp: ChargeBasisDiscountComponent;
  let fixture: ComponentFixture<ChargeBasisDiscountComponent>;
  let de: DebugElement;
  let store: Store<fromInvoicing.State>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        FormsModule,
        ReactiveFormsModule,
        StoreModule.forRoot({
          'invoicing': combineReducers(fromInvoicing.reducers),
          'application': combineReducers(fromApplication.reducers)
        })
      ],
      declarations: [
        ChargeBasisDiscountComponent
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChargeBasisDiscountComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
    store = TestBed.inject(Store);

    comp.form = ChargeBasisEntryForm.formGroup(new UntypedFormBuilder());
    comp.ngOnInit();
    fixture.detectChanges();
  });

  it('should initialize', () => {
    expect(de.query(By.css('form'))).toBeDefined();
  });

  it('shows common fields', fakeAsync(() => {
    expect(de.query(By.css('[formControlName="text"]'))).toBeTruthy();
    expect(de.query(By.css('[formControlName="referredTag"]'))).toBeTruthy();
  }));

  it('allows referring only to non manual entries', fakeAsync(() => {
    const manual = manualEntry(1);
    const calculated = calculatedEntry(2);
    store.dispatch(new LoadSuccess([manual, calculated]));
    comp.ngOnInit();
    detectAndTick();

    comp.referableEntries.subscribe(referable => {
      expect(referable.length).toEqual(1);
      expect(referable[0]).toEqual(calculated);
    });
  }));

  it('should allow selecting referred entry by tag', fakeAsync(() => {
    const existingEntries = [calculatedEntry(1, 'entry1', 'tag1'), calculatedEntry(2, 'entry2', 'tag2')];
    store.dispatch(new LoadSuccess(existingEntries));
    comp.ngOnInit();
    detectAndTick();

    const select = de.query(By.css('[formControlName="referredTag"]'));
    select.nativeElement.click();
    detectAndTick();

    const options = select.queryAll(By.css('mat-option .mat-option-text'));
    expect(options.length).toEqual(existingEntries.length + 1); // entries + default option
    expect(options[0].nativeElement.textContent.trim()).toEqual(findTranslation('chargeBasis.discountFor.wholeInvoice'));
    expect(options[1].nativeElement.textContent.trim()).toEqual(existingEntries[0].text);
    expect(options[2].nativeElement.textContent.trim()).toEqual(existingEntries[1].text);
  }));

  it('shows correct fields for percentage discount', fakeAsync(() => {
    comp.form.patchValue({unit: ChargeBasisUnit.PERCENT});
    detectAndTick();
    expect(de.query(By.css('[formControlName="quantity"]'))).toBeTruthy();
    expect(de.query(By.css('[formControlName="unitPrice"]'))).toBeNull();
  }));

  it('sets default values when discount changed to percent', fakeAsync(() => {
    comp.form.patchValue({unit: ChargeBasisUnit.PERCENT});
    detectAndTick();
    const formValue = comp.form.getRawValue();
    expect(formValue.quantity).toBeUndefined();
    expect(formValue.unitPrice).toBeUndefined();
    expect(formValue.netPrice).toBeUndefined();
  }));

  it('shows correct fields for sum discount', fakeAsync(() => {
    comp.form.patchValue({unit: ChargeBasisUnit.PIECE});
    detectAndTick();
    expect(de.query(By.css('[formControlName="unitPrice"]'))).toBeTruthy();
    expect(de.query(By.css('[formControlName="quantity"]'))).toBeNull();
  }));

  it('sets default values when discount changed to sum', fakeAsync(() => {
    comp.form.patchValue({unit: ChargeBasisUnit.PIECE});
    detectAndTick();
    const formValue = comp.form.getRawValue();
    expect(formValue.quantity).toEqual(1);
    expect(formValue.unitPrice).toBeUndefined();
    expect(formValue.netPrice).toBeUndefined();
  }));

  function detectAndTick(): void {
    fixture.detectChanges();
    tick();
  }

  function calculatedEntry(id: number, text = 'calculated', tag = 'tag'): ChargeBasisEntry {
    return new ChargeBasisEntry(id, ChargeBasisType.CALCULATED, ChargeBasisUnit.PIECE, 10,
      text, 100, 1000, false, tag, null, null, false, true);
  }

  function manualEntry(id: number): ChargeBasisEntry {
    return new ChargeBasisEntry(id, ChargeBasisType.ADDITIONAL_FEE, ChargeBasisUnit.PIECE, 5, 'Manual', 100, 500, true);
  }
});
