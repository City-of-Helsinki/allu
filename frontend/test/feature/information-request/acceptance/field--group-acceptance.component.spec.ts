import {Component, DebugElement, forwardRef, Input} from '@angular/core';
import {Selected} from '../../../../src/app/feature/information-request/acceptance/field-acceptance.component';
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {ControlValueAccessor, FormBuilder, FormsModule, NG_VALUE_ACCESSOR, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '../../../../src/app/feature/common/allu-common.module';
import {MatDialogModule} from '@angular/material';
import {FieldGroupAcceptanceComponent} from '../../../../src/app/feature/information-request/acceptance/field-group-acceptance.component';
import {By} from '@angular/platform-browser';

const FIELD_ACCEPTANCE_VALUE_ACCESSOR = {
  provide: NG_VALUE_ACCESSOR,
  useExisting: forwardRef(() => MockFieldAcceptanceComponent),
  multi: true
};

@Component({
  selector: 'field-acceptance',
  template: '',
  providers: [FIELD_ACCEPTANCE_VALUE_ACCESSOR]
})
class MockFieldAcceptanceComponent implements ControlValueAccessor {
  @Input() label: string;
  @Input() oldValue: any;
  @Input() newValue: any;

  select(selected: Selected) {
    this._onChange(selected);
  }

  registerOnChange(fn: any): void {
    this._onChange = fn;
  }

  registerOnTouched(fn: any): void {}

  writeValue(selected: Selected): void {}

  private _onChange = (_: any) => {};
}

describe('FieldGroupAcceptanceComponent', () => {
  let fixture: ComponentFixture<FieldGroupAcceptanceComponent>;
  let testComp: FieldGroupAcceptanceComponent;
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
        FieldGroupAcceptanceComponent,
        MockFieldAcceptanceComponent
      ],
      providers: [
        FormBuilder
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FieldGroupAcceptanceComponent);
    testComp = fixture.componentInstance;
    de = fixture.debugElement;

    testComp.oldValues = {
      value1: 'oldValue1',
      value2: 1
    };

    testComp.newValues = {
      value1: 'newValue1',
      value2: 2
    };

    testComp.fieldLabels = {
      value1: 'value1Label',
      value2: 'value2Label'
    };

    testComp.form = new FormBuilder().group({});

    fixture.detectChanges();
  });

  it('loads component', () => {
    const hostElement = de.nativeElement;
    expect(hostElement).toBeDefined();
  });

  it('should create child component for each value', () => {
    const children: DebugElement[] = de.queryAll(By.directive(MockFieldAcceptanceComponent));
    expect(children.length).toEqual(Object.keys(testComp.fieldLabels).length);
  });

  it('should pass labels and values to child components', () => {
    const children: DebugElement[] = de.queryAll(By.directive(MockFieldAcceptanceComponent));

    const firstChild: MockFieldAcceptanceComponent = children[0].componentInstance;
    expect(firstChild.label).toEqual(testComp.fieldLabels.value1);
    expect(firstChild.oldValue).toEqual(testComp.oldValues.value1);
    expect(firstChild.newValue).toEqual(testComp.newValues.value1);

    const secondChild: MockFieldAcceptanceComponent = children[1].componentInstance;
    expect(secondChild.label).toEqual(testComp.fieldLabels.value2);
    expect(secondChild.oldValue).toEqual(testComp.oldValues.value2);
    expect(secondChild.newValue).toEqual(testComp.newValues.value2);
  });

  it('should have valid form when all children have selected value', () => {
    expect(testComp.form.valid).toEqual(false);
    const children: DebugElement[] = de.queryAll(By.directive(MockFieldAcceptanceComponent));
    const firstChild: MockFieldAcceptanceComponent = children[0].componentInstance;
    const secondChild: MockFieldAcceptanceComponent = children[1].componentInstance;
    firstChild.select('old');
    secondChild.select('new');
    expect(testComp.form.valid).toEqual(true);
  });
});
