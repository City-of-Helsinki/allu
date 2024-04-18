import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {UntypedFormArray, UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {Location} from '@model/common/location';

@Component({
  selector: 'location-acceptance',
  templateUrl: './location-acceptance.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LocationAcceptanceComponent implements OnInit, OnDestroy {
  @Input() id = '';
  @Input() formArray: UntypedFormArray;
  @Input() readonly: boolean;
  @Input() oldLocation: Location;
  @Input() newLocation: Location;
  @Input() hideExisting = false;

  @Output() locationChanges: EventEmitter<Location> = new EventEmitter<Location>();

  form: UntypedFormGroup;

  constructor(private fb: UntypedFormBuilder) {}

  ngOnInit(): void {
    this.form = this.fb.group({});
    this.formArray.push(this.form);
  }

  ngOnDestroy(): void {
  }

}
