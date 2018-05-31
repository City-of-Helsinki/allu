import {Component, Input, OnInit} from '@angular/core';
import {ActionTargetType} from '../allu/actions/action-target-type';

@Component({
  selector: 'history',
  templateUrl: './history.component.html',
  styleUrls: []
})
export class HistoryComponent implements OnInit {
  @Input() targetType: ActionTargetType;

  ngOnInit(): void {
  }
}
