import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {AvailableToDirective} from '@service/authorization/available-to.directive';
import {ActivatedRouteMock, availableToDirectiveMockMeta, CurrentUserMock} from '../../mocks';
import {SupervisionWorkItem} from '@model/application/supervision/supervision-work-item';
import {UntypedFormBuilder, ReactiveFormsModule} from '@angular/forms';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {WorkQueueContentComponent} from '@feature/supervision-workqueue/content/workqueue-content.component';
import {Page} from '@model/common/page';
import {ActivatedRoute} from '@angular/router';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule} from '@angular/material/sort';
import {MatTableModule} from '@angular/material/table';
import {RouterTestingModule} from '@angular/router/testing';
import {StoredFilterStoreMock} from '../common/stored-filter-store.mock';
import {StoredFilterStore} from '@service/stored-filter/stored-filter-store';
import {combineReducers, Store, StoreModule} from '@ngrx/store';
import * as fromAuth from '@feature/auth/reducers';
import * as fromSupervisionWorkQueue from '@feature/supervision-workqueue/reducers';
import {SearchSuccess} from '@feature/application/supervision/actions/supervision-task-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

const defaultItems = new Page([
  new SupervisionWorkItem(1),
  new SupervisionWorkItem(2)
]);

describe('SupervisionWorkqueueContentComponent', () => {
  let comp: WorkQueueContentComponent;
  let fixture: ComponentFixture<WorkQueueContentComponent>;
  let store: Store<fromSupervisionWorkQueue.State>;
  let de: DebugElement;
  const currentUserMock = CurrentUserMock.create(true, true);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        AlluCommonModule,
        RouterTestingModule.withRoutes([]),
        MatTableModule,
        MatSortModule,
        MatPaginatorModule,
        StoreModule.forRoot({
          'supervisionWorkQueue': combineReducers(fromSupervisionWorkQueue.reducers),
          'auth': combineReducers(fromAuth.reducers)
        })
      ],
      declarations: [
        WorkQueueContentComponent
      ],
      providers: [
        UntypedFormBuilder,
        {provide: ActivatedRoute, useValue: new ActivatedRouteMock()},
        {provide: StoredFilterStore, useClass: StoredFilterStoreMock}
      ]
    })
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
      .compileComponents();
  }));

  beforeEach(() => {
    store = TestBed.inject(Store);
    fixture = TestBed.createComponent(WorkQueueContentComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;

    store.dispatch(new SearchSuccess(ActionTargetType.SupervisionTaskWorkQueue, defaultItems));
    comp.ngOnInit();
    fixture.detectChanges();
  });

  it('should show content', () => {
    expect(de.queryAll(By.css('mat-row')).length).toEqual(defaultItems.content.length);
  });

  it('should select all when select all checkbox is clicked', () => {
    spyOn(store, 'dispatch').and.callThrough();
    const selectAll: HTMLInputElement = de.query(By.css('mat-header-cell mat-checkbox label')).nativeElement;
    selectAll.click();
    fixture.detectChanges();
    expect(store.dispatch).toHaveBeenCalledTimes(1);
    expect(de.queryAll(By.css('mat-cell mat-checkbox.mat-checkbox-checked')).length).toEqual(defaultItems.content.length);
  });

  it('should select item which checkbox is clicked', () => {
    spyOn(store, 'dispatch').and.callThrough();
    const checkboxes = de.queryAll(By.css('mat-cell mat-checkbox label')).map(elem => elem.nativeElement);
    checkboxes[1].click();
    fixture.detectChanges();
    expect(store.dispatch).toHaveBeenCalledTimes(1);
    expect(de.queryAll(By.css('mat-cell mat-checkbox.mat-checkbox-checked')).length).toEqual(1);
  });
});
