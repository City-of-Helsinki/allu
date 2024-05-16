import {AfterViewInit, Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Application} from '@model/application/application';
import {PublicityType} from '@model/application/publicity-type';
import {EnumUtil} from '@util/enum.util';
import {DistributionEntry} from '@model/common/distribution-entry';
import {Observable, Subject, Subscription} from 'rxjs';
import {distributionChangeAllowed} from '@model/application/application-status';
import {DistributionListComponent} from '@feature/application/distribution/distribution-list/distribution-list.component';
import {map, takeUntil} from 'rxjs/operators';

@Component({
  selector: 'distribution',
  templateUrl: './distribution.component.html',
  styleUrls: []
})
export class DistributionComponent implements OnInit, AfterViewInit, OnDestroy {

  @Input() form: UntypedFormGroup;
  @Input() application: Application;
  @Input() distributionList: DistributionEntry[];
  @Input() readonly: boolean;

  @Output() distributionChange: EventEmitter<DistributionEntry[]> = new EventEmitter<DistributionEntry[]>();

  @ViewChild(DistributionListComponent) distributionListComponent: DistributionListComponent;

  communicationForm: UntypedFormGroup;
  publicityTypes = EnumUtil.enumValues(PublicityType);
  distributionChangeAllowed = false;

  private destroy: Subject<boolean> = new Subject<boolean>();

  constructor(private fb: UntypedFormBuilder) {}

  ngOnInit(): void {
    this.communicationForm = this.fb.group({
      publicityType: [this.application.decisionPublicityType || PublicityType[PublicityType.PUBLIC], Validators.required],
      distributionValid: [true, Validators.requiredTrue]
    });
    this.form.addControl('communication', this.communicationForm);

    if (this.readonly) {
      this.communicationForm.disable();
    }

    this.distributionChangeAllowed = distributionChangeAllowed(this.application.status);
  }

  ngAfterViewInit(): void {
    this.distributionListComponent.statusChanges.pipe(
      takeUntil(this.destroy),
      map(status => status === 'VALID')
    ).subscribe(valid => this.communicationForm.get('distributionValid').patchValue(valid));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  savePending(): void {
    this.distributionListComponent.saveAll();
  }
}
