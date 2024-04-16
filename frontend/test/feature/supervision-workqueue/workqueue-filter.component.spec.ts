import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {Component, DebugElement, Input} from '@angular/core';
import {By} from '@angular/platform-browser';
import {AvailableToDirective} from '@service/authorization/available-to.directive';
import {availableToDirectiveMockMeta, CurrentUserMock, UserServiceMock} from '../../mocks';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {WorkQueueFilterComponent} from '@feature/supervision-workqueue/filter/workqueue-filter.component';
import {findTranslation} from '@util/translations';
import {StoredFilterStore} from '@service/stored-filter/stored-filter-store';
import {StoredFilterStoreMock} from '@test/feature/common/stored-filter-store.mock';
import {UserService} from '@service/user/user-service';
import {combineReducers, Store, StoreModule} from '@ngrx/store';
import * as fromCityDistricts from '@feature/allu/reducers/city-district-reducer';
import * as fromSupervisionWorkQueue from '@feature/supervision-workqueue/reducers';
import * as fromAuth from '@feature/auth/reducers';
import {StoredFilterType} from '@model/user/stored-filter-type';
import {StoredFilter} from '@model/user/stored-filter';

@Component({
  selector: 'stored-filter',
  template: ''
})
class MockStoredFilterComponent {
  @Input() type: StoredFilterType;
  @Input() filter: any;
  @Input() selectedFilter: StoredFilter;
  @Input() availableFilters: StoredFilter[];
  @Input() classNames: string[];
}

describe('SupervisionWorkqueueFilterComponent', () => {
  let comp: WorkQueueFilterComponent;
  let fixture: ComponentFixture<WorkQueueFilterComponent>;
  let store: Store<fromSupervisionWorkQueue.State>;
  let de: DebugElement;
  const currentUserMock = CurrentUserMock.create(true, true);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        AlluCommonModule,
        StoreModule.forRoot({
          'cityDistricts': fromCityDistricts.reducer,
          'supervisionWorkQueue': combineReducers(fromSupervisionWorkQueue.reducers),
          'auth': combineReducers(fromAuth.reducers)
        }),
      ],
      declarations: [
        WorkQueueFilterComponent,
        MockStoredFilterComponent
      ],
      providers: [
        FormBuilder,
        {provide: StoredFilterStore, useClass: StoredFilterStoreMock},
        {provide: UserService, useClass: UserServiceMock},
      ]
    })
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
      .compileComponents();
  }));

  beforeEach(() => {
    store = TestBed.inject(Store);
    fixture = TestBed.createComponent(WorkQueueFilterComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;

    comp.ngOnInit();
    fixture.detectChanges();
  });

  it('should show title', () => {
    expect(de.query(By.css('h1')).nativeElement.textContent)
      .toEqual(findTranslation('supervisionWorkqueue.title').toUpperCase());
  });
});
