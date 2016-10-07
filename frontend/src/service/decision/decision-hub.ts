import {Injectable, OnInit} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {Decision} from '../../model/decision/Decision';
import {DecisionService} from './decision.service';

@Injectable()
export class DecisionHub {
  constructor(private decisionService: DecisionService) {
  }

  /**
   * Asks decision service to generate decision pdf for given application.
   * Returns observable which contains generated pdf eventually
   */
  public generate = (applicationId: number) => this.decisionService.generate(applicationId);

  /**
   * Asks decision service to fetch decision pdf for given application.
   * Returns observable which contains pdf eventually
   */
  public fetch = (applicationId: number) => this.decisionService.fetch(applicationId);
}
