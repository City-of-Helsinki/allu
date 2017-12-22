import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';

import {MapHub} from '../../service/map/map-hub';
import {PostalAddress} from '../../model/common/postal-address';
import {Observable} from 'rxjs/Observable';
import {NotificationService} from '../../service/notification/notification.service';
import {ArrayUtil} from '../../util/array-util';
import {StringUtil} from '../../util/string.util';
import {EnumUtil} from '../../util/enum.util';
import {ApplicationStatus, searchable} from '../../model/application/application-status';
import {MapSearchFilter} from '../../service/map-search-filter';
import {Subject} from 'rxjs/Subject';

enum BarType {
  SIMPLE,
  BAR,
  ADVANCED
}

@Component({
  selector: 'searchbar',
  templateUrl: './searchbar.component.html',
  styleUrls: ['./searchbar.component.scss']
})
export class SearchbarComponent implements OnInit, OnDestroy {
  @Input() datesRequired = false;
  @Input() barType: string = BarType[BarType.BAR];

  @Output() onShowAdvanced = new EventEmitter<boolean>();

  searchForm: FormGroup;
  addressControl: FormControl;
  matchingAddresses: Observable<Array<PostalAddress>>;
  statusTypes = searchable.map(status => ApplicationStatus[status]);

  private destroy = new Subject<boolean>();

  constructor(private fb: FormBuilder, private mapHub: MapHub) {
    this.addressControl = this.fb.control('');
    this.searchForm = this.fb.group({
      address: this.addressControl,
      startDate: this.datesRequired ? [undefined, Validators.required] : undefined,
      endDate: this.datesRequired ? [undefined, Validators.required] : undefined,
      statusTypes: [[]]
    });
  }

  ngOnInit(): void {
    this.searchForm.patchValue(this.mapHub.searchFilterSnapshot());

    this.mapHub.coordinates()
      .takeUntil(this.destroy)
      .filter(coords => !coords.isDefined())
      .subscribe(coords => NotificationService.message('Osoitetta ei löytynyt', 4000));

    this.searchForm.valueChanges
      .takeUntil(this.destroy)
      .subscribe(form => this.notifySearchUpdated(form));

    this.matchingAddresses = this.addressControl.valueChanges
      .debounceTime(300)
      .filter(searchTerm => !!searchTerm && searchTerm.length >= 3)
      .switchMap(searchTerm => this.mapHub.findMatchingAddresses(searchTerm))
      .map(matching => matching.sort(ArrayUtil.naturalSort((address: PostalAddress) => address.uiStreetAddress)));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  public notifySearchUpdated(filter: MapSearchFilter): void {
    this.mapHub.addSearchFilter(filter);
  }

  public addressSelected(streetAddress: string) {
    this.mapHub.coordinateSearch(StringUtil.capitalize(streetAddress));
    this.searchForm.patchValue({address: streetAddress});
  }

  public showMore() {
    this.onShowAdvanced.emit(true);
  }
}
