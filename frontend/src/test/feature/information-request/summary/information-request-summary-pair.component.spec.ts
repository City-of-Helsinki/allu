import {Component, DebugElement, Input} from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {MatDialogModule} from '@angular/material/dialog';
import {By} from '@angular/platform-browser';
import {InformationRequestSummary} from '@model/information-request/information-request-summary';
import {InformationRequestSummaryPairComponent} from '@feature/information-request/summary/information-request-summary-pair.component';
import {InformationRequestStatus} from '@model/information-request/information-request-status';

@Component({
  selector: 'test-host',
  template: `<information-request-summary-pair [summary]="summary"></information-request-summary-pair>`
})
class MockHostComponent {
  summary: InformationRequestSummary;
}

@Component({
  selector: 'information-request-summary',
  template: '',
})
class InformationRequestSummaryMockComponent {
  @Input() summary: InformationRequestSummary;
}

@Component({
  selector: 'information-request-response-summary',
  template: '',
})
class InformationRequestResponseSummaryMockComponent {
  @Input() summary: InformationRequestSummary;
}


describe('FieldSelectComponent', () => {
  let fixture: ComponentFixture<MockHostComponent>;
  let testHost: MockHostComponent;
  let de: DebugElement;
  let summaryPairComponenent: InformationRequestSummaryPairComponent;

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
        InformationRequestSummaryPairComponent,
        InformationRequestSummaryMockComponent,
        InformationRequestResponseSummaryMockComponent
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MockHostComponent);
    testHost = fixture.componentInstance;
    de = fixture.debugElement;
    fixture.detectChanges();
    summaryPairComponenent = de.query(By.directive(InformationRequestSummaryPairComponent)).componentInstance;
  });

  it('loads component', () => {
    const hostElement = de.nativeElement;
    expect(hostElement).toBeDefined();
  });

  it('should show request and response when available', () => {
    const summary = new InformationRequestSummary();
    summary.informationRequestId = 1;
    summary.status = InformationRequestStatus.CLOSED;
    testHost.summary = summary;
    fixture.detectChanges();

    const request: DebugElement = de.query(By.css('information-request-summary'));
    const response: DebugElement = de.query(By.css('information-request-response-summary'));
    expect(request).toBeDefined();
    expect(response).toBeDefined();
  });

  it('should show only request when no response available', () => {
    const summary = new InformationRequestSummary();
    summary.informationRequestId = 1;
    summary.status = InformationRequestStatus.OPEN;
    testHost.summary = summary;
    fixture.detectChanges();

    const request: DebugElement = de.query(By.css('information-request-summary'));
    const response: DebugElement = de.query(By.css('information-request-response-summary'));
    expect(request).toBeDefined();
    expect(response).toBeNull();
  });
});
