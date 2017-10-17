import {async, ComponentFixture, TestBed} from '@angular/core/testing';
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
import {WorkQueueFilterComponent} from '../../../src/feature/supervision-workqueue/filter/workqueue-filter.component';
import {findTranslation} from '../../../src/util/translations';
import {WorkQueueTab} from '../../../src/feature/workqueue/workqueue-tab';

const defaultItems = [
  new SupervisionWorkItem(1),
  new SupervisionWorkItem(2)
];

describe('SupervisionWorkqueueFilterComponent', () => {
  let comp: WorkQueueFilterComponent;
  let fixture: ComponentFixture<WorkQueueFilterComponent>;
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
        WorkQueueFilterComponent
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
    fixture = TestBed.createComponent(WorkQueueFilterComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;

    store.changeSubject.next({...store.changeSubject.getValue(), items: defaultItems});
    comp.ngOnInit();
    fixture.detectChanges();
  });

  afterEach(() => {
    comp.ngOnDestroy();
  });

  it('should show title', () => {
    expect(de.query(By.css('h1')).nativeElement.textContent).toEqual(findTranslation('supervisionWorkqueue.title'));
  });

  it('should change handler id based on tab', () => {
    store.changeSubject.next({...store.changeSubject.getValue(), tab: WorkQueueTab.COMMON});
    expect(comp.queryForm.value.handlerId).toBeUndefined();
    store.changeSubject.next({...store.changeSubject.getValue(), tab: WorkQueueTab.OWN});
    expect(comp.queryForm.value.handlerId).toEqual(1);
  });
});
