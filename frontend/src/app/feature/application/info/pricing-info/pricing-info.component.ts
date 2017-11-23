import {Component, Input, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {EventNature} from '../../../../model/application/event/event-nature';
import {EnumUtil} from '../../../../util/enum.util';

@Component({
  selector: 'pricing-info',
  viewProviders: [],
  templateUrl: './pricing-info.component.html',
  styleUrls: []
})
export class PricingInfoComponent implements OnInit {

  @Input() form: FormGroup;
  @Input() kind: string;

  eventNatures = EnumUtil.enumValues(EventNature).filter(nature => nature !== 'PROMOTION');

  ngOnInit(): void {
  }
}
