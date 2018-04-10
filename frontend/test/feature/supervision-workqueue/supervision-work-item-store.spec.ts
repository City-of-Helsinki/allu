import {async, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {SupervisionTaskSearchCriteria} from '../../../src/app/model/application/supervision/supervision-task-search-criteria';
import {Observable} from 'rxjs/Observable';
import {SupervisionWorkItem} from '../../../src/app/model/application/supervision/supervision-work-item';
import {HttpResponse, HttpStatus} from '../../../src/app/util/http-response';
import {SupervisionWorkItemStore} from '../../../src/app/feature/supervision-workqueue/supervision-work-item-store';
import {SupervisionTaskService} from '../../../src/app/service/supervision/supervision-task.service';
import {WorkQueueTab} from '../../../src/app/feature/workqueue/workqueue-tab';
import {Page} from '../../../src/app/model/common/page';
import {CurrentUser} from '../../../src/app/service/user/current-user';
import {CurrentUserMock} from '../../mocks';

const STORE_DEBOUNCE_MS = 150;

class SupervisionTaskServiceMock {
  search(searchCriteria: SupervisionTaskSearchCriteria): Observable<Page<SupervisionWorkItem>> {
    return Observable.of(new Page<SupervisionWorkItem>());
  }

  changeOwner(ownerId: number, taskIds: Array<number>): Observable<HttpResponse> {
    return Observable.of(new HttpResponse(HttpStatus.OK));
  }

  removeOwner(taskIds: Array<number>): Observable<HttpResponse> {
    return Observable.of(new HttpResponse(HttpStatus.OK));
  }
}

describe('supervision-work-item-store', () => {
  let store: SupervisionWorkItemStore;
  let taskService: SupervisionTaskServiceMock;
  const currentUserMock = CurrentUserMock.create(true, true);

  beforeEach(() => {
    const tb = TestBed.configureTestingModule({
      providers: [
        { provide: SupervisionTaskService, useClass: SupervisionTaskServiceMock},
        SupervisionWorkItemStore,
        {provide: CurrentUser, useValue: currentUserMock},
      ]
    });
    store = tb.get(SupervisionWorkItemStore);
    taskService = tb.get(SupervisionTaskService) as SupervisionTaskServiceMock;
  });

  it('should notify tab change', fakeAsync(() => {
    let result;
    store.tabChange(WorkQueueTab.COMMON);
    store.changes.map(state => state.tab).subscribe(change => result = change);
    tick();
    expect(result).toEqual(WorkQueueTab.COMMON);
  }));

  it('should notify search change', async(() => {
    let result;
    const search = new SupervisionTaskSearchCriteria([], 'testId');
    store.searchChange(search);
    store.changes.map(state => state.search).subscribe(change => result = change);
    expect(result).toEqual(search);
  }));

  it('should notify items change', fakeAsync(() => {
    let result;
    const page = new Page([new SupervisionWorkItem(1), new SupervisionWorkItem(2)]);
    store.pageChange(page);
    store.changes.map(state => state.page).subscribe(change => result = change);
    tick();
    expect(result).toEqual(page);
  }));

  it('should select item', fakeAsync(() => {
    let result;
    const page = initWithItems();
    store.changes.map(state => state.selectedItems).subscribe(change => result = change);
    store.toggleSingle(page.content[0].id, true);
    tick();
    expect(result.length).toEqual(1);
    expect(result[0]).toEqual(page.content[0].id);
    store.toggleSingle(page.content[0].id, false);
    expect(result.length).toEqual(0);
  }));

  it('should select all', fakeAsync(() => {
    let selected;
    let allSelected;
    initWithItems();
    store.changes.map(state => state.selectedItems).subscribe(change => selected = change);
    store.changes.map(state => state.allSelected).subscribe(change => allSelected = change);

    store.toggleAll(true);
    tick();
    expect(selected.length).toEqual(2);
    expect(allSelected).toEqual(true);

    store.toggleAll(false);
    tick();
    expect(selected.length).toEqual(0);
    expect(allSelected).toEqual(false);
  }));

  it('should not show all selected after item is deselected', fakeAsync(() => {
    let selected;
    let allSelected;
    const page = initWithItems();
    store.changes.map(state => state.selectedItems).subscribe(change => selected = change);
    store.changes.map(state => state.allSelected).subscribe(change => allSelected = change);
    store.toggleAll(true);
    tick();

    store.toggleSingle(page.content[0].id, false);
    tick(STORE_DEBOUNCE_MS);
    expect(selected.length).toEqual(1);
    expect(allSelected).toEqual(false);
  }));

  it('should remove selections when search changes', async(() => {
    let selected;
    const page = new Page([new SupervisionWorkItem(1), new SupervisionWorkItem(2)]);
    store.pageChange(page);
    store.changes.map(state => state.selectedItems)
      .subscribe(change => selected = change);
    store.toggleAll(true);

    store.searchChange(new SupervisionTaskSearchCriteria([], 'testId'));
    // A bit ugly setTimeout usage to advance time enough that store emits change event
    setTimeout(() => expect(selected.length).toEqual(0), STORE_DEBOUNCE_MS);
  }));

  it('should change owner for selected', fakeAsync(() => {
    let selected;
    store.changes.map(state => state.selectedItems).subscribe(change => selected = change);
    store.toggleAll(true);
    tick();

    const handlerId = 1;
    spyOn(taskService, 'changeOwner').and.callThrough();
    store.changeHandlerForSelected(handlerId).subscribe();
    tick();
    expect(taskService.changeOwner).toHaveBeenCalledWith(handlerId, selected);
  }));

  it('should remove owner for selected', fakeAsync(() => {
    let selected;
    store.changes.map(state => state.selectedItems).subscribe(change => selected = change);
    store.toggleAll(true);
    tick();

    spyOn(taskService, 'removeOwner').and.callThrough();
    store.removeHandlerFromSelected().subscribe();
    tick();
    expect(taskService.removeOwner).toHaveBeenCalledWith(selected);
  }));

  function initWithItems(): Page<SupervisionWorkItem> {
    const page = new Page([new SupervisionWorkItem(1), new SupervisionWorkItem(2)]);
    store.pageChange(page);
    tick();
    return page;
  }
});


