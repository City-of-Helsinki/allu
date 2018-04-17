import {AfterViewInit, Component, EventEmitter, Input, OnDestroy, Output, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {Application} from '../../../model/application/application';
import {Sort} from '../../../model/common/sort';
import {MatPaginator, MatSort} from '@angular/material';
import {Observable} from 'rxjs/Observable';
import {CityDistrictService} from '../../../service/map/city-district.service';
import {PageRequest} from '../../../model/common/page-request';
import {Subject} from 'rxjs/Subject';
import {Page} from '../../../model/common/page';

export interface SearchChange {
  sort?: Sort;
  pageRequest: PageRequest;
}

@Component({
  selector: 'project-application-list',
  templateUrl: './project-application-list.component.html',
  styleUrls: []
})
export class ProjectApplicationListComponent implements AfterViewInit, OnDestroy {

  @Input() loading: boolean;
  @Input() page: Page<Application>;

  @Output() applicationAdd = new EventEmitter<number>();
  @Output() applicationRemove = new EventEmitter<number>();
  @Output() searchChange = new EventEmitter<SearchChange>(true);

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns = [
    'controls',
    'owner.realName', 'applicationId', 'type', 'status',
    'customers.applicant.customer.name', 'locations.cityDistrictId',
    'creationTime', 'startTime'
  ];

  private destroy = new Subject<boolean>();

  constructor(private router: Router,
              private cityDistrictService: CityDistrictService) {}

  ngAfterViewInit(): void {
    this.searchParameterChanges();
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  districtName(id: number): Observable<string> {
    return this.cityDistrictService.name(id);
  }

  private searchParameterChanges(): void {
    const displayChanges = [
      this.sort.sortChange.startWith({active: this.sort.active, direction: this.sort.direction}),
      this.paginator.page.startWith({pageIndex: this.paginator.pageIndex, pageSize: this.paginator.pageSize})
    ];

    Observable.combineLatest(displayChanges)
      .map(([sort, page]) => ({
        sort: Sort.fromMatSort(sort),
        pageRequest: new PageRequest(page.pageIndex, page.pageSize)
    })).subscribe(search => this.searchChange.emit(search));
  }
}
