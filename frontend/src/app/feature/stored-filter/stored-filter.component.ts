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
import {StoredFilterStore} from '../../service/stored-filter/stored-filter-store';

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
  @Input() selectedFilter: StoredFilter;
  @Input() availableFilters: StoredFilter[];
  @Input() classNames: string[];

  private currentUser: User;
  private destroy = new Subject<boolean>();

  constructor(
    private store: StoredFilterStore,
    private userService: UserService,
    private dialog: MatDialog) {}

  ngOnInit(): void {
    if (this.type === undefined) {
      throw new Error('Type is required for stored filter component');
    }

    this.loadCurrentUser().subscribe(user => this.currentUser = user);
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
    dialogRef.afterClosed()
      .switchMap(added => this.store.save(added))
      .subscribe(
        () => NotificationService.translateMessage('storedFilter.action.save'),
        err => NotificationService.error(err));
  }

  selectFilter(filter: StoredFilter): void {
    this.store.currentChange(filter);
  }

  removeFilter(id: number): void {
    this.store.remove(id)
      .subscribe(
        () => NotificationService.translateMessage('storedFilter.action.remove'),
        (err) => NotificationService.translateError(err));
  }

  private loadCurrentUser(): Observable<User> {
    return this.userService.getCurrentUser()
      .takeUntil(this.destroy)
      .catch(err => NotificationService.errorCatch(err, undefined));
  }
}
