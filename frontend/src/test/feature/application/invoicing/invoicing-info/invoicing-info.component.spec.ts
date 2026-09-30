import {DebugElement, NO_ERRORS_SCHEMA} from '@angular/core';
import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {FormsModule, ReactiveFormsModule, UntypedFormBuilder} from '@angular/forms';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {MatFormFieldModule} from '@angular/material/form-field';
import {Store, StoreModule} from '@ngrx/store';
import {combineReducers} from '@ngrx/store';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {InvoicingInfoComponent} from '@feature/application/invoicing/invoicing-info/invoicing-info.component';
import {InvoicingInfoForm} from '@feature/application/invoicing/invoicing-info/invoicing-info.form';
import {ApplicationStore} from '@service/application/application-store';
import {MatDialog} from '@angular/material/dialog';
import {NotificationService} from '@feature/notification/notification.service';
import {ApplicationStoreMock, MatDialogMock, NotificationServiceMock} from '../../../../mocks';
import * as fromApplication from '@feature/application/reducers';
import * as fromInvoicing from '@feature/application/invoicing/reducers';
import {LoadSuccess} from '@feature/application/actions/application-actions';
import {Application} from '@model/application/application';
import {ApplicationType} from '@model/application/type/application-type';
import {ApplicationStatus} from '@model/application/application-status';
import {ExcavationAnnouncement} from '@model/application/excavation-announcement/excavation-announcement';
import {AreaRental} from '@model/application/area-rental/area-rental';
import {BehaviorSubject, Observable, of} from 'rxjs';

class InvoicingApplicationStoreMock extends ApplicationStoreMock {
  deposit = new BehaviorSubject(undefined);

  loadDeposit(): Observable<unknown> {
    return of({});
  }

  saveDeposit(_deposit: unknown): Observable<unknown> {
    return of({});
  }
}

describe('InvoicingInfoComponent', () => {
  let comp: InvoicingInfoComponent;
  let fixture: ComponentFixture<InvoicingInfoComponent>;
  let de: DebugElement;
  let store: Store<fromApplication.State>;

  const createComponent = (appType: ApplicationType, extension?: object): void => {
    fixture = TestBed.createComponent(InvoicingInfoComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
    store = TestBed.inject(Store);

    const app = new Application(1);
    app.type = appType;
    app.status = ApplicationStatus.HANDLING;
    app.extension = extension;
    store.dispatch(new LoadSuccess(app));

    comp.form = InvoicingInfoForm.initialForm(new UntypedFormBuilder());
    comp.ngOnInit();
    fixture.detectChanges();
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        MatFormFieldModule,
        FormsModule,
        ReactiveFormsModule,
        NoopAnimationsModule,
        StoreModule.forRoot({
          invoicing: combineReducers(fromInvoicing.reducers),
          application: combineReducers(fromApplication.reducers)
        })
      ],
      declarations: [
        InvoicingInfoComponent
      ],
      providers: [
        {provide: ApplicationStore, useClass: InvoicingApplicationStoreMock},
        {provide: MatDialog, useClass: MatDialogMock},
        {provide: NotificationService, useClass: NotificationServiceMock}
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  }));

  it('shows the no area usage fee checkbox for excavation announcements', () => {
    createComponent(ApplicationType.EXCAVATION_ANNOUNCEMENT, new ExcavationAnnouncement());

    expect(comp.showNoAreaUsageFee).toBe(true);
    expect(de.query(By.css('[formControlName="noAreaUsageFee"]'))).toBeTruthy();
  });

  it('hides the no area usage fee checkbox for other application types', () => {
    createComponent(ApplicationType.AREA_RENTAL, new AreaRental());

    expect(comp.showNoAreaUsageFee).toBe(false);
    expect(de.query(By.css('[formControlName="noAreaUsageFee"]'))).toBeNull();
  });

  it('shows the reason field when the no area usage fee checkbox is checked', () => {
    createComponent(ApplicationType.EXCAVATION_ANNOUNCEMENT, new ExcavationAnnouncement());

    expect(de.query(By.css('[formControlName="noAreaUsageFeeReason"]'))).toBeNull();

    comp.form.get('noAreaUsageFee').setValue(true);
    fixture.detectChanges();

    expect(de.query(By.css('[formControlName="noAreaUsageFeeReason"]'))).toBeTruthy();
  });

  it('makes the reason mandatory when the no area usage fee checkbox is checked', () => {
    createComponent(ApplicationType.EXCAVATION_ANNOUNCEMENT, new ExcavationAnnouncement());
    const reason = comp.form.get('noAreaUsageFeeReason');

    expect(reason.hasError('required')).toBe(false);

    comp.form.get('noAreaUsageFee').setValue(true);
    fixture.detectChanges();
    expect(reason.hasError('required')).toBe(true);

    reason.setValue('Kaupungin kaavahankkeen toteuttaminen');
    fixture.detectChanges();
    expect(reason.hasError('required')).toBe(false);
  });

  it('does not require the reason when the checkbox is unchecked', () => {
    createComponent(ApplicationType.EXCAVATION_ANNOUNCEMENT, new ExcavationAnnouncement());
    const reason = comp.form.get('noAreaUsageFeeReason');

    comp.form.get('noAreaUsageFee').setValue(true);
    fixture.detectChanges();
    expect(reason.hasError('required')).toBe(true);

    comp.form.get('noAreaUsageFee').setValue(false);
    fixture.detectChanges();
    expect(reason.hasError('required')).toBe(false);
  });
});
