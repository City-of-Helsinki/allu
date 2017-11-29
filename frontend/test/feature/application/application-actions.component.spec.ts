import {DebugElement} from '@angular/core';
import {FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {AlluCommonModule} from '../../../src/app/feature/common/allu-common.module';
import {ApplicationActionsComponent} from '../../../src/app/feature/application/info/application-actions.component';
import {ApplicationStoreMock, availableToDirectiveMockMeta, CurrentUserMock, RouterMock} from '../../mocks';
import {ApplicationStore} from '../../../src/app/service/application/application-store';
import {ActivatedRoute, Router} from '@angular/router';
import {AvailableToDirective} from '../../../src/app/service/authorization/available-to.directive';
import {getButtonWithText} from '../../selector-helpers';
import {findTranslation} from '../../../src/app/util/translations';
import {RouterTestingModule} from '@angular/router/testing';
import {AttachmentInfo} from '../../../src/app/model/application/attachment/attachment-info';
import {Location} from '../../../src/app/model/common/location';
import {ApplicationType} from '../../../src/app/model/application/type/application-type';
import {Observable} from 'rxjs/Observable';
import {HttpResponse, HttpStatus} from '../../../src/app/util/http-response';
import {NotificationService} from '../../../src/app/service/notification/notification.service';
import {ApplicationStatus} from '../../../src/app/model/application/application-status';

describe('ApplicationActionsComponent', () => {
  let comp: ApplicationActionsComponent;
  let fixture: ComponentFixture<ApplicationActionsComponent>;
  let de: DebugElement;
  let router: RouterMock;
  let applicationStore: ApplicationStoreMock;
  const currentUserMock = CurrentUserMock.create(true, true);
  const applicationId = 15;
  const form = new FormGroup({});


  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([])
      ],
      declarations: [
        ApplicationActionsComponent
      ],
      providers: [
        {provide: Router, useClass: RouterMock},
        {provide: ActivatedRoute},
        {provide: ApplicationStore, useClass: ApplicationStoreMock}
      ]
    }).overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicationActionsComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
    router = TestBed.get(Router) as RouterMock;
    applicationStore = TestBed.get(ApplicationStore) as ApplicationStoreMock;

    applicationStore._application.id  = applicationId;
    comp.form = form;
    comp.ngOnInit();
    fixture.detectChanges();
  });

  it('should initialize', () => {
    expect(de.query(By.css('mat-card'))).toBeDefined();
  });

  it('should show edit button', () => {
    setAndInit(true);
    const editBtn = getButtonWithText(de, findTranslation('common.button.edit').toUpperCase());
    expect(editBtn.getAttribute('ng-reflect-router-link')).toEqual('/applications,' + String(applicationId) + ',edit');
  });

  it('should copy application as new', () => {
    setAndInit(true);
    spyOn(router, 'navigate');
    const location = new Location(12, 12, 12, new Date());

    const application = applicationStore._application;
    application.id = 1;
    application.attachmentList = [new AttachmentInfo(15, 'type', 'name'), new AttachmentInfo(10, 'type', 'name')];
    application.locations = [location];

    const copyAsNewBtn = getButtonWithText(de, findTranslation('application.button.copy').toUpperCase());
    copyAsNewBtn.click();

    expect(applicationStore._application.id).toBeUndefined();
    expect(applicationStore._application.attachmentList).toEqual([]);
    expect(applicationStore._application.locations.length).toEqual(1);
    expect(applicationStore._application.locations[0].startTime).toEqual(location.startTime);
    expect(router.navigate).toHaveBeenCalledWith(['/applications/edit']);
  });

  it('should delete NOTE', fakeAsync(() => {
    applicationStore._application.type = ApplicationType[ApplicationType.NOTE];
    setAndInit(true);
    spyOn(router, 'navigate');
    spyOn(applicationStore, 'delete').and.returnValue(Observable.of(new HttpResponse(HttpStatus.OK)));
    spyOn(NotificationService, 'translateMessage');

    const removeBtn = getButtonWithText(de, findTranslation('common.button.remove').toUpperCase());
    removeBtn.click();
    tickAndDetect();

    expect(applicationStore.delete).toHaveBeenCalledWith(applicationId);
    expect(NotificationService.translateMessage).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  }));

  it('should hide delete for other than NOTE', () => {
    applicationStore._application.type = ApplicationType[ApplicationType.EVENT];
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('common.button.remove').toUpperCase())).toBeUndefined();
  });

  it('should show / hide save button on depending on readonly', () => {
    setAndInit(false);
    expect(getButtonWithText(de, findTranslation('application.button.toSummary').toUpperCase())).toBeDefined();
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.toSummary').toUpperCase())).toBeUndefined();
  });

  it('should show cancel button when status is before decision', () => {
    applicationStore._application.status = ApplicationStatus[ApplicationStatus.DECISIONMAKING];
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('common.button.cancel').toUpperCase())).toBeDefined();

    applicationStore._application.status = ApplicationStatus[ApplicationStatus.DECISION];
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('common.button.cancel').toUpperCase())).toBeUndefined();
  });

  it('should change application as canceled', fakeAsync(() => {
    applicationStore._application.status = ApplicationStatus[ApplicationStatus.HANDLING];
    setAndInit(true);
    spyOn(router, 'navigate');
    spyOn(applicationStore, 'changeStatus').and.returnValue(Observable.of(applicationStore.application));
    spyOn(NotificationService, 'translateMessage');

    const cancelBtn = getButtonWithText(de, findTranslation('common.button.cancel').toUpperCase());
    cancelBtn.click();
    tickAndDetect();

    expect(applicationStore.changeStatus).toHaveBeenCalledWith(applicationId, ApplicationStatus.CANCELLED);
    expect(NotificationService.translateMessage).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/workqueue']);
  }));

  it('should show move to handling depending on status', () => {
    applicationStore._application.status = ApplicationStatus[ApplicationStatus.PENDING];
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.toHandling').toUpperCase())).toBeDefined();

    applicationStore._application.status = ApplicationStatus[ApplicationStatus.HANDLING];
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('common.button.toHandling').toUpperCase())).toBeUndefined();
  });

  it('should move application to handling', fakeAsync(() => {
    applicationStore._application.status = ApplicationStatus[ApplicationStatus.PENDING];
    setAndInit(true);
    spyOn(router, 'navigate');
    spyOn(applicationStore, 'changeStatus').and.returnValue(applicationStore.application);
    spyOn(NotificationService, 'translateMessage');

    const toHandlingBtn = getButtonWithText(de, findTranslation('application.button.toHandling').toUpperCase());
    toHandlingBtn.click();
    tickAndDetect();

    expect(applicationStore.changeStatus).toHaveBeenCalledWith(applicationId, ApplicationStatus.HANDLING);
    expect(NotificationService.translateMessage).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/applications', applicationId, 'edit']);
  }));

  it('should show move to decision depending on status and type', () => {
    // Don't show for status before HANDLING
    applicationStore._application.status = ApplicationStatus[ApplicationStatus.PENDING];
    applicationStore._application.type = ApplicationType[ApplicationType.EVENT];
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase())).toBeUndefined();

    // Don't show for states HANDLING and after
    applicationStore._application.status = ApplicationStatus[ApplicationStatus.HANDLING];
    applicationStore._application.type = ApplicationType[ApplicationType.NOTE];
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase())).toBeUndefined();

    // Should show
    applicationStore._application.status = ApplicationStatus[ApplicationStatus.HANDLING];
    applicationStore._application.type = ApplicationType[ApplicationType.EVENT];
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase())).toBeDefined();
  });

  it('should disable to decision making button when no invoice recipient is set', () => {
    applicationStore._application.status = ApplicationStatus[ApplicationStatus.HANDLING];
    applicationStore._application.type = ApplicationType[ApplicationType.EVENT];
    setAndInit(true);
    const decisionBtn = getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase());
    expect(decisionBtn.getAttribute('ng-reflect-disabled')).toEqual('true');
  });

  it('should enable to decision making button when invoice recipient is set', () => {
    applicationStore._application.status = ApplicationStatus[ApplicationStatus.HANDLING];
    applicationStore._application.type = ApplicationType[ApplicationType.EVENT];
    applicationStore._application.invoiceRecipientId = 1;
    setAndInit(true);
    const decisionBtn = getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase());
    expect(decisionBtn.getAttribute('ng-reflect-disabled')).toEqual('false');
  });

  it('should enable to decision making button when application is set to not billable', () => {
    applicationStore._application.status = ApplicationStatus[ApplicationStatus.HANDLING];
    applicationStore._application.type = ApplicationType[ApplicationType.EVENT];
    applicationStore._application.notBillable = true;
    setAndInit(true);
    const decisionBtn = getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase());
    expect(decisionBtn.getAttribute('ng-reflect-disabled')).toEqual('false');
  });

  it('should change status of Cable report to decision making', fakeAsync(() => {
    applicationStore._application.status = ApplicationStatus[ApplicationStatus.HANDLING];
    applicationStore._application.type = ApplicationType[ApplicationType.CABLE_REPORT];
    applicationStore._application.invoiceRecipientId = 1;
    setAndInit(true);
    spyOn(router, 'navigate');
    spyOn(applicationStore, 'changeStatus').and.returnValue(Observable.of(applicationStore._application));
    spyOn(NotificationService, 'translateMessage');

    const decisionBtn = getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase());
    decisionBtn.click();
    tickAndDetect();

    expect(applicationStore.changeStatus).toHaveBeenCalledWith(applicationId, ApplicationStatus.DECISIONMAKING);
    expect(NotificationService.translateMessage).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/applications', applicationStore._application.id, 'decision']);
  }));

  it('should navigate to decision making for other application types', fakeAsync(() => {
    applicationStore._application.status = ApplicationStatus[ApplicationStatus.HANDLING];
    applicationStore._application.type = ApplicationType[ApplicationType.EVENT];
    applicationStore._application.invoiceRecipientId = 1;
    setAndInit(true);
    spyOn(router, 'navigate');

    const decisionBtn = getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase());
    decisionBtn.click();
    tickAndDetect();

    expect(router.navigate).toHaveBeenCalledWith(['/applications', applicationStore._application.id, 'decision']);
  }));

  function setAndInit(readonly: boolean) {
    comp.readonly = readonly;
    comp.ngOnInit();
    fixture.detectChanges();
  }

  function tickAndDetect() {
    tick();
    fixture.detectChanges();
  }
});
