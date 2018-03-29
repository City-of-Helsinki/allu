import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {StoredFilterService} from '../../service/stored-filter/stored-filter.service';
import {StoredFilterType} from '../../model/user/stored-filter-type';
import {UserService} from '../../service/user/user-service';
import {Subject} from 'rxjs/Subject';
import {StoredFilter} from '../../model/user/stored-filter';
import {Observable} from 'rxjs/Observable';
import {NotificationService} from '../../service/notification/notification.service';
import {User} from '../../model/user/user';
import {MatDialog} from '@angular/material';
import {STORED_FILTER_MODAL_CONFIG, StoredFilterModalComponent} from './stored-filter-modal.component';

@Component({
  selector: 'stored-filter',
  templateUrl: './stored-filter.component.html',
  styleUrls: [
    './stored-filter.component.scss'
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class StoredFilterComponent implements OnInit, OnDestroy {

  @Input() type: StoredFilterType;
  @Input() filter: any;

  @Output() filterSelected = new EventEmitter<StoredFilter>();

  availableFilters: Observable<StoredFilter[]>;
  selectedFilter: StoredFilter;

  private currentUser: User;
  private destroy = new Subject<boolean>();

  constructor(
    private storedFilterService: StoredFilterService,
    private userService: UserService,
    private dialog: MatDialog) {}


  ngOnInit(): void {
    if (this.type === undefined) {
      throw new Error('Type is required for stored filter component');
    }

    this.availableFilters = this.loadAvailableFilters();
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  addNewFilter(): void {
    const config = {
      ...STORED_FILTER_MODAL_CONFIG,
      data: {
        filterType: this.type,
        userId: this.currentUser.id,
        filter: this.filter
    }};

    const dialogRef = this.dialog.open<StoredFilterModalComponent>(StoredFilterModalComponent, config);
    this.availableFilters = dialogRef.afterClosed()
      .switchMap(() => this.loadAvailableFilters());
  }

  selectFilter(filter: StoredFilter): void {
    this.selectedFilter = filter;
    this.filterSelected.emit(filter);
  }

  removeFilter(id: number): void {
    this.availableFilters = this.storedFilterService.remove(id)
      .do(() => this.handleRemoval(id))
      .switchMap(() => this.loadAvailableFilters());
  }

  private handleRemoval(id: number): void {
    NotificationService.translateMessage('storedFilter.action.remove');
    if (this.selectedFilter && this.selectedFilter.id === id) {
      this.selectedFilter = undefined;
    }
  }

  private loadAvailableFilters(): Observable<StoredFilter[]> {
    return this.loadCurrentUser()
      .takeUntil(this.destroy)
      .switchMap(user => this.storedFilterService.findByUserAndType(user.id, this.type))
      .do(filters => this.useAvailableDefaultFilter(filters))
      .catch(err => NotificationService.errorCatch(err, []));
  }

  private loadCurrentUser(): Observable<User> {
    return this.userService.getCurrentUser()
      .do(user => this.currentUser = user);
  }

  private useAvailableDefaultFilter(filters: StoredFilter[]): void {
    const filter = filters.find(f => f.defaultFilter);
    if (filter) {
      this.selectFilter(filter);
    }
  }
}
