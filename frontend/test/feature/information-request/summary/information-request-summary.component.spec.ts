import {Component, DebugElement, NO_ERRORS_SCHEMA} from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {MatDialogModule} from '@angular/material/dialog';
import {By} from '@angular/platform-browser';
import {InformationRequestSummary} from '@model/information-request/information-request-summary';
import {InformationRequestSummaryComponent} from '@feature/information-request/summary/information-request-summary.component';
import {InformationRequestFieldsComponent} from '@feature/information-request/request/display/information-request-fields.component';
import {Store, StoreModule} from '@ngrx/store';
import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {getButtonWithText} from '@test/selector-helpers';
import {findTranslation} from '@util/translations';
import {RouterTestingModule} from '@angular/router/testing';
import {Router} from '@angular/router';
import {Location} from '@angular/common';
import {MockRoutedComponent} from '@test/mocks';
import * as fromRoot from '@feature/allu/reducers';
import {CancelRequest} from '@feature/information-request/actions/information-request-actions';

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
  let router: Router;
  let location: Location;
  let store: Store<fromRoot.State>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        ReactiveFormsModule,
        MatDialogModule,
        AlluCommonModule,
        StoreModule.forRoot({}),
        RouterTestingModule.withRoutes([
          { path: 'information_request', component: MockRoutedComponent }
        ])
      ],
      declarations: [
        MockHostComponent,
        MockRoutedComponent,
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
    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    store = TestBed.inject(Store);
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

  it('should show action buttons for active request', () => {
    const summary = new InformationRequestSummary();
    summary.status = InformationRequestStatus.OPEN;
    testHost.summary = summary;
    fixture.detectChanges();
    expect(getButtonWithText(de, findTranslation('application.button.cancelInformationRequest').toUpperCase())).toBeDefined();
    expect(getButtonWithText(de, findTranslation('common.button.show').toUpperCase())).toBeDefined();
  });

  it('should hide action buttons for request with response', () => {
    const summary = new InformationRequestSummary();
    summary.status = InformationRequestStatus.RESPONSE_RECEIVED;
    testHost.summary = summary;
    fixture.detectChanges();
    expect(getButtonWithText(de, findTranslation('application.button.cancelInformationRequest').toUpperCase())).toBeFalsy();
    expect(getButtonWithText(de, findTranslation('common.button.show').toUpperCase())).toBeFalsy();
  });

  it('should hide action buttons for closed request', () => {
    const summary = new InformationRequestSummary();
    summary.status = InformationRequestStatus.CLOSED;
    testHost.summary = summary;
    fixture.detectChanges();
    expect(getButtonWithText(de, findTranslation('application.button.cancelInformationRequest').toUpperCase())).toBeFalsy();
    expect(getButtonWithText(de, findTranslation('common.button.show').toUpperCase())).toBeFalsy();
  });

  it('should navigate when show is clicked', fakeAsync(() => {
    const summary = new InformationRequestSummary();
    summary.status = InformationRequestStatus.OPEN;
    testHost.summary = summary;
    fixture.detectChanges();
    const button = getButtonWithText(de, findTranslation('common.button.show').toUpperCase());
    button.click();
    tick();
    expect(location.path()).toBe('/information_request');
  }));

  it('should dispatch cancel request when cancel is clicked', fakeAsync(() => {
    const summary = new InformationRequestSummary();
    summary.informationRequestId = 123;
    summary.status = InformationRequestStatus.OPEN;
    testHost.summary = summary;
    fixture.detectChanges();
    spyOn(store, 'dispatch');

    const button = getButtonWithText(de, findTranslation('application.button.cancelInformationRequest').toUpperCase());
    button.click();
    tick();
    expect(store.dispatch).toHaveBeenCalledWith(new CancelRequest(summary.informationRequestId));
  }));
});
