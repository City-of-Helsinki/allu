import {Component, DebugElement, forwardRef, Input} from '@angular/core';
import {Selected} from '../../../../src/app/feature/information-request/acceptance/field-acceptance.component';
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {ControlValueAccessor, FormBuilder, FormsModule, NG_VALUE_ACCESSOR, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {MatDialogModule} from '@angular/material';
import {FieldGroupAcceptanceComponent, FieldValues} from '@feature/information-request/acceptance/field-group-acceptance.component';
import {By} from '@angular/platform-browser';
import {findTranslation} from '@util/translations';

@Component({
  selector: 'test-host',
  template: `
    <field-group-acceptance
      [form]="form"
      [fieldLabels]="fieldLabels"
      [oldValues]="oldValues"
      [newValues]="newValues"
    ></field-group-acceptance>
  `
})
class MockHostComponent {
  fieldLabels = {
    value1: 'value1Label',
    value2: 'value2Label'
  };

  oldValues: FieldValues = {
    value1: 'oldValue1',
    value2: 1
  };

  newValues: FieldValues = {
    value1: 'newValue1',
    value2: 2
  };

  form = new FormBuilder().group({});
}

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
  @Input() readonly: boolean;

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

@Component({
  selector: 'field-display',
  template: ''
})
class MockFieldDisplayComponent {
  @Input() label: string;
  @Input() value: any;
}


describe('FieldGroupAcceptanceComponent', () => {
  let fixture: ComponentFixture<MockHostComponent>;
  let testHost: MockHostComponent;
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
        MockHostComponent,
        FieldGroupAcceptanceComponent,
        MockFieldAcceptanceComponent,
        MockFieldDisplayComponent
      ],
      providers: [
        FormBuilder
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MockHostComponent);
    testHost = fixture.componentInstance;
    de = fixture.debugElement;
    fixture.detectChanges();
  });

  it('loads component', () => {
    const hostElement = de.nativeElement;
    expect(hostElement).toBeDefined();
  });

  it('should create child component for each value', () => {
    const children: DebugElement[] = de.queryAll(By.directive(MockFieldAcceptanceComponent));
    expect(children.length).toEqual(Object.keys(testHost.fieldLabels).length);
  });

  it('should pass labels and values to child components', () => {
    const children: DebugElement[] = de.queryAll(By.directive(MockFieldAcceptanceComponent));

    const firstChild: MockFieldAcceptanceComponent = children[0].componentInstance;
    expect(firstChild.label).toEqual(testHost.fieldLabels.value1);
    expect(firstChild.oldValue).toEqual(testHost.oldValues.value1);
    expect(firstChild.newValue).toEqual(testHost.newValues.value1);

    const secondChild: MockFieldAcceptanceComponent = children[1].componentInstance;
    expect(secondChild.label).toEqual(testHost.fieldLabels.value2);
    expect(secondChild.oldValue).toEqual(testHost.oldValues.value2);
    expect(secondChild.newValue).toEqual(testHost.newValues.value2);
  });

  it('should have valid form when all children have selected value', () => {
    expect(testHost.form.valid).toEqual(false);
    const children: DebugElement[] = de.queryAll(By.directive(MockFieldAcceptanceComponent));
    const firstChild: MockFieldAcceptanceComponent = children[0].componentInstance;
    const secondChild: MockFieldAcceptanceComponent = children[1].componentInstance;
    firstChild.select('old');
    secondChild.select('new');
    expect(testHost.form.valid).toEqual(true);
  });

  it('should handle equal values automatically', () => {
    testHost.oldValues = {
      value1: 'equalValue',
      value2: 'notReallyEqualValue'
    };

    testHost.newValues = {
      value1: 'equalValue',
      value2: 'notEqualValue'
    };

    fixture.detectChanges();
    const children: DebugElement[] = de.queryAll(By.directive(MockFieldAcceptanceComponent));
    expect(children.length).toEqual(1, 'Only unequal values should show selection');
  });

  it('should inform user when all values are equal', () => {
    testHost.oldValues = {
      value1: 'equalValue',
      value2: 'equalValue'
    };

    testHost.newValues = {
      value1: 'equalValue',
      value2: 'equalValue'
    };

    fixture.detectChanges();
    const infoElemenent: HTMLElement = de.query(By.css('li.label-row')).nativeElement;
    expect(infoElemenent.textContent.trim()).toEqual(findTranslation('informationRequest.acceptance.noChanges'));
  });

  it('should show only new values when all old values are empty', () => {
    testHost.oldValues = {
      value1: undefined,
      value2: undefined
    };

    fixture.detectChanges();
    const children: DebugElement[] = de.queryAll(By.directive(MockFieldDisplayComponent));
    expect(children.length).toEqual(2, 'Should show all fields for new value');
  });
});
