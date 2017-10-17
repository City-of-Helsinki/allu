import {fakeAsync, TestBed, tick} from '@angular/core/testing';
import {SupervisionTaskSearchCriteria} from '../../../src/model/application/supervision/supervision-task-search-criteria';
import {Observable} from 'rxjs/Observable';
import {SupervisionWorkItem} from '../../../src/model/application/supervision/supervision-work-item';
import {HttpResponse, HttpStatus} from '../../../src/util/http-response';
import {SupervisionWorkItemStore} from '../../../src/feature/supervision-workqueue/supervision-work-item-store';
import {SupervisionTaskService} from '../../../src/service/supervision/supervision-task.service';
import {WorkQueueTab} from '../../../src/feature/workqueue/workqueue-tab';

class SupervisionTaskServiceMock {
  search(searchCriteria: SupervisionTaskSearchCriteria): Observable<Array<SupervisionWorkItem>> {
    return Observable.of([]);
  }

  changeHandler(handlerId: number, taskIds: Array<number>): Observable<HttpResponse> {
    return Observable.of(new HttpResponse(HttpStatus.OK));
  }

  removeHandler(taskIds: Array<number>): Observable<HttpResponse> {
    return Observable.of(new HttpResponse(HttpStatus.OK));
  }
}

describe('supervision-work-item-store', () => {
  let store: SupervisionWorkItemStore;
  let taskService: SupervisionTaskServiceMock;

  beforeEach(() => {
    const tb = TestBed.configureTestingModule({
      providers: [
        { provide: SupervisionTaskService, useClass: SupervisionTaskServiceMock},
        SupervisionWorkItemStore
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

  it('should notify search change', fakeAsync(() => {
    let result;
    const search = new SupervisionTaskSearchCriteria([], 'testId');
    store.searchChange(search);
    store.changes.map(state => state.search).subscribe(change => result = change);
    tick();
    expect(result).toEqual(search);
  }));

  it('should notify items change', fakeAsync(() => {
    let result;
    const items = [new SupervisionWorkItem(1), new SupervisionWorkItem(2)];
    store.itemsChange(items);
    store.changes.map(state => state.items).subscribe(change => result = change);
    tick();
    expect(result).toEqual(items);
  }));

  it('should select item', fakeAsync(() => {
    let result;
    const items = initWithItems();
    store.changes.map(state => state.selectedItems).subscribe(change => result = change);
    store.toggleSingle(items[0].id, true);
    tick();
    expect(result.length).toEqual(1);
    expect(result[0]).toEqual(items[0].id);
    store.toggleSingle(items[0].id, false);
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
    const items = initWithItems();
    store.changes.map(state => state.selectedItems).subscribe(change => selected = change);
    store.changes.map(state => state.allSelected).subscribe(change => allSelected = change);
    store.toggleAll(true);
    tick();

    store.toggleSingle(items[0].id, false);
    tick();
    expect(selected.length).toEqual(1);
    expect(allSelected).toEqual(false);
  }));

  it('should remove selections when search changes', fakeAsync(() => {
    let selected;
    store.changes.map(state => state.selectedItems).subscribe(change => selected = change);
    store.toggleAll(true);
    tick();

    store.searchChange(new SupervisionTaskSearchCriteria([], 'testId'));
    tick();
    expect(selected.length).toEqual(0);
  }));

  it('should change handler for selected', fakeAsync(() => {
    let selected;
    store.changes.map(state => state.selectedItems).subscribe(change => selected = change);
    store.toggleAll(true);
    tick();

    const handlerId = 1;
    spyOn(taskService, 'changeHandler').and.callThrough();
    store.changeHandlerForSelected(handlerId).subscribe();
    tick();
    expect(taskService.changeHandler).toHaveBeenCalledWith(handlerId, selected);
  }));

  it('should remove handler for selected', fakeAsync(() => {
    let selected;
    store.changes.map(state => state.selectedItems).subscribe(change => selected = change);
    store.toggleAll(true);
    tick();

    spyOn(taskService, 'removeHandler').and.callThrough();
    store.removeHandlerFromSelected().subscribe();
    tick();
    expect(taskService.removeHandler).toHaveBeenCalledWith(selected);
  }));

  function initWithItems(): Array<SupervisionWorkItem> {
    const items = [new SupervisionWorkItem(1), new SupervisionWorkItem(2)];
    store.itemsChange(items);
    tick();
    return items;
  }
});


