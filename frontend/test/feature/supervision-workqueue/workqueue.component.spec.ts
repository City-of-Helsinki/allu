import {WorkQueueComponent} from '../../../src/app/feature/supervision-workqueue/workqueue.component';
import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {Component, DebugElement} from '@angular/core';
import {SupervisionWorkItemStoreMock} from './supervision-work-item-store.mock';
import {SupervisionWorkItemStore} from '../../../src/app/feature/supervision-workqueue/supervision-work-item-store';
import {AvailableToDirective} from '../../../src/app/service/authorization/available-to.directive';
import {availableToDirectiveMockMeta, CurrentUserMock, UserHubMock} from '../../mocks';
import {CurrentUser} from '../../../src/app/service/user/current-user';
import {UserHub} from '../../../src/app/service/user/user-hub';
import {MatDialog} from '@angular/material';
import {SupervisionWorkItem} from '../../../src/app/model/application/supervision/supervision-work-item';
import {FormsModule} from '@angular/forms';
import {AlluCommonModule} from '../../../src/app/feature/common/allu-common.module';
import {HandlerModalModule} from '../../../src/app/feature/common/handlerModal/handler-modal.module';
import {NotificationService} from '../../../src/app/service/notification/notification.service';
import {getButtonWithText} from '../../selector-helpers';
import {Page} from '../../../src/app/model/common/page';

const defaultItems = [
  new SupervisionWorkItem(1),
  new SupervisionWorkItem(2)
];

@Component({
  selector: 'supervision-workqueue-content',
  template: ''
})
class WorkqueueContentComponentMock {
}

@Component({
  selector: 'supervision-workqueue-filter',
  template: ''
})
class WorkqueueFilterComponentMock {
}

describe('SupervisionWorkqueueComponent', () => {
  let comp: WorkQueueComponent;
  let fixture: ComponentFixture<WorkQueueComponent>;
  let store: SupervisionWorkItemStoreMock;
  let dialog: MatDialog;
  let de: DebugElement;
  let currentUserMock = CurrentUserMock.create(true, true);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        AlluCommonModule,
        HandlerModalModule
      ],
      declarations: [
        WorkQueueComponent,
        WorkqueueContentComponentMock,
        WorkqueueFilterComponentMock
      ],
      providers: [
        {provide: SupervisionWorkItemStore, useClass: SupervisionWorkItemStoreMock},
        {provide: CurrentUser, useValue: currentUserMock},
        {provide: UserHub, useClass: UserHubMock},
        MatDialog
      ]
    })
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
      .compileComponents();
  }));

  beforeEach(() => {
    store = TestBed.get(SupervisionWorkItemStore) as SupervisionWorkItemStoreMock;
    dialog = TestBed.get(MatDialog);
    fixture = TestBed.createComponent(WorkQueueComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;

    store.changeSubject.next({...store.changeSubject.getValue(), page: new Page(defaultItems)});
    comp.ngOnInit();
    fixture.detectChanges();
  });

  afterEach(() => {
    comp.ngOnDestroy();
  });

  it('should disable handler change buttons when no item is selected', fakeAsync(() => {
    de.queryAll(By.css('.mat-raised-button'))
      .map(btn => btn.nativeElement)
      .forEach(btn => expect(btn.disabled).toEqual(true));
  }));

  it('should enable handler change buttons when item is selected', fakeAsync(() => {
    store.changeSubject.next({...store.changeSubject.getValue(), selectedItems: [defaultItems[0].id]});
    fixture.detectChanges();
    tick();
    de.queryAll(By.css('.mat-raised-button'))
      .map(btn => btn.nativeElement)
      .forEach(btn => expect(btn.disabled).toEqual(false));
  }));

  it('should react to tab change', fakeAsync(() => {
    const secondTab = de.queryAll(By.css('.mat-tab-label'))[1].nativeElement;
    spyOn(store, 'tabChange').and.callThrough();
    secondTab.click();
    fixture.detectChanges();
    tick();
    expect(store.tabChange).toHaveBeenCalledTimes(1);
  }));

  it('should react changing items to self', fakeAsync(() => {
    spyOn(store, 'changeHandlerForSelected').and.callThrough();
    spyOn(NotificationService, 'translateMessage').and.stub();
    store.changeSubject.next({...store.changeSubject.getValue(), selectedItems: [defaultItems[0].id]});
    fixture.detectChanges();
    tick();

    getButtonWithText(de, 'OMAKSI').click();
    fixture.detectChanges();
    tick();

    const myself = currentUserMock.user$.getValue();
    expect(store.changeHandlerForSelected).toHaveBeenCalledWith(myself.id);
  }));

  it('should open handler modal', fakeAsync(() => {
    store.changeSubject.next({...store.changeSubject.getValue(), selectedItems: [defaultItems[0].id]});
    fixture.detectChanges();
    tick();
    spyOn(dialog, 'open').and.callThrough();

    getButtonWithText(de, 'SIIRRÄ').click();
    fixture.detectChanges();
    tick();
    expect(dialog.open).toHaveBeenCalledTimes(1);
  }));
});
