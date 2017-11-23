import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Application} from '../../../model/application/application';
import {ApplicationTag} from '../../../model/application/tag/application-tag';
import {ApplicationTagType, manualTagTypes} from '../../../model/application/tag/application-tag-type';

@Component({
  selector: 'tagbar',
  templateUrl: './tagbar.component.html',
  styleUrls: [
    './tagbar.component.scss'
  ]
})
export class TagBarComponent implements OnInit {
  @Input() application: Application;
  @Input() readonly: boolean;
  @Output() onTagChange = new EventEmitter<Array<ApplicationTag>>();

  tags: Array<ApplicationTag> = [];
  manualTagTypes = manualTagTypes.map(type => ApplicationTagType[type]);

  constructor() {}

  ngOnInit() {
    this.tags = this.application.applicationTags || [];
  }

  canBeRemoved(typeName: string) {
    return this.manualTagTypes.indexOf(typeName) >= 0;
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
