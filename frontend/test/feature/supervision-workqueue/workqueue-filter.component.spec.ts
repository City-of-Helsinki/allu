import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {of} from 'rxjs/index';
import {SupervisionWorkItemStoreMock} from './supervision-work-item-store.mock';
import {SupervisionWorkItemStore} from '../../../src/app/feature/supervision-workqueue/supervision-work-item-store';
import {AvailableToDirective} from '../../../src/app/service/authorization/available-to.directive';
import {availableToDirectiveMockMeta, CurrentUserMock, NotificationServiceMock, UserServiceMock} from '../../mocks';
import {SupervisionWorkItem} from '../../../src/app/model/application/supervision/supervision-work-item';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '../../../src/app/feature/common/allu-common.module';
import {WorkQueueFilterComponent} from '../../../src/app/feature/supervision-workqueue/filter/workqueue-filter.component';
import {findTranslation} from '../../../src/app/util/translations';
import {Page} from '../../../src/app/model/common/page';
import {StoredFilterStore} from '../../../src/app/service/stored-filter/stored-filter-store';
import {StoredFilterStoreMock} from '../common/stored-filter-store.mock';
import {StoredFilterModule} from '../../../src/app/feature/stored-filter/stored-filter.module';
import {UserService} from '../../../src/app/service/user/user-service';
import {NotificationService} from '../../../src/app/service/notification/notification.service';
import {StoreModule} from '@ngrx/store';
import * as fromRoot from '../../../src/app/feature/allu/reducers';

const defaultItems = new Page([
  new SupervisionWorkItem(1),
  new SupervisionWorkItem(2)
]);

describe('SupervisionWorkqueueFilterComponent', () => {
  let comp: WorkQueueFilterComponent;
  let fixture: ComponentFixture<WorkQueueFilterComponent>;
  let store: SupervisionWorkItemStoreMock;
  let de: DebugElement;
  const currentUserMock = CurrentUserMock.create(true, true);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        AlluCommonModule,
        StoredFilterModule,
        StoreModule.forRoot({
          ...fromRoot.reducers
        }),
      ],
      declarations: [
        WorkQueueFilterComponent
      ],
      providers: [
        FormBuilder,
        {provide: SupervisionWorkItemStore, useClass: SupervisionWorkItemStoreMock},
        {provide: StoredFilterStore, useClass: StoredFilterStoreMock},
        {provide: UserService, useClass: UserServiceMock},
        {provide: NotificationService, useClass: NotificationServiceMock},
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

    store.changeSubject.next({...store.changeSubject.getValue(), page: defaultItems});
    comp.ngOnInit();
    fixture.detectChanges();
  });

  it('should show title', () => {
    expect(de.query(By.css('h1')).nativeElement.textContent)
      .toEqual(findTranslation('supervisionWorkqueue.title').toUpperCase());
  });
});
