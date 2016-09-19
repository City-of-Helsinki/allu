import {Injectable, OnInit} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';
import {Decision} from '../../model/decision/Decision';
import '../../rxjs-extensions.ts';

@Injectable()
export class DecisionHub {
  private decisions$: Subject<Array<Decision>> = new Subject<Array<Decision>>();
  private generate$: Subject<number> = new Subject<number>();
  private fetch$: Subject<number> = new Subject<number>();

  public decisions = () => this.decisions$.asObservable();
  public addDecisions = (decisions: Array<Decision>) => this.decisions$.next(decisions);

  public generateRequest = () => this.generate$.asObservable();
  public generate = (applicationId) => this.generate$.next(applicationId);

  public fetchRequest = () => this.fetch$.asObservable();
  public fetch = (applicationId) => this.fetch$.next(applicationId);
}
