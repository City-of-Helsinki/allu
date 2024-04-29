import {Component, DebugElement} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {ApplicationActionsComponent} from '@feature/application/info/application-actions.component';
import {
  ApplicationStoreMock,
  availableToDirectiveMockMeta,
  CurrentUserMock,
  NotificationServiceMock,
  RouterMock,
  UserServiceMock
} from '../../mocks';
import {ApplicationStore} from '@service/application/application-store';
import {ActivatedRoute, Router} from '@angular/router';
import {AvailableToDirective} from '@service/authorization/available-to.directive';
import {getButtonWithText} from '../../selector-helpers';
import {findTranslation} from '@util/translations';
import {RouterTestingModule} from '@angular/router/testing';
import {AttachmentInfo} from '@model/application/attachment/attachment-info';
import {Location} from '@model/common/location';
import {ApplicationType} from '@model/application/type/application-type';
import {ApplicationStatus} from '@model/application/application-status';
import {MatLegacyDialog as MatDialog} from '@angular/material/legacy-dialog';
import {User} from '@model/user/user';
import {EMPTY, Observable, of} from 'rxjs/index';
import {StoreModule} from '@ngrx/store';
import {UserService} from '@service/user/user-service';
import {NotificationService} from '@feature/notification/notification.service';
import {ShortTermRental} from '@app/model/application/short-term-rental/short-term-rental';
import {TerminationService} from '@feature/decision/termination/termination-service';
import {TerminationModalService} from '@feature/decision/termination/termination-modal-service';
import {AttachmentType} from '@model/application/attachment/attachment-type';

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

class TerminationServiceMock {
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
  let userService: UserServiceMock;
  const currentUserMock = CurrentUserMock.create(true, true);
  const applicationId = 15;

  beforeEach(waitForAsync(() => {
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
        {provide: ActivatedRoute, useValue: {}},
        {provide: ApplicationStore, useClass: ApplicationStoreMock},
        {provide: MatDialog, useClass: MatDialogMock},
        {provide: UserService, useClass: UserServiceMock},
        {provide: NotificationService, useClass: NotificationServiceMock},
        {provide: TerminationService, useClass: TerminationServiceMock},
        {provide: TerminationModalService, useValue: {}}
      ]
    }).overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestHostComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
    router = TestBed.inject(Router) as RouterMock;
    applicationStore = TestBed.inject(ApplicationStore) as unknown as ApplicationStoreMock;
    dialog = TestBed.inject(MatDialog) as MatDialogMock;
    userService = TestBed.inject(UserService) as UserServiceMock;

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

    const replaceBtn = getButtonWithText(de, findTranslation('application.button.replace').toUpperCase());
    replaceBtn.click();
    tickAndDetect();

    expect(applicationStore.replace).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/applications', applicationId, 'summary']);
  }));

  it('should copy application as new', () => {
    setAndInit(true);
    spyOn(router, 'navigate');
    const location = new Location(12, 12, 12, new Date());

    const preferredOwner = new User(52);
    spyOn(userService, 'search').and.returnValue(of([preferredOwner]));

    const application = applicationStore.snapshot.application;
    application.id = 1;
    application.attachmentList = [
      new AttachmentInfo(15, AttachmentType.ADDED_BY_HANDLER, 'name'),
      new AttachmentInfo(10, AttachmentType.ADDED_BY_HANDLER, 'name')
    ];
    application.locations = [location];
    application.extension = new ShortTermRental();

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

    const removeBtn = getButtonWithText(de, findTranslation('common.button.remove').toUpperCase());
    removeBtn.click();
    tickAndDetect();

    expect(applicationStore.delete).toHaveBeenCalledWith(applicationId);
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

    const dialogRef = new MatDialogRefMock();
    spyOn(dialogRef, 'afterClosed').and.returnValue(of(true));
    spyOn(dialog, 'open').and.returnValue(dialogRef);

    const cancelBtn = getButtonWithText(de, findTranslation('application.button.cancel').toUpperCase());
    cancelBtn.click();
    tickAndDetect();

    expect(applicationStore.changeStatus).toHaveBeenCalledWith(applicationId, ApplicationStatus.CANCELLED);
    expect(router.navigate).toHaveBeenCalledWith(['/workqueue']);
  }));

  it('should ignore cancel when cancel not approved', fakeAsync(() => {
    applicationStore.updateStatus(ApplicationStatus.HANDLING);
    setAndInit(true);
    spyOn(router, 'navigate');
    spyOn(applicationStore, 'changeStatus').and.returnValue(applicationStore.application);

    const dialogRef = new MatDialogRefMock();
    spyOn(dialogRef, 'afterClosed').and.returnValue(of(false));
    spyOn(dialog, 'open').and.returnValue(dialogRef);

    const cancelBtn = getButtonWithText(de, findTranslation('application.button.cancel').toUpperCase());
    cancelBtn.click();
    tickAndDetect();

    expect(applicationStore.changeStatus).not.toHaveBeenCalled();
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

    const toHandlingBtn = getButtonWithText(de, findTranslation('application.button.toHandling').toUpperCase());
    toHandlingBtn.click();
    tickAndDetect();

    expect(applicationStore.changeStatus).toHaveBeenCalledWith(applicationId, ApplicationStatus.HANDLING);
    expect(router.navigate).toHaveBeenCalledWith(['/applications', applicationId, 'edit']);
  }));

  it('should show move to decision depending on status and type', () => {
    // Don't show for status before HANDLING
    let app = applicationStore.snapshot.application;
    app.status = ApplicationStatus.PENDING;
    app.type = ApplicationType[ApplicationType.EVENT];
    applicationStore.applicationChange(app);
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase())).toBeUndefined();

    // Don't show for states HANDLING and after
    app = applicationStore.snapshot.application;
    app.status = ApplicationStatus.HANDLING;
    app.type = ApplicationType[ApplicationType.NOTE];
    applicationStore.applicationChange(app);
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase())).toBeUndefined();

    // Should show
    app = applicationStore.snapshot.application;
    app.status = ApplicationStatus.HANDLING;
    app.type = ApplicationType[ApplicationType.EVENT];
    applicationStore.applicationChange(app);
    setAndInit(true);
    expect(getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase())).toBeDefined();
  });

  it('should hide actions when pending on client', () => {
    const app = applicationStore.snapshot.application;
    app.status = ApplicationStatus.PENDING_CLIENT;
    app.type = ApplicationType[ApplicationType.EVENT];
    applicationStore.applicationChange(app);
    setAndInit(true);
    const buttons = de.queryAll(By.css('button'));
    expect(buttons.length).toEqual(0, `Visible action buttons ${buttons.length}`);
  });

  it('should hide actions when waiting for contract approval', () => {
    const app = applicationStore.snapshot.application;
    app.status = ApplicationStatus.WAITING_CONTRACT_APPROVAL;
    app.type = ApplicationType[ApplicationType.EVENT];
    applicationStore.applicationChange(app);
    setAndInit(true);
    const buttons = de.queryAll(By.css('button'));
    expect(buttons.length).toEqual(0, `Visible action buttons ${buttons.length}`);
  });

  it('should show only show pending data and cancel when pending client data', () => {
    const app = applicationStore.snapshot.application;
    app.status = ApplicationStatus.PENDING;
    app.type = ApplicationType[ApplicationType.EVENT];
    applicationStore.applicationChange(app);
    comp.pendingClientData = true;
    setAndInit(true);
    const buttons = de.queryAll(By.css('button'));
    expect(buttons.length).toEqual(2, `More than two buttons visible ${buttons.length}`);
    expect(getButtonWithText(de, findTranslation('application.button.showPending').toUpperCase())).toBeDefined();
    expect(getButtonWithText(de, findTranslation('application.button.cancel').toUpperCase())).toBeDefined();
  });

  it('should disable to decision making button when no invoice recipient is set', () => {
    const app = applicationStore.snapshot.application;
    app.status = ApplicationStatus.HANDLING;
    app.type = ApplicationType[ApplicationType.EVENT];
    applicationStore.applicationChange(app);
    setAndInit(true);
    const decisionBtn = getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase());
    expect(decisionBtn.getAttribute('ng-reflect-disabled')).toEqual('true');
  });

  it('should enable to decision making button when invoice recipient is set', () => {
    const app = applicationStore.snapshot.application;
    app.status = ApplicationStatus.HANDLING;
    app.type = ApplicationType[ApplicationType.EVENT];
    app.invoiceRecipientId = 1;
    applicationStore.applicationChange(app);
    setAndInit(true);
    const decisionBtn = getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase());
    expect(decisionBtn.getAttribute('ng-reflect-disabled')).toEqual('false');
  });

  it('should enable to decision making button when application is set to not billable', () => {
    const app = applicationStore.snapshot.application;
    app.status = ApplicationStatus.HANDLING;
    app.type = ApplicationType[ApplicationType.EVENT];
    app.notBillable = true;
    applicationStore.applicationChange(app);
    setAndInit(true);
    const decisionBtn = getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase());
    expect(decisionBtn.getAttribute('ng-reflect-disabled')).toEqual('false');
  });

  it('should navigate to decision making', fakeAsync(() => {
    const app = applicationStore.snapshot.application;
    app.status = ApplicationStatus.HANDLING;
    app.type = ApplicationType[ApplicationType.EVENT];
    app.invoiceRecipientId = 1;
    applicationStore.applicationChange(app);
    setAndInit(true);
    spyOn(router, 'navigate');

    const decisionBtn = getButtonWithText(de, findTranslation('application.button.toDecision').toUpperCase());
    decisionBtn.click();
    tickAndDetect();

    expect(router.navigate).toHaveBeenCalledWith(['/applications', app.id, 'summary', 'decision']);
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
