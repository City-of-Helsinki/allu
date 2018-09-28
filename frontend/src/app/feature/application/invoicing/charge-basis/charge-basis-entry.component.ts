import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {ChargeBasisEntry, unitPriceEuro} from '@model/application/invoice/charge-basis-entry';
import {ChargeBasisType} from '@model/application/invoice/charge-basis-type';
import {ChargeBasisUnit} from '@model/application/invoice/charge-basis-unit';

@Component({
  selector: 'charge-basis-entry',
  templateUrl: './charge-basis-entry.component.html',
  styleUrls: ['./charge-basis-entry.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChargeBasisEntryComponent {
  @Input() entry: ChargeBasisEntry;
  @Input() changesAllowed: boolean;

  @Output() onEdit: EventEmitter<ChargeBasisEntry> = new EventEmitter<ChargeBasisEntry>();
  @Output() onRemove: EventEmitter<void> = new EventEmitter<void>();

  editAllowed(entry: ChargeBasisEntry): boolean {
    return !entry.locked && entry.manuallySet && this.changesAllowed;
  }

  showMinimal(entry: ChargeBasisEntry): boolean {
    return entry.type === ChargeBasisType.DISCOUNT
      || entry.unit === ChargeBasisUnit.PERCENT;
  }

  entryValue(entry: ChargeBasisEntry): string {
    let prefix = '';
    let value = unitPriceEuro(entry);

    if (entry.unit === ChargeBasisUnit.PERCENT) {
      value = entry.quantity;
    } else if (entry.type === ChargeBasisType.DISCOUNT) {
      if (value < 0) {
        prefix = '+';
        value = -value;
      } else {
        prefix = '-';
      }
    }
    return prefix + value;
  }
}
