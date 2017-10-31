import {BackendChargeBasisEntry} from '../backend-model/backend-charge-basis-entry';
import {ChargeBasisEntry} from '../../model/application/invoice/charge-basis-entry';
import {ChargeBasisUnit} from '../../model/application/invoice/charge-basis-unit';
import {Some} from '../../util/option';
export class ChargeBasisEntryMapper {

  public static mapBackendArray(entries: Array<BackendChargeBasisEntry> = []): Array<ChargeBasisEntry> {
    return entries.map(this.mapBackend);
  }

  public static mapFrontendArray(entries: Array<ChargeBasisEntry>): Array<BackendChargeBasisEntry> {
    return entries.map(this.mapFrontEnd);
  }

  public static mapBackend(backendChargeBasisEntry: BackendChargeBasisEntry): ChargeBasisEntry {
    return new ChargeBasisEntry(
      Some(backendChargeBasisEntry.unit).map(unit => ChargeBasisUnit[unit]).orElse(ChargeBasisUnit.PIECE),
      backendChargeBasisEntry.quantity,
      backendChargeBasisEntry.text,
      backendChargeBasisEntry.unitPrice,
      backendChargeBasisEntry.netPrice,
      backendChargeBasisEntry.manuallySet,
      backendChargeBasisEntry.tag,
      backendChargeBasisEntry.referredTag
    );
  }

  public static mapFrontEnd(chargeBasisEntry: ChargeBasisEntry): BackendChargeBasisEntry {
    return {
      unit: Some(chargeBasisEntry.unit).map(unit => ChargeBasisUnit[unit]).orElse(ChargeBasisUnit[ChargeBasisUnit.PIECE]),
      quantity: chargeBasisEntry.quantity,
      text: chargeBasisEntry.text,
      unitPrice: chargeBasisEntry.unitPrice,
      netPrice: chargeBasisEntry.netPrice,
      manuallySet: chargeBasisEntry.manuallySet,
      tag: chargeBasisEntry.tag,
      referredTag: chargeBasisEntry.referredTag
    };
  }
}
