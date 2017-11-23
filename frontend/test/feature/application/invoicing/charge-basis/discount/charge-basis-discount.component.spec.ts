import {DebugElement} from '@angular/core';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {AlluCommonModule} from '../../../../../../src/app/feature/common/allu-common.module';
import {ChargeBasisDiscountComponent}
  from '../../../../../../src/app/feature/application/invoicing/charge-basis/discount/charge-basis-discount.component';
import {InvoiceHubMock} from '../../../../../mocks';
import {InvoiceHub} from '../../../../../../src/app/service/application/invoice/invoice-hub';
import {ChargeBasisEntryForm} from '../../../../../../src/app/feature/application/invoicing/charge-basis/charge-basis-entry.form';
import {Observable} from 'rxjs/Observable';
import {ChargeBasisEntry} from '../../../../../../src/app/model/application/invoice/charge-basis-entry';
import {ChargeBasisType} from '../../../../../../src/app/model/application/invoice/charge-basis-type';
import {ChargeBasisUnit} from '../../../../../../src/app/model/application/invoice/charge-basis-unit';
import {findTranslation} from '../../../../../../src/app/util/translations';

describe('ChargeBasisDiscountComponent', () => {
  let comp: ChargeBasisDiscountComponent;
  let fixture: ComponentFixture<ChargeBasisDiscountComponent>;
  let de: DebugElement;
  let invoiceHub: InvoiceHubMock;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        FormsModule,
        ReactiveFormsModule
      ],
      declarations: [
        ChargeBasisDiscountComponent
      ],
      providers: [
        {provide: InvoiceHub, useClass: InvoiceHubMock}
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    invoiceHub = TestBed.get(InvoiceHub) as InvoiceHubMock;
    fixture = TestBed.createComponent(ChargeBasisDiscountComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;

    comp.form = ChargeBasisEntryForm.formGroup(new FormBuilder());
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
    const manual = manualEntry();
    const calculated = calculatedEntry();
    const existingEntries = [manual, calculated];
    spyOnProperty(invoiceHub, 'chargeBasisEntries', 'get').and.returnValue(Observable.of(existingEntries));
    comp.ngOnInit();
    comp.referableEntries.subscribe(referable => {
      expect(referable.length).toEqual(1);
      expect(referable[0]).toEqual(calculated);
    });
    detectAndTick();
  }));

  it('should allow selecting referred entry by tag', fakeAsync(() => {
    const existingEntries = [calculatedEntry('entry1', 'tag1'), calculatedEntry('entry2', 'tag2')];
    spyOnProperty(invoiceHub, 'chargeBasisEntries', 'get').and.returnValue(Observable.of(existingEntries));
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
    comp.form.patchValue({unit: ChargeBasisUnit[ChargeBasisUnit.PERCENT]});
    detectAndTick();
    expect(de.query(By.css('[formControlName="quantity"]'))).toBeTruthy();
    expect(de.query(By.css('[formControlName="unitPrice"]'))).toBeNull();
  }));

  it('sets default values when discount changed to percent', fakeAsync(() => {
    comp.form.patchValue({unit: ChargeBasisUnit[ChargeBasisUnit.PERCENT]});
    detectAndTick();
    const formValue = comp.form.getRawValue();
    expect(formValue.quantity).toBeUndefined();
    expect(formValue.unitPrice).toBeUndefined();
    expect(formValue.netPrice).toBeUndefined();
  }));

  it('shows correct fields for sum discount', fakeAsync(() => {
    comp.form.patchValue({unit: ChargeBasisUnit[ChargeBasisUnit.PIECE]});
    detectAndTick();
    expect(de.query(By.css('[formControlName="unitPrice"]'))).toBeTruthy();
    expect(de.query(By.css('[formControlName="quantity"]'))).toBeNull();
  }));

  it('sets default values when discount changed to sum', fakeAsync(() => {
    comp.form.patchValue({unit: ChargeBasisUnit[ChargeBasisUnit.PIECE]});
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

  function calculatedEntry(text = 'calculated', tag = 'tag'): ChargeBasisEntry {
    return new ChargeBasisEntry(ChargeBasisType.CALCULATED, ChargeBasisUnit.PIECE, 10, text, 100, 1000, false, tag);
  }

  function manualEntry(): ChargeBasisEntry {
    return new ChargeBasisEntry(ChargeBasisType.ADDITIONAL_FEE, ChargeBasisUnit.PIECE, 5, 'Manual', 100, 500, true);
  }
});
