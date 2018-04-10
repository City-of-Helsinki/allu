import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {SupervisionWorkItemStoreMock} from './supervision-work-item-store.mock';
import {SupervisionWorkItemStore} from '../../../src/app/feature/supervision-workqueue/supervision-work-item-store';
import {AvailableToDirective} from '../../../src/app/service/authorization/available-to.directive';
import {availableToDirectiveMockMeta, CityDistrictServiceMock, CurrentUserMock, UserServiceMock} from '../../mocks';
import {SupervisionWorkItem} from '../../../src/app/model/application/supervision/supervision-work-item';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '../../../src/app/feature/common/allu-common.module';
import {WorkQueueFilterComponent} from '../../../src/app/feature/supervision-workqueue/filter/workqueue-filter.component';
import {findTranslation} from '../../../src/app/util/translations';

import {Page} from '../../../src/app/model/common/page';
import {CityDistrictService} from '../../../src/app/service/map/city-district.service';
import {StoredFilterStore} from '../../../src/app/service/stored-filter/stored-filter-store';
import {StoredFilterStoreMock} from '../common/stored-filter-store.mock';
import {StoredFilterModule} from '../../../src/app/feature/stored-filter/stored-filter.module';
import {UserService} from '../../../src/app/service/user/user-service';

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
        StoredFilterModule
      ],
      declarations: [
        WorkQueueFilterComponent
      ],
      providers: [
        FormBuilder,
        {provide: SupervisionWorkItemStore, useClass: SupervisionWorkItemStoreMock},
        {provide: CityDistrictService, useClass: CityDistrictServiceMock},
        {provide: StoredFilterStore, useClass: StoredFilterStoreMock},
        {provide: UserService, useClass: UserServiceMock}
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
