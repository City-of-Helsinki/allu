import {Component, DebugElement, ViewChild} from '@angular/core';
import {StoredFilterType} from '../../../src/app/model/user/stored-filter-type';
import {Subject} from 'rxjs/Subject';
import {StoredFilter} from '../../../src/app/model/user/stored-filter';
import {async, ComponentFixture, discardPeriodicTasks, fakeAsync, flush, TestBed, tick} from '@angular/core/testing';
import {UserServiceMock} from '../../mocks';
import {StoredFilterComponent} from '../../../src/app/feature/stored-filter/stored-filter.component';
import {StoredFilterService} from '../../../src/app/service/stored-filter/stored-filter.service';
import {Observable} from 'rxjs/Observable';
import {HttpResponse, HttpStatus} from '../../../src/app/util/http-response';
import {UserService} from '../../../src/app/service/user/user-service';
import {StoredFilterModule} from '../../../src/app/feature/stored-filter/stored-filter.module';
import {By} from '@angular/platform-browser';
import {getMatIconButton} from '../../selector-helpers';

@Component({
  selector: 'parent-component',
  template: '<stored-filter [type]="type" [filter]="filter" (filterSelected)="filterSelected($event)"></stored-filter>'
})
class ParentComponent {
  type = StoredFilterType.MAP;
  filter = new Subject<any>();
  selectedFilter: StoredFilter;

  @ViewChild(StoredFilterComponent)
  public storedFilterComponent: StoredFilterComponent;

  filterSelected(filter: StoredFilter): void {
    this.selectedFilter = filter;
  }

  onDestroy(): void {
    this.storedFilterComponent.ngOnDestroy();
  }
}

class StoredFilterServiceMock {
  filters = [
    new StoredFilter(1, StoredFilterType.MAP, 'map-filter-1', false, '{field1: "value1"}', 1),
    new StoredFilter(2, StoredFilterType.MAP, 'map-filter-2', false, '{field1: "value2"}', 1),
    new StoredFilter(3, StoredFilterType.MAP, 'map-filter-other-user', false, '{field1: "value2"}', 2),
    new StoredFilter(4, StoredFilterType.APPLICATION_SEARCH, 'app-filter-2', false, '{field1: "value2"}', 1)
  ];

  findByUserAndType(userId: number, type: StoredFilterType): Observable<StoredFilter[]> {
    return Observable.of(this.findByUserAndTypeImmediate(userId, type));
  }

  findByUserAndTypeImmediate(userId: number, type: StoredFilterType): StoredFilter[] {
    return this.filters.filter(f => f.userId === userId && f.type === type);
  }

  save(filter: StoredFilter): Observable<StoredFilter[]> {
    this.filters.push(filter);
    return Observable.of(this.filters);
  }

  remove(id: number): Observable<HttpResponse> {
    this.filters = this.filters.filter(f => f.id !== id);
    return Observable.of(new HttpResponse(HttpStatus.OK));
  }

  setAsDefault(id: number): Observable<HttpResponse> {
    return Observable.of(new HttpResponse(HttpStatus.OK));
  }
}

const userId = 1;

describe('StoredFilterComponent', () => {
  let parentComp: ParentComponent;
  let parentFixture: ComponentFixture<ParentComponent>;
  let filterComp: StoredFilterComponent;
  let filterService: StoredFilterServiceMock;
  let de: DebugElement;
  let userService: UserServiceMock;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        StoredFilterModule
      ],
      declarations: [
        ParentComponent
      ],
      providers: [
        {provide: UserService, useClass: UserServiceMock},
        {provide: StoredFilterService, useClass: StoredFilterServiceMock}
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    filterService = TestBed.get(StoredFilterService) as StoredFilterServiceMock;
    userService = TestBed.get(UserService) as UserServiceMock;
    parentFixture = TestBed.createComponent(ParentComponent);
    parentComp = parentFixture.componentInstance;
    de = parentFixture.debugElement;
    parentFixture.detectChanges();
    filterComp = parentComp.storedFilterComponent;
  });

  it('loads filters on init', fakeAsync(() => {
    expect(filterComp).toBeTruthy();

    const expectedFilters = filterService.findByUserAndTypeImmediate(userId, StoredFilterType.MAP);

    filterComp.availableFilters.subscribe(filters => {
      expect(filters.length).toEqual(expectedFilters.length);
    });
  }));

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

  it('selects default filter on init', fakeAsync(() => {
    filterService.filters[0].defaultFilter = true;
    recreateParent();
    tick();
    expect(filterComp.selectedFilter).toEqual(filterService.filters[0]);
  }));

  it('shows available filters', fakeAsync(() => {
    openFilterMenu();
    const actual = de.queryAll(By.css('.allu-menu-item .icon-suffix'));
    const expected = filterService.findByUserAndTypeImmediate(userId, StoredFilterType.MAP);
    expect(actual.length).toEqual(expected.length);
  }));

  it('selects clicked filter and emits event', fakeAsync(() => {
    openFilterMenu();
    const options = de.queryAll(By.css('.allu-menu-item button.mat-menu-item'));

    options[0].nativeElement.click();
    parentFixture.detectChanges();
    flush();

    const expected = filterService.filters[0];
    expect(filterComp.selectedFilter).toEqual(expected);
    expect(parentComp.selectedFilter).toEqual(expected);
  }));

  it('removes filter when remove is clicked', fakeAsync(() => {
    spyOn(filterService, 'remove').and.callThrough();
    openFilterMenu();
    const btn = getMatIconButton(de, 'clear');
    btn.click();
    parentFixture.detectChanges();
    flush(500);

    expect(filterService.remove).toHaveBeenCalledTimes(1);
    discardPeriodicTasks();
  }));

  function openFilterMenu(): void {
    const selectElem = de.query(By.css('.allu-icon-button'));
    selectElem.nativeElement.click();
    parentFixture.detectChanges();
    flush();
  }

  function recreateParent(): void {
    parentFixture = TestBed.createComponent(ParentComponent);
    parentComp = parentFixture.componentInstance;
    filterComp = parentComp.storedFilterComponent;
    parentFixture.detectChanges();
  }
});
