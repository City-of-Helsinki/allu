import {Component, DebugElement} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {AlluCommonModule} from '../../../src/app/feature/common/allu-common.module';
import {ApplicationActionsComponent} from '../../../src/app/feature/application/info/application-actions.component';
import {ApplicationStoreMock, availableToDirectiveMockMeta, CurrentUserMock, NotificationServiceMock, RouterMock} from '../../mocks';
import {ApplicationStore} from '../../../src/app/service/application/application-store';
import {ActivatedRoute, Router} from '@angular/router';
import {AvailableToDirective} from '../../../src/app/service/authorization/available-to.directive';
import {getButtonWithText} from '../../selector-helpers';
import {findTranslation} from '../../../src/app/util/translations';
import {RouterTestingModule} from '@angular/router/testing';
import {AttachmentInfo} from '../../../src/app/model/application/attachment/attachment-info';
import {Location} from '../../../src/app/model/common/location';
import {ApplicationType} from '../../../src/app/model/application/type/application-type';
import {NotificationService} from '../../../src/app/service/notification/notification.service';
import {ApplicationStatus} from '../../../src/app/model/application/application-status';
import {MatDialog} from '@angular/material';
import {User} from '../../../src/app/model/user/user';
import {UserHub} from '../../../src/app/service/user/user-hub';
import {UserSearchCriteria} from '../../../src/app/model/user/user-search-criteria';
import {EMPTY, Observable, of} from 'rxjs/index';
import {InformationAcceptanceModalEvents} from '@feature/information-request/acceptance/information-acceptance-modal-events';
import {StoreModule} from '@ngrx/store';

class MatDialogRefMock {
  afterClosed(): Observable<any> {
    return EMPTY;
  }
}

class MatDialogMock {
  open(componentOrTemplateRef: any, config?: any): any {
    return undefined;
  }
}

class UserHubMock {
  searchUsers(criteria: UserSearchCriteria) { return of([]); }
}


@Component({
  template: `
    <application-actions
      *ngIf="visible"
      [readonly]="readonly"
      [valid]="valid"
      [pendingClientData]="pendingClientData"
      [submitPending]="submitPending"></application-actions>`
})
class TestHostComponent {
  readonly = true;
  valid = true;
  submitPending = false;
  visible = true;
  pendingClientData = false;
}

describe('ApplicationActionsComponent', () => {
  let comp: TestHostComponent;
  let fixture: ComponentFixture<TestHostComponent>;
  let de: DebugElement;
  let router: RouterMock;
  let applicationStore: ApplicationStoreMock;
  let dialog: MatDialogMock;
  let userHub: UserHubMock;
  let notification: NotificationServiceMock;
  const currentUserMock = CurrentUserMock.create(true, true);
  const applicationId = 15;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
        StoreModule.forRoot({}),
      ],
      declarations: [
        TestHostComponent,
        ApplicationActionsComponent
      ],
      providers: [
        {provide: Router, useClass: RouterMock},
        {provide: ActivatedRoute},
        {provide: ApplicationStore, useClass: ApplicationStoreMock},
        {provide: NotificationService, useClass: NotificationServiceMock},
        {provide: MatDialog, useClass: MatDialogMock},
        {provide: UserHub, useClass: UserHubMock},
        InformationAcceptanceModalEvents
      ]
    }).overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestHostComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
    router = TestBed.get(Router) as RouterMock;
    applicationStore = TestBed.get(ApplicationStore) as ApplicationStoreMock;
    dialog = TestBed.get(MatDialog) as MatDialogMock;
    userHub = TestBed.get(UserHub) as UserHubMock;
    notification = TestBed.get(NotificationService) as NotificationServiceMock;

    const app = applicationStore.snapshot.application;
    app.id  = applicationId;
    applicationStore.applicationChange(app);
    comp.valid = true;
    fixture.detectChanges();
  });

  afterEach(() => {
    comp.visible = false;
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

  it('should hide edit button for decided application', () => {
    applicationStore.updateStatus(ApplicationStatus.DECISION);
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('common.button.edit').toUpperCase())).toBeUndefined();
  });

  it('should show replace button for decided application and hide for others', () => {
    applicationStore.updateStatus(ApplicationStatus.DECISION);
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.replace').toUpperCase())).toBeDefined();
    applicationStore.updateStatus(ApplicationStatus.HANDLING);
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.replace').toUpperCase())).toBeUndefined();
  });

  it('should replace application', fakeAsync(() => {
    applicationStore.updateStatus(ApplicationStatus.DECISION);
    setAndInit(true);
    spyOn(router, 'navigate');
    spyOn(applicationStore, 'replace').and.returnValue(applicationStore.application);
    spyOn(notification, 'translateSuccess');

    const replaceBtn = getButtonWithText(de, findTranslation('application.button.replace').toUpperCase());
    replaceBtn.click();
    tickAndDetect();

    expect(applicationStore.replace).toHaveBeenCalled();
    expect(notification.translateSuccess).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/applications', applicationId, 'summary']);
  }));

  it('should copy application as new', () => {
    setAndInit(true);
    spyOn(router, 'navigate');
    const location = new Location(12, 12, 12, new Date());

    const preferredOwner = new User(52);
    spyOn(userHub, 'searchUsers').and.returnValue(of([preferredOwner]));

    const application = applicationStore.snapshot.application;
    application.id = 1;
    application.attachmentList = [new AttachmentInfo(15, 'type', 'name'), new AttachmentInfo(10, 'type', 'name')];
    application.locations = [location];

    const copyAsNewBtn = getButtonWithText(de, findTranslation('application.button.copy').toUpperCase());
    copyAsNewBtn.click();

    const copy = applicationStore.snapshot.applicationCopy;
    expect(copy.id).toBeUndefined();
    expect(copy.attachmentList).toEqual([]);
    expect(copy.locations.length).toEqual(1);
    expect(copy.locations[0].startTime).toEqual(location.startTime);
    expect(copy.owner).toEqual(preferredOwner);
    expect(copy.handler).toBeUndefined();
    expect(router.navigate).toHaveBeenCalledWith(['/applications/edit']);
  });

  it('should delete NOTE', fakeAsync(() => {
    applicationStore.updateType(ApplicationType.NOTE);
    setAndInit(true);
    spyOn(router, 'navigate');
    spyOn(applicationStore, 'delete').and.returnValue(of({}));
    spyOn(notification, 'translateSuccess');

    const removeBtn = getButtonWithText(de, findTranslation('common.button.remove').toUpperCase());
    removeBtn.click();
    tickAndDetect();

    expect(applicationStore.delete).toHaveBeenCalledWith(applicationId);
    expect(notification.translateSuccess).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  }));

  it('should hide delete for other than NOTE', () => {
    applicationStore.updateType(ApplicationType.EVENT);
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('common.button.remove').toUpperCase())).toBeUndefined();
  });

  it('should show / hide save button on depending on readonly', () => {
    setAndInit(false);
    expect(getButtonWithText(de, findTranslation('common.button.save').toUpperCase())).toBeDefined();
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('common.button.save').toUpperCase())).toBeUndefined();
  });

  it('should show cancel button when status is decision or before', () => {
    applicationStore.updateStatus(ApplicationStatus.DECISION);
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.cancel').toUpperCase())).toBeDefined();

    applicationStore.updateStatus(ApplicationStatus.FINISHED);
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.cancel').toUpperCase())).toBeUndefined();
  });

  it('should change application as canceled on approve', fakeAsync(() => {
    applicationStore.updateStatus(ApplicationStatus.HANDLING);
    setAndInit(true);
    spyOn(router, 'navigate');
    spyOn(applicationStore, 'changeStatus').and.callThrough();
    spyOn(notification, 'translateSuccess');

    const dialogRef = new MatDialogRefMock();
    spyOn(dialogRef, 'afterClosed').and.returnValue(of(true));
    spyOn(dialog, 'open').and.returnValue(dialogRef);

    const cancelBtn = getButtonWithText(de, findTranslation('application.button.cancel').toUpperCase());
    cancelBtn.click();
    tickAndDetect();

    expect(applicationStore.changeStatus).toHaveBeenCalledWith(applicationId, ApplicationStatus.CANCELLED);
    expect(notification.translateSuccess).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/workqueue']);
  }));

  it('should ignore cancel when cancel not approved', fakeAsync(() => {
    applicationStore.updateStatus(ApplicationStatus.HANDLING);
    setAndInit(true);
    spyOn(router, 'navigate');
    spyOn(applicationStore, 'changeStatus').and.returnValue(applicationStore.application);
    spyOn(notification, 'translateSuccess');

    const dialogRef = new MatDialogRefMock();
    spyOn(dialogRef, 'afterClosed').and.returnValue(of(false));
    spyOn(dialog, 'open').and.returnValue(dialogRef);

    const cancelBtn = getButtonWithText(de, findTranslation('application.button.cancel').toUpperCase());
    cancelBtn.click();
    tickAndDetect();

    expect(applicationStore.changeStatus).not.toHaveBeenCalled();
    expect(notification.translateSuccess).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  }));

  it('should show move to handling depending on status', () => {
    applicationStore.updateStatus(ApplicationStatus.PENDING);
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.toHandling').toUpperCase())).toBeDefined();

    applicationStore.updateStatus(ApplicationStatus.HANDLING);
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('common.button.toHandling').toUpperCase())).toBeUndefined();
  });

  it('should move application to handling', fakeAsync(() => {
    applicationStore.updateStatus(ApplicationStatus.PENDING);
    setAndInit(true);
    spyOn(router, 'navigate');
    spyOn(applicationStore, 'changeStatus').and.callThrough();
    spyOn(notification, 'translateSuccess');

    const toHandlingBtn = getButtonWithText(de, findTranslation('application.button.toHandling').toUpperCase());
    toHandlingBtn.click();
    tickAndDetect();

    expect(applicationStore.changeStatus).toHaveBeenCalledWith(applicationId, ApplicationStatus.HANDLING);
    expect(notification.translateSuccess).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/applications', applicationId, 'edit']);
  }));

  it('should show move to decision depending on status and type', () => {
    // Don't show for status before HANDLING
    let app = applicationStore.snapshot.application;
    app.statusEnum = ApplicationStatus.PENDING;
    app.type = ApplicationType[ApplicationType.EVENT];
    applicationStore.applicationChange(app);
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase())).toBeUndefined();

    // Don't show for states HANDLING and after
    app = applicationStore.snapshot.application;
    app.statusEnum = ApplicationStatus.HANDLING;
    app.type = ApplicationType[ApplicationType.NOTE];
    applicationStore.applicationChange(app);
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase())).toBeUndefined();

    // Should show
    app = applicationStore.snapshot.application;
    app.statusEnum = ApplicationStatus.HANDLING;
    app.type = ApplicationType[ApplicationType.EVENT];
    applicationStore.applicationChange(app);
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase())).toBeDefined();
  });

  it('should hide actions when pending on client', () => {
    const app = applicationStore.snapshot.application;
    app.statusEnum = ApplicationStatus.PENDING_CLIENT;
    app.type = ApplicationType[ApplicationType.EVENT];
    applicationStore.applicationChange(app);
    setAndInit(true);
    const buttons = de.queryAll(By.css('button'));
    expect(buttons.length).toEqual(0, `Visible action buttons ${buttons.length}`);
  });

  it('should hide actions when waiting for contract approval', () => {
    const app = applicationStore.snapshot.application;
    app.statusEnum = ApplicationStatus.WAITING_CONTRACT_APPROVAL;
    app.type = ApplicationType[ApplicationType.EVENT];
    applicationStore.applicationChange(app);
    setAndInit(true);
    const buttons = de.queryAll(By.css('button'));
    expect(buttons.length).toEqual(0, `Visible action buttons ${buttons.length}`);
  });

  it('should show only show pending data when pending client data', () => {
    const app = applicationStore.snapshot.application;
    app.statusEnum = ApplicationStatus.PENDING;
    app.type = ApplicationType[ApplicationType.EVENT];
    applicationStore.applicationChange(app);
    comp.pendingClientData = true;
    setAndInit(true);
    const buttons = de.queryAll(By.css('button'));
    expect(buttons.length).toEqual(1, `More than one button visible ${buttons.length}`);
    expect(getButtonWithText(de, findTranslation('application.button.showPending').toUpperCase())).toBeDefined();
  });

  it('should disable to decision making button when no invoice recipient is set', () => {
    const app = applicationStore.snapshot.application;
    app.statusEnum = ApplicationStatus.HANDLING;
    app.type = ApplicationType[ApplicationType.EVENT];
    applicationStore.applicationChange(app);
    setAndInit(true);
    const decisionBtn = getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase());
    expect(decisionBtn.getAttribute('ng-reflect-disabled')).toEqual('true');
  });

  it('should enable to decision making button when invoice recipient is set', () => {
    const app = applicationStore.snapshot.application;
    app.statusEnum = ApplicationStatus.HANDLING;
    app.type = ApplicationType[ApplicationType.EVENT];
    app.invoiceRecipientId = 1;
    applicationStore.applicationChange(app);
    setAndInit(true);
    const decisionBtn = getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase());
    expect(decisionBtn.getAttribute('ng-reflect-disabled')).toEqual('false');
  });

  it('should enable to decision making button when application is set to not billable', () => {
    const app = applicationStore.snapshot.application;
    app.statusEnum = ApplicationStatus.HANDLING;
    app.type = ApplicationType[ApplicationType.EVENT];
    app.notBillable = true;
    applicationStore.applicationChange(app);
    setAndInit(true);
    const decisionBtn = getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase());
    expect(decisionBtn.getAttribute('ng-reflect-disabled')).toEqual('false');
  });

  it('should change status of Cable report to decision making', fakeAsync(() => {
    const app = applicationStore.snapshot.application;
    app.statusEnum = ApplicationStatus.HANDLING;
    app.type = ApplicationType[ApplicationType.CABLE_REPORT];
    app.invoiceRecipientId = 1;
    applicationStore.applicationChange(app);
    setAndInit(true);
    spyOn(router, 'navigate');
    spyOn(applicationStore, 'changeStatus').and.callThrough();
    spyOn(notification, 'translateSuccess');

    const decisionBtn = getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase());
    decisionBtn.click();
    tickAndDetect();

    expect(applicationStore.changeStatus).toHaveBeenCalledWith(applicationId, ApplicationStatus.DECISIONMAKING);
    expect(notification.translateSuccess).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/applications', app.id, 'decision']);
  }));

  it('should navigate to decision making for other application types', fakeAsync(() => {
    const app = applicationStore.snapshot.application;
    app.statusEnum = ApplicationStatus.HANDLING;
    app.type = ApplicationType[ApplicationType.EVENT];
    app.invoiceRecipientId = 1;
    applicationStore.applicationChange(app);
    setAndInit(true);
    spyOn(router, 'navigate');

    const decisionBtn = getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase());
    decisionBtn.click();
    tickAndDetect();

    expect(router.navigate).toHaveBeenCalledWith(['/applications', app.id, 'decision']);
  }));

  function setAndInit(readonly: boolean) {
    comp.readonly = readonly;
    comp.visible = false;
    fixture.detectChanges();
    comp.visible = true;
    fixture.detectChanges();
  }

  function tickAndDetect() {
    tick();
    fixture.detectChanges();
  }
});
