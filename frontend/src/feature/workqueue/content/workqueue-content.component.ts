import {Component, OnInit, OnDestroy, Input, Output, EventEmitter} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {ConnectableObservable} from 'rxjs';
import {Subscription} from 'rxjs/Subscription';
import '../../../rxjs-extensions.ts';
import {MdDialogRef, MdDialog} from '@angular/material';

import {Application} from '../../../model/application/application';
import {Sort, Direction} from '../../../model/common/sort';
import {User} from '../../../model/common/user';
import {translations} from '../../../util/translations';
import {MapHub} from '../../../service/map/map-hub';
import {CommentsModalComponent} from '../../application/comment/comments-modal.component';
import {ApplicationTagType} from '../../../model/application/tag/application-tag-type';
import {WorkQueueHub} from '../workqueue-search/workqueue-hub';


@Component({
  selector: 'workqueue-content',
  template: require('./workqueue-content.component.html'),
  styles: [require('./workqueue-content.component.scss')]
})
export class WorkQueueContentComponent implements OnInit, OnDestroy {
  @Input() applications: ConnectableObservable<Array<Application>>;
  @Output() onSortChange = new EventEmitter<Sort>();
  @Output() onSelectChange = new EventEmitter<Array<number>>();
  applicationRows: Array<ApplicationRow>;
  allSelected = false;
  sort = new Sort(undefined, undefined);
  translations = translations;
  hoveredRowIndex = undefined;
  selectedTags: Array<string> = [];

  private applicationSubscription: Subscription;
  private searchSubscription: Subscription;
  private dialogRef: MdDialogRef<CommentsModalComponent>;

  constructor(private router: Router,
              private mapHub: MapHub,
              private dialog: MdDialog,
              private workQueueHub: WorkQueueHub) {}

  ngOnInit(): void {
    this.applicationSubscription = this.applications.connect();
     this.applications
      .map(applications => this.toApplicationRows(applications))
      .subscribe(applicationRows => {
        this.allSelected = false;
        this.applicationRows = applicationRows;
      });

     this.searchSubscription = this.workQueueHub.searchQuery
       .subscribe(query => this.selectedTags = query.tags);
  }

  ngOnDestroy(): void {
    this.applicationSubscription.unsubscribe();
    this.searchSubscription.unsubscribe();
  }

  checkAll() {
    let selection = !this.allSelected;
    this.applicationRows.forEach(row => row.selected = selection);
    this.notifySelection();
  }

  checkSingle(row: ApplicationRow) {
    row.selected = !row.selected;
    this.allSelected = this.applicationRows.every(r => r.selected);
    this.notifySelection();
  }

  sortBy(sort: Sort) {
    this.sort = sort;
    this.onSortChange.emit(this.sort);
  }

  goToSummary(col: number, application: Application): void {
    // undefined and 0 should not trigger navigation
    if (col) {
      this.router.navigate(['applications', application.id, 'summary']);
    }
  }

  showComments(applicationId: number): void {
    this.dialogRef = this.dialog.open(CommentsModalComponent, {disableClose: false, width: '800px'});
    this.dialogRef.componentInstance.applicationId = applicationId;
  }

  districtName(id: number): Observable<string> {
    return id !== undefined ? this.mapHub.districtById(id).map(d => d.name) : Observable.empty();
  }

  tagSelected(tagName: string): boolean {
    return this.selectedTags.indexOf(tagName) >= 0;
  }

  onMouseEnter(index: number): void {
    this.hoveredRowIndex = index;
  }

  onMouseLeave(index: number): void {
    this.hoveredRowIndex = undefined;
  }

  private toApplicationRows(applications: Array<Application>): Array<ApplicationRow> {
    return applications
      .map(application => {
        return {
          selected: false,
          application: application
        };
      });
  }

  private notifySelection() {
    this.onSelectChange.emit(
      this.applicationRows
        .filter(row => row.selected)
        .map(row => row.application.id)
    );
  }
}

interface ApplicationRow {
  selected: boolean;
  application: Application;
}
