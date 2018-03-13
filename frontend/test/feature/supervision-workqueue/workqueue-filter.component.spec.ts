import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {SupervisionWorkItemStoreMock} from './supervision-work-item-store.mock';
import {SupervisionWorkItemStore} from '../../../src/app/feature/supervision-workqueue/supervision-work-item-store';
import {AvailableToDirective} from '../../../src/app/service/authorization/available-to.directive';
import {availableToDirectiveMockMeta, CityDistrictServiceMock, CurrentUserMock} from '../../mocks';
import {CurrentUser} from '../../../src/app/service/user/current-user';
import {SupervisionWorkItem} from '../../../src/app/model/application/supervision/supervision-work-item';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '../../../src/app/feature/common/allu-common.module';
import {WorkQueueFilterComponent} from '../../../src/app/feature/supervision-workqueue/filter/workqueue-filter.component';
import {findTranslation} from '../../../src/app/util/translations';
import {WorkQueueTab} from '../../../src/app/feature/workqueue/workqueue-tab';
import {Page} from '../../../src/app/model/common/page';
import {CityDistrictService} from '../../../src/app/service/map/city-district.service';

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
        AlluCommonModule
      ],
      declarations: [
        WorkQueueFilterComponent
      ],
      providers: [
        FormBuilder,
        {provide: SupervisionWorkItemStore, useClass: SupervisionWorkItemStoreMock},
        {provide: CurrentUser, useValue: currentUserMock},
        {provide: CityDistrictService, useClass: CityDistrictServiceMock}
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

  afterEach(() => {
    comp.ngOnDestroy();
  });

  it('should show title', () => {
    expect(de.query(By.css('h1')).nativeElement.textContent).toEqual(findTranslation('supervisionWorkqueue.title'));
  });

  it('should change owner id based on tab', () => {
    store.changeSubject.next({...store.changeSubject.getValue(), tab: WorkQueueTab.COMMON});
    expect(comp.queryForm.value.ownerId).toBeUndefined();
    store.changeSubject.next({...store.changeSubject.getValue(), tab: WorkQueueTab.OWN});
    expect(comp.queryForm.value.ownerId).toEqual(1);
  });
});
