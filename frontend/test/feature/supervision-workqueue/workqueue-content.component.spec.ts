import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {SupervisionWorkItemStoreMock} from './supervision-work-item-store.mock';
import {SupervisionWorkItemStore} from '../../../src/feature/supervision-workqueue/supervision-work-item-store';
import {AvailableToDirective} from '../../../src/service/authorization/available-to.directive';
import {availableToDirectiveMockMeta, CurrentUserMock} from '../../mocks';
import {CurrentUser} from '../../../src/service/user/current-user';
import {SupervisionWorkItem} from '../../../src/model/application/supervision/supervision-work-item';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '../../../src/feature/common/allu-common.module';
import {WorkQueueContentComponent} from '../../../src/feature/supervision-workqueue/content/workqueue-content.component';

const defaultItems = [
  new SupervisionWorkItem(1),
  new SupervisionWorkItem(2)
];

describe('SupervisionWorkqueueContentComponent', () => {
  let comp: WorkQueueContentComponent;
  let fixture: ComponentFixture<WorkQueueContentComponent>;
  let store: SupervisionWorkItemStoreMock;
  let de: DebugElement;
  let currentUserMock = CurrentUserMock.create(true, true);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        AlluCommonModule
      ],
      declarations: [
        WorkQueueContentComponent
      ],
      providers: [
        FormBuilder,
        {provide: SupervisionWorkItemStore, useClass: SupervisionWorkItemStoreMock},
        {provide: CurrentUser, useValue: currentUserMock}
      ]
    })
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
      .compileComponents();
  }));

  beforeEach(() => {
    store = TestBed.get(SupervisionWorkItemStore) as SupervisionWorkItemStoreMock;
    fixture = TestBed.createComponent(WorkQueueContentComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;

    store.changeSubject.next({...store.changeSubject.getValue(), items: defaultItems});
    comp.ngOnInit();
    fixture.detectChanges();
  });

  afterEach(() => {
    comp.ngOnDestroy();
  });

  it('should show content', fakeAsync(() => {
    expect(de.queryAll(By.css('tr.clickable')).length).toEqual(defaultItems.length);
  }));

  it('should check all selected checkbox based on state', fakeAsync(() => {
    store.changeSubject.next({...store.changeSubject.getValue(), allSelected: true});
    fixture.detectChanges();
    tick();
    expect(de.query(By.css('th .mat-checkbox-checked'))).toBeDefined();

    store.changeSubject.next({...store.changeSubject.getValue(), allSelected: false});
    fixture.detectChanges();
    tick();
    expect(de.query(By.css('th .mat-checkbox-checked'))).toBeFalsy();
  }));

  it('should check item checkboxes based on state', fakeAsync(() => {
    expect(de.queryAll(By.css('td .mat-checkbox-checked')).length).toEqual(0);

    store.changeSubject.next({...store.changeSubject.getValue(), selectedItems: defaultItems.map(item => item.id)});
    fixture.detectChanges();
    tick();
    expect(de.queryAll(By.css('td .mat-checkbox-checked')).length).toEqual(2);
  }));

  it('should notify store when items are checked', fakeAsync(() => {
    const checkbox = de.query(By.css('td label.mat-checkbox-layout')).nativeElement;
    spyOn(store, 'toggleSingle');
    checkbox.click();
    fixture.detectChanges();
    tick();
    expect(store.toggleSingle).toHaveBeenCalledWith(defaultItems[0].id, true);
  }));
});
