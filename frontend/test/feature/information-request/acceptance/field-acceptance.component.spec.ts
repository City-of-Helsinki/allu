import {Component, DebugElement, Input, ViewChild} from '@angular/core';
import {FieldAcceptanceComponent} from '../../../../src/app/feature/information-request/acceptance/field-acceptance.component';
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';
import {AlluCommonModule} from '../../../../src/app/feature/common/allu-common.module';
import {MatDialogModule} from '@angular/material';
import {getButtonWithMatIcon} from '../../../selector-helpers';

@Component({
  selector: 'parent',
  template: `
    <form [formGroup]="form">
      <field-acceptance
        [formControlName]="fieldName"
        [label]="label"
        [oldValue]="oldValue"
        [newValue]="newValue"></field-acceptance>
    </form>`
})
class MockParentComponent {
  form: FormGroup;
  fieldName =  'testField';
  label = 'testLabel';
  oldValue = 'oldValue';
  newValue = 'newValue';

  @ViewChild(FieldAcceptanceComponent) fieldAcceptanceComponent: FieldAcceptanceComponent;

  constructor(fb: FormBuilder) {
    this.form = fb.group({});
    this.form.addControl(this.fieldName, fb.control(undefined));
  }
}

@Component({
  selector: 'field-value',
  template: ''
})
class MockFieldValueComponent {
  @Input() value: any;
}

describe('FieldAcceptanceComponent', () => {
  let testHost: MockParentComponent;
  let fixture: ComponentFixture<MockParentComponent>;
  let de: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        ReactiveFormsModule,
        MatDialogModule,
        AlluCommonModule
      ],
      declarations: [
        MockParentComponent,
        FieldAcceptanceComponent,
        MockFieldValueComponent
      ],
      providers: [
        FormBuilder
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MockParentComponent);
    testHost = fixture.componentInstance;
    de = fixture.debugElement;
    fixture.detectChanges();
  });

  it('loads component', () => {
    const hostElement = de.nativeElement;
    expect(hostElement).toBeDefined();
  });

  it('shows given label', () => {
    const labelDe: DebugElement = de.query(By.css('.text-primary'));
    expect(labelDe).toBeDefined();
    expect(labelDe.nativeElement.textContent.trim()).toEqual('testLabel');
  });

  it('selects new or old by user action', () => {
    const oldButton: HTMLButtonElement = getButtonWithMatIcon(de, 'clear');
    const newButton: HTMLButtonElement = getButtonWithMatIcon(de, 'check');
    expect(testHost.form.get(testHost.fieldName).value).toBeNull();

    oldButton.click();
    expect(testHost.form.get(testHost.fieldName).value).toEqual('old');

    newButton.click();
    expect(testHost.form.get(testHost.fieldName).value).toEqual('new');
  });
});
