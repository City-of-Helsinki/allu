import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Application} from '../../../model/application/application';
import {ApplicationTag} from '../../../model/application/tag/application-tag';
import {ApplicationTagType} from '../../../model/application/tag/application-tag-type';

@Component({
  selector: 'tagbar',
  template: require('./tagbar.component.html'),
  styles: [
    require('./tagbar.component.scss')
  ]
})
export class TagBarComponent implements OnInit {
  @Input() application: Application;
  @Input() readonly: boolean;
  @Output() onTagChange = new EventEmitter<Array<ApplicationTag>>();

  tags: Array<ApplicationTag> = [];
  tagTypes = [
    ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED,
    ApplicationTagType.STATEMENT_REQUESTED,
    ApplicationTagType.DEPOSIT_REQUESTED,
    ApplicationTagType.DEPOSIT_PAID,
    ApplicationTagType.WAITING,
    ApplicationTagType.COMPENSATION_CLARIFICATION,
    ApplicationTagType.PAYMENT_BASIS_CORRECTION,
    ApplicationTagType.OPERATIONAL_CONDITION_REPORTED,
    ApplicationTagType.OPERATIONAL_CONDITION_ACCEPTED,
    ApplicationTagType.OPERATIONAL_CONDITION_REJECTED,
    ApplicationTagType.WORK_READY_REPORTED,
    ApplicationTagType.WORK_READY_ACCEPTED,
    ApplicationTagType.WORK_READY_REJECTED
  ].map(type => ApplicationTagType[type]);

  constructor() {}

  ngOnInit() {
    this.tags = this.application.applicationTags || [];
  }

  remove(index: number): void {
    this.tags.splice(index, 1);
    this.onTagChange.emit(this.tags);
  }

  add(type: string): void {
    if (!this.tags.some(tag => tag.type === type)) {
      this.tags.push(new ApplicationTag(type, undefined, new Date()));
      this.onTagChange.emit(this.tags);
    }
  }
}
