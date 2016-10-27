import {Component, Input, OnInit, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {Applicant} from '../../../model/application/applicant';
import {StructureMeta} from '../../../model/application/structure-meta';
import {outdoorEventConfig, applicantIdSelection, applicantNameSelection} from '../outdoor-event/outdoor-event-config';
import {ApplicationHub} from '../../../service/application/application-hub';

@Component({
  selector: 'applicant',
  viewProviders: [],
  template: require('./applicant.component.html'),
  styles: []
})
export class ApplicantComponent implements OnInit, AfterViewInit {
  @Input() applicant: Applicant;
  @Input() readonly: boolean;

  meta: StructureMeta;
  countries: Array<any>;
  applicantType: any;
  applicantText: any;
  applicantNameSelection: string;
  applicantIdSelection: string;

  constructor(private applicationHub: ApplicationHub) {
    applicationHub.metaData().subscribe(meta => this.metadataLoaded(meta));
  }

  ngOnInit(): void {
    this.applicantNameSelection = applicantNameSelection(this.applicant.type);
    this.applicantIdSelection = applicantIdSelection(this.applicant.type);

    this.countries = outdoorEventConfig.countries;
    this.applicantType = outdoorEventConfig.applicantType;
    this.applicantText = outdoorEventConfig.applicantText;
  }

  ngAfterViewInit(): void {
    setTimeout(() => Materialize.updateTextFields(), 10);
  }

  applicantTypeSelection(value: string) {
    this.applicantNameSelection = this.applicantText[value].name;
    this.applicantIdSelection = this.applicantText[value].id;
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.meta = metadata;
    this.applicantText = {
      'DEFAULT': {
        name: 'Hakijan nimi',
        id: this.meta.getUiName('applicant.businessId')},
      'COMPANY': {
        name: this.meta.getUiName('applicant.companyName'),
        id: this.meta.getUiName('applicant.businessId')},
      'ASSOCIATION': {
        name: this.meta.getUiName('applicant.organizationName'),
        id: this.meta.getUiName('applicant.businessId')},
      'PERSON': {
        name: this.meta.getUiName('applicant.personName'),
        id: this.meta.getUiName('applicant.ssn')}
    };
    this.applicantNameSelection = applicantNameSelection(this.applicant.type);
    this.applicantIdSelection = applicantIdSelection(this.applicant.type);
  }
}
