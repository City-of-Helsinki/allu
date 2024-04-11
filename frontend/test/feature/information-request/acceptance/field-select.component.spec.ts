import {Component, DebugElement, Input} from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {MatDialogModule} from '@angular/material/dialog';
import {MatListOption} from '@angular/material/list';
import {By} from '@angular/platform-browser';
import {FieldSelectComponent, FieldValues} from '@feature/information-request/acceptance/field-select/field-select.component';
import {FieldValueComponent} from '@feature/information-request/acceptance/field-select/field-value.component';
import {getButtonWithText, getMatIcon} from '../../../selector-helpers';
import {findTranslation} from '@util/translations';
import {FieldDescription} from '@feature/information-request/acceptance/field-select/field-description';
import {MapFeature} from '@feature/map/map-feature';

@Component({
  selector: 'test-host',
  template: `
    <form [formGroup]="form">
      <field-select
        [descriptions]="fieldDescriptions"
        [fieldValues]="values"
        [comparedValues]="comparedValues"
        formControlName="selectedValues"></field-select>
    </form>
  `
})
class MockHostComponent {
  fieldDescriptions: FieldDescription[] = [
    new FieldDescription('value1', 'value1Label'),
    new FieldDescription('value2', 'value2Label')
  ];

  values: FieldValues = {
    value1: 'new value here',
    value2: 52
  };

  comparedValues: FieldValues = {
    value1: 'value to compare',
    value2: 11
  };

  form = new FormBuilder().group({
    selectedValues: [[]]
  });
}

@Component({
  selector: 'simple-map',
  template: '',
})
class SimpleMapMockComponent {
  @Input() mapId = 'map';
  @Input() content: MapFeature[] = [];
  @Input() selectedFeature: number;
}


describe('FieldSelectComponent', () => {
  let fixture: ComponentFixture<MockHostComponent>;
  let testHost: MockHostComponent;
  let de: DebugElement;
  let fieldSelect: FieldSelectComponent;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        ReactiveFormsModule,
        MatDialogModule,
        AlluCommonModule
      ],
      declarations: [
        MockHostComponent,
        FieldSelectComponent,
        FieldValueComponent,
        SimpleMapMockComponent
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
    fieldSelect = de.query(By.directive(FieldSelectComponent)).componentInstance;
  });

  it('loads component', () => {
    const hostElement = de.nativeElement;
    expect(hostElement).toBeDefined();
  });

  it('should create option for each value', () => {
    const options: DebugElement[] = de.queryAll(By.directive(MatListOption));
    expect(options.length).toEqual(testHost.fieldDescriptions.length);
  });


  it('should show labels and values in options', () => {
    const options: DebugElement[] = de.queryAll(By.directive(MatListOption));

    const firstOpt: DebugElement = options[0];
    expect(firstOpt.nativeElement.getAttribute('ng-reflect-value')).toEqual('value1');
    expect(firstOpt.query(By.css('.text-highlighted')).nativeElement.textContent).toEqual(testHost.fieldDescriptions[0].label);
    expect(firstOpt.query(By.directive(FieldValueComponent)).componentInstance.displayValue).toEqual(testHost.values.value1);

    const secondOpt: DebugElement = options[1];
    expect(secondOpt.nativeElement.getAttribute('ng-reflect-value')).toEqual('value2');
    expect(secondOpt.query(By.css('.text-highlighted')).nativeElement.textContent).toEqual(testHost.fieldDescriptions[1].label);
    expect(secondOpt.query(By.directive(FieldValueComponent)).componentInstance.displayValue).toEqual(testHost.values.value2);
  });

  it('should inform user when values differ', () => {
    const options: DebugElement[] = de.queryAll(By.directive(MatListOption));
    const firstOpt: DebugElement = options[0];
    expect(getMatIcon(firstOpt, 'warning')).toBeTruthy();
  });

  it('should hide warning when values are equal', () => {
    testHost.values = {
      value1: 'equalValue',
      value2: 'equalValue'
    };

    testHost.comparedValues = {
      value1: 'equalValue',
      value2: 'equalValue'
    };

    fixture.detectChanges();

    const options: DebugElement[] = de.queryAll(By.directive(MatListOption));
    expect(getMatIcon(options[0], 'warning')).toBeFalsy();
    expect(getMatIcon(options[1], 'warning')).toBeFalsy();
  });

  it('should disable selected field to prevent unselecting', () => {
    const options: DebugElement[] = de.queryAll(By.directive(MatListOption));
    const opt: DebugElement = options[0];
    const optComponent: MatListOption = opt.componentInstance;
    expect(optComponent.disabled).toEqual(false);

    opt.nativeElement.click();
    fixture.detectChanges();
    expect(optComponent.disabled).toEqual(true);
  });

  it('should show select all when more than two items', () => {
    expect(getButtonWithText(de, findTranslation('common.selectAll'))).toBeTruthy();
  });

  it('should select all when clicking select all', () => {
    const options: DebugElement[] = de.queryAll(By.directive(MatListOption));
    const optComponents: MatListOption[] = options.map(opt => opt.componentInstance);
    const selectAllBtn: HTMLButtonElement = getButtonWithText(de, findTranslation('common.selectAll'));

    expect(optComponents.every(component => component.selected === false)).toEqual(true);

    selectAllBtn.click();
    fixture.detectChanges();
    expect(optComponents.every(component => component.selected === true)).toEqual(true);
  });

  it('should deselect all when deselect all is called', () => {
    const options: DebugElement[] = de.queryAll(By.directive(MatListOption));
    const optComponents: MatListOption[] = options.map(opt => opt.componentInstance);

    fieldSelect.selectAll();
    fixture.detectChanges();
    expect(optComponents.every(component => component.selected === true)).toEqual(true);

    fieldSelect.deselectAll();
    fixture.detectChanges();
    expect(optComponents.every(component => component.selected === false)).toEqual(true);
  });

  it('should deselect given field', () => {
    const options: DebugElement[] = de.queryAll(By.directive(MatListOption));
    const optComponents: MatListOption[] = options.map(opt => opt.componentInstance);

    fieldSelect.selectAll();
    fixture.detectChanges();

    fieldSelect.deselect('value1');
    fixture.detectChanges();
    expect(optComponents[0].selected).toEqual(false);
  });
});
