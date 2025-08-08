import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {StoredFilterType} from '@model/user/stored-filter-type';
import {UserService} from '@service/user/user-service';
import {Observable, Subject} from 'rxjs';
import {StoredFilter} from '@model/user/stored-filter';
import {NotificationService} from '../notification/notification.service';
import {User} from '@model/user/user';
import {MatDialog} from '@angular/material/dialog';
import {STORED_FILTER_MODAL_CONFIG, StoredFilterModalComponent} from './stored-filter-modal.component';
import {StoredFilterStore} from '@service/stored-filter/stored-filter-store';
import {catchError, filter, switchMap, takeUntil} from 'rxjs/internal/operators';

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
    private dialog: MatDialog,
    private notification: NotificationService) {}

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
    dialogRef.afterClosed().pipe(
      filter(result => !!result),
      switchMap(added => this.store.save(added))
    ).subscribe(
      () => this.notification.translateSuccess('storedFilter.action.save'),
      err => this.notification.errorInfo(err));
  }

  selectFilter(storedFilter: StoredFilter): void {
    if (this.type === StoredFilterType.MAP) {
      this.store.currentMapFilterChange(storedFilter);
    } else {
      this.store.currentChange(storedFilter);
    }
  }

  removeFilter(id: number): void {
    this.store.remove(id)
      .subscribe(
        () => this.notification.translateSuccess('storedFilter.action.remove'),
        (err) => this.notification.translateError(err));
  }

  private loadCurrentUser(): Observable<User> {
    return this.userService.getCurrentUser().pipe(
      takeUntil(this.destroy),
      catchError(err => this.notification.errorCatch(err, undefined))
    );
  }
}
