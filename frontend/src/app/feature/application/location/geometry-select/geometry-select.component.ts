import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {UntypedFormControl} from '@angular/forms';
import {Subject} from 'rxjs';
import {Application} from '@model/application/application';
import {MatLegacyOption as MatOption} from '@angular/material/legacy-core';
import {debounceTime, takeUntil} from 'rxjs/internal/operators';
import {GeometryCollection} from 'geojson';
import {ArrayUtil} from '@util/array-util';
import {NumberUtil} from '@util/number.util';

interface GeometryEntry {
  name: string;
  geometry: GeometryCollection;
}

@Component({
  selector: 'geometry-select',
  templateUrl: './geometry-select.component.html',
  styleUrls: ['./geometry-select.component.scss']
})
export class GeometrySelectComponent implements OnInit, OnDestroy {
  matchingGeometries: GeometryEntry[] = [];

  @Output() searchChange = new EventEmitter<string>(true);
  @Output() selectedChange = new EventEmitter<GeometryCollection>(true);

  searchControl = new UntypedFormControl();

  private destroy = new Subject<boolean>();

  constructor() {
  }

  ngOnInit(): void {
    this.searchControl.valueChanges.pipe(
      takeUntil(this.destroy),
      debounceTime(300)
    ).subscribe(term => this.searchChange.emit(term));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  @Input() set matchingApplications(applications: Application[]) {
    if (applications) {
      this.matchingGeometries = ArrayUtil
        .flatten(applications.map(app => this.toGeometryEntries(app)))
        .sort(ArrayUtil.naturalSort(item => item.name));
    } else {
      this.matchingGeometries = [];
    }
  }

  add(option: MatOption): void {
    const geometryEntry = option.value;
    this.selectedChange.emit(geometryEntry.geometry);
    this.searchControl.reset();
  }

  private toGeometryEntries(application: Application): GeometryEntry[] {
    return application.locations.map((loc, index, arr) => {
      const name = NumberUtil.isDefined(loc.locationKey) && arr.length > 1 // Add only when we have multiple locations with key
        ? `${application.applicationId}/${loc.locationKey}`
        : application.applicationId;

      return {
        name,
        geometry: loc.geometry
      };
    });
  }
}
