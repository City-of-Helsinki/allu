import {BackendChargeBasisEntry} from '@service/backend-model/backend-charge-basis-entry';
import {ChargeBasisEntry} from '@model/application/invoice/charge-basis-entry';

export class ChargeBasisEntryMapper {

  public static mapBackendArray(entries: Array<BackendChargeBasisEntry> = []): Array<ChargeBasisEntry> {
    return entries.map(this.mapBackend);
  }

  public static mapFrontendArray(entries: Array<ChargeBasisEntry>): Array<BackendChargeBasisEntry> {
    return entries.map(this.mapFrontEnd);
  }

  public static mapBackend(backendChargeBasisEntry: BackendChargeBasisEntry): ChargeBasisEntry {
    return new ChargeBasisEntry(
      backendChargeBasisEntry.id,
      backendChargeBasisEntry.type,
      backendChargeBasisEntry.unit,
      backendChargeBasisEntry.quantity,
      backendChargeBasisEntry.text,
      backendChargeBasisEntry.unitPrice,
      backendChargeBasisEntry.netPrice,
      backendChargeBasisEntry.manuallySet,
      backendChargeBasisEntry.tag,
      backendChargeBasisEntry.referredTag,
      backendChargeBasisEntry.explanation,
      backendChargeBasisEntry.locked,
      backendChargeBasisEntry.referrable,
      backendChargeBasisEntry.invoicable,
      backendChargeBasisEntry.invoicingPeriodId
    );
  }

  public static mapFrontEnd(chargeBasisEntry: ChargeBasisEntry): BackendChargeBasisEntry {
    return {
      id: chargeBasisEntry.id,
      type: chargeBasisEntry.type,
      unit: chargeBasisEntry.unit,
      quantity: chargeBasisEntry.quantity,
      text: chargeBasisEntry.text,
      unitPrice: chargeBasisEntry.unitPrice,
      netPrice: chargeBasisEntry.netPrice,
      manuallySet: chargeBasisEntry.manuallySet,
      tag: chargeBasisEntry.tag,
      referredTag: chargeBasisEntry.referredTag,
      explanation: chargeBasisEntry.explanation || [],
      invoicable: chargeBasisEntry.invoicable,
      invoicingPeriodId: chargeBasisEntry.invoicingPeriodId
    };
  }
}
