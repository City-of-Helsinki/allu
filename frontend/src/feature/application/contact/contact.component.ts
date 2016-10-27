import {Component, Input, OnInit, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {StructureMeta} from '../../../model/application/structure-meta';
import {outdoorEventConfig} from '../outdoor-event/outdoor-event-config';
import {ApplicationHub} from '../../../service/application/application-hub';
import {Contact} from '../../../model/application/contact';

@Component({
  selector: 'contact',
  viewProviders: [],
  template: require('./contact.component.html'),
  styles: []
})
export class ContactComponent implements OnInit, AfterViewInit {
  @Input() contactList: Array<Contact>;
  @Input() readonly: boolean;

  meta: StructureMeta;

  constructor(private applicationHub: ApplicationHub) {
    applicationHub.metaData().subscribe(meta => this.metadataLoaded(meta));
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    setTimeout(() => Materialize.updateTextFields(), 10);
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.meta = metadata;
  }
}
