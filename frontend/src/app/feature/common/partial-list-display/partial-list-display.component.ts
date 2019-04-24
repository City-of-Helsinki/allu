import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {BehaviorSubject, combineLatest} from 'rxjs';
import {Observable} from 'rxjs/internal/Observable';
import {map} from 'rxjs/operators';

interface DisplayedContent {
  items: string[];
  restCount: number;
}

@Component({
  selector: 'partial-list-display',
  templateUrl: './partial-list-display.component.html',
  styleUrls: [
    './partial-list-display.component.scss'
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartialListDisplayComponent implements OnInit {

  displayedContent$: Observable<DisplayedContent>;

  private _items: BehaviorSubject<string[]> = new BehaviorSubject<string[]>([]);
  private _showCount: BehaviorSubject<number> = new BehaviorSubject<number>(1);

  ngOnInit(): void {
    this.displayedContent$ = combineLatest(
      this._items,
      this._showCount
    ).pipe(
      map(([items, showCount]) => this.createDisplayedContent(items, showCount))
    );
  }

  @Input() set items(items: string[]) {
    this._items.next(items || []);
  }

  @Input() set show(count: number) {
    this._showCount.next(count);
  }

  private createDisplayedContent(items: string[] = [], showCount: number = 0): DisplayedContent {
    if (items.length > showCount) {
      return {
        items: items.slice(0, showCount),
        restCount: items.length - showCount
      };
    } else {
      return {
        items: items,
        restCount: 0
      };
    }
  }
}
