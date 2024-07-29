import {Component, DebugElement, ViewChild} from '@angular/core';
import {StoredFilterType} from '../../../app/model/user/stored-filter-type';
import {StoredFilter} from '../../../app/model/user/stored-filter';
import { ComponentFixture, discardPeriodicTasks, fakeAsync, flush, TestBed, tick, waitForAsync } from '@angular/core/testing';
import {NotificationServiceMock, UserServiceMock} from '../../mocks';
import {StoredFilterComponent} from '../../../app/feature/stored-filter/stored-filter.component';
import {UserService} from '../../../app/service/user/user-service';
import {StoredFilterModule} from '../../../app/feature/stored-filter/stored-filter.module';
import {By} from '@angular/platform-browser';
import {getMatIconButton} from '../../selector-helpers';
import {StoredFilterStore} from '../../../app/service/stored-filter/stored-filter-store';
import {NotificationService} from '../../../app/feature/notification/notification.service';
import {Observable, of, Subject} from 'rxjs/index';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

const filters = [
  new StoredFilter(1, StoredFilterType.MAP, 'map-filter-1', false, '{field1: "value1"}', 1),
  new StoredFilter(2, StoredFilterType.MAP, 'map-filter-2', false, '{field1: "value2"}', 1),
  new StoredFilter(3, StoredFilterType.MAP, 'map-filter-other-user', false, '{field1: "value2"}', 2),
  new StoredFilter(4, StoredFilterType.APPLICATION_SEARCH, 'app-filter-2', false, '{field1: "value2"}', 1)
];

@Component({
  selector: 'parent-component',
  template: `<stored-filter [type]="type"
                            [filter]="filter"
                            [selectedFilter]="selectedFilter"
                            [availableFilters]="availableFilters">
             </stored-filter>`
})
class ParentComponent {
  type = StoredFilterType.MAP;
  filter = new Subject<any>();
  selectedFilter: StoredFilter;
  availableFilters = filters;

  @ViewChild(StoredFilterComponent)
  public storedFilterComponent: StoredFilterComponent;

  onDestroy(): void {
    this.storedFilterComponent.ngOnDestroy();
  }
}

class StoredFilterStoreMock {
  save(filter: StoredFilter): Observable<StoredFilter> {
    return of(filter);
  }

  remove(id: number): Observable<object> {
    return of({});
  }

  currentChange(filter: StoredFilter): void {
  }

  currentMapFilterChange(storedFilter: StoredFilter): void {}
}

describe('StoredFilterComponent', () => {
  let parentComp: ParentComponent;
  let parentFixture: ComponentFixture<ParentComponent>;
  let filterComp: StoredFilterComponent;
  let filterStore: StoredFilterStoreMock;
  let de: DebugElement;
  let userService: UserServiceMock;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        StoredFilterModule,
        NoopAnimationsModule
      ],
      declarations: [
        ParentComponent
      ],
      providers: [
        {provide: UserService, useClass: UserServiceMock},
        {provide: StoredFilterStore, useClass: StoredFilterStoreMock},
        {provide: NotificationService, useClass: NotificationServiceMock}
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    filterStore = TestBed.inject(StoredFilterStore) as StoredFilterStoreMock;
    userService = TestBed.inject(UserService) as UserServiceMock;
    parentFixture = TestBed.createComponent(ParentComponent);
    parentComp = parentFixture.componentInstance;
    de = parentFixture.debugElement;
    parentFixture.detectChanges();
    filterComp = parentComp.storedFilterComponent;
  });

  it('throws when init without type', fakeAsync(() => {
    const initFn = () => {
      parentFixture = TestBed.createComponent(ParentComponent);
      parentComp = parentFixture.componentInstance;
      parentComp.type = undefined;
      filterComp = parentComp.storedFilterComponent;
      parentFixture.detectChanges();
      tick();
    };

    expect(initFn).toThrow();
  }));

  it('shows available filters', fakeAsync(() => {
    openFilterMenu();
    const actual = de.queryAll(By.css('.allu-menu-item .icon-suffix'));
    const expected = filters;
    expect(actual.length).toEqual(expected.length);
  }));

  it('selects clicked filter and sets selected filter', fakeAsync(() => {
    spyOn(filterStore, 'currentMapFilterChange');
    openFilterMenu();
    const options = de.queryAll(By.css('.allu-menu-item button.mat-menu-item'));

    options[0].nativeElement.click();
    parentFixture.detectChanges();
    flush();
    const expected = filters[0];
    expect(filterStore.currentMapFilterChange).toHaveBeenCalledWith(expected);
  }));

  it('emits correct event based on input type', fakeAsync(() => {
    parentComp.type = StoredFilterType.APPLICATION_SEARCH;
    parentFixture.detectChanges();

    spyOn(filterStore, 'currentChange');
    openFilterMenu();
    const options = de.queryAll(By.css('.allu-menu-item button.mat-menu-item'));

    options[3].nativeElement.click();
    parentFixture.detectChanges();
    flush();
    const expected = filters[3];
    expect(filterStore.currentChange).toHaveBeenCalledWith(expected);
  }));

  it('removes filter when remove is clicked', fakeAsync(() => {
    spyOn(filterStore, 'remove').and.callThrough();
    openFilterMenu();
    const btn = getMatIconButton(de, 'clear');
    btn.click();
    parentFixture.detectChanges();
    flush(500);

    expect(filterStore.remove).toHaveBeenCalledTimes(1);
    discardPeriodicTasks();
  }));

  function openFilterMenu(): void {
    const selectElem = de.query(By.css('.allu-icon-button'));
    selectElem.nativeElement.click();
    parentFixture.detectChanges();
    flush();
  }
});
