import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {Location} from '@model/common/location';

@Component({
  selector: 'location-acceptance',
  templateUrl: './location-acceptance.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LocationAcceptanceComponent implements OnInit, OnDestroy {
  @Input() id = '';
  @Input() formArray: FormArray;
  @Input() readonly: boolean;
  @Input() oldLocation: Location;
  @Input() newLocation: Location;
  @Input() hideExisting = false;

  @Output() locationChanges: EventEmitter<Location> = new EventEmitter<Location>();

  form: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.form = this.fb.group({});
    this.formArray.push(this.form);
  }

  ngOnDestroy(): void {
  }

}
