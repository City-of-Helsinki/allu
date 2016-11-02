import {Component, Input, OnInit, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {StructureMeta} from '../../../model/application/structure-meta';
import {outdoorEventConfig} from '../outdoor-event/outdoor-event-config';
import {ApplicationHub} from '../../../service/application/application-hub';
import {MapHub} from '../../../service/map-hub';
import {SquareSection} from '../../../model/common/square-section';
import {Location} from '../../../model/common/location';

@Component({
  selector: 'location-details',
  viewProviders: [],
  template: require('./location-details.component.html'),
  styles: []
})
export class LocationDetailsComponent implements OnInit, AfterViewInit {
  @Input() applicationId: number;
  @Input() location: Location;
  @Input() readonly: boolean;

  meta: StructureMeta;
  squareSection: SquareSection;

  constructor(private applicationHub: ApplicationHub, private mapHub: MapHub) {
    applicationHub.metaData().subscribe(meta => this.metadataLoaded(meta));
  }

  ngOnInit(): void {
    this.mapHub.squareAndSection(this.location.squareSectionId).subscribe(ssOpt => {
      this.squareSection = ssOpt.orElse(new SquareSection());
    });
  }

  ngAfterViewInit(): void {
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.meta = metadata;
  }
}
