import {Component, DebugElement, NO_ERRORS_SCHEMA} from '@angular/core';
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {MatDialogModule} from '@angular/material/dialog';
import {By} from '@angular/platform-browser';
import {InformationRequestSummary} from '@model/information-request/information-request-summary';
import {InformationRequestSummaryComponent} from '@feature/information-request/summary/information-request-summary.component';
import {InformationRequestFieldsComponent} from '@feature/information-request/request/display/information-request-fields.component';

@Component({
  selector: 'test-host',
  template: `<information-request-summary [summary]="summary"></information-request-summary>`
})
class MockHostComponent {
  summary: InformationRequestSummary = new InformationRequestSummary();
}

describe('InformationRequestSummary', () => {
  let fixture: ComponentFixture<MockHostComponent>;
  let testHost: MockHostComponent;
  let de: DebugElement;
  let summaryComponent: InformationRequestSummaryComponent;

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
        InformationRequestSummaryComponent
      ],
      schemas: [ NO_ERRORS_SCHEMA ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MockHostComponent);
    testHost = fixture.componentInstance;
    de = fixture.debugElement;
    fixture.detectChanges();
    summaryComponent = de.query(By.directive(InformationRequestSummaryComponent)).componentInstance;
  });

  it('loads component', () => {
    const hostElement = de.nativeElement;
    expect(hostElement).toBeDefined();
  });

  it('should show request and its info', () => {
    const summary = new InformationRequestSummary();
    summary.updateWithoutRequest = false;
    testHost.summary = summary;
    fixture.detectChanges();

    const infoFields: DebugElement[] = de.queryAll(By.css('.mat-form-field'));
    expect(infoFields.length).toEqual(2);
    const requestFields: DebugElement = de.query(By.directive(InformationRequestFieldsComponent));
    expect(requestFields).toBeDefined();
  });

  it('should show only title and status when update without request', () => {
    const summary = new InformationRequestSummary();
    summary.updateWithoutRequest = true;
    testHost.summary = summary;
    fixture.detectChanges();

    const infoFields: DebugElement[] = de.queryAll(By.css('.mat-form-field'));
    expect(infoFields.length).toEqual(0);
    const requestFields: DebugElement = de.query(By.directive(InformationRequestFieldsComponent));
    expect(requestFields).toBeNull();
  });
});
