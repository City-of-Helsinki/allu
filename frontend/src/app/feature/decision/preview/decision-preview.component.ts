import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs/index';
import {Store} from '@ngrx/store';
import * as fromDecision from '../reducers';
import {Load} from '@feature/decision/actions/decision-actions';

@Component({
  selector: 'decision-preview',
  templateUrl: './decision-preview.component.html',
  styleUrls: ['./decision-preview.component.scss']
})
export class DecisionPreviewComponent {
  constructor() {}
}
