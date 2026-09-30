import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {FieldChange} from '../../../model/history/field-change';
import {StructureMeta} from '../../../model/application/meta/structure-meta';

@Component({
  selector: 'history-fields',
  templateUrl: './history-fields.component.html',
  styleUrls: ['./history-fields.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HistoryFieldsComponent {
  @Input() fieldChanges: FieldChange[] = [];
  @Input() meta: StructureMeta;
}
