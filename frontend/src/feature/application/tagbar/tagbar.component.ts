import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {Application} from '../../../model/application/application';
import {ApplicationTag} from '../../../model/application/tag/application-tag';
import {EnumUtil} from '../../../util/enum.util';
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
  tagTypes = EnumUtil.enumValues(ApplicationTagType);

  constructor() {}

  ngOnInit() {
    this.tags = this.application.applicationTags || [];
  }

  remove(index: number): void {
    this.tags.splice(index, 1);
    this.onTagChange.emit(this.tags);
  }

  add(type: string): void {
    this.tags.push(new ApplicationTag(type, undefined, new Date()));
    this.onTagChange.emit(this.tags);
  }
}
