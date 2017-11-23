import {DistributionEntry} from '../../model/common/distribution-entry';
import {BackendDistributionEntry} from '../backend-model/backend-distribution-entry';
import {Some} from '../../util/option';
import {DistributionType} from '../../model/common/distribution-type';
import {PostalAddress} from '../../model/common/postal-address';

export class DistributionMapper {
  static mapBackendList(entries: Array<BackendDistributionEntry>): Array<DistributionEntry> {
    return (entries)
      ? entries.map(entry => DistributionMapper.mapBackend(entry))
      : [];
  }

  static mapFrontendList(entries: Array<DistributionEntry>): Array<BackendDistributionEntry> {
    return (entries)
      ? entries.map(entry => DistributionMapper.mapFrontend(entry))
      : [];
  }

  static mapBackend(backendEntry: BackendDistributionEntry): DistributionEntry {
    const postalAddress = Some(backendEntry.postalAddress)
      .map(address => new PostalAddress(address.streetAddress, address.postalCode, address.city))
      .orElse(undefined);

    return new DistributionEntry(
      backendEntry.id,
      backendEntry.name,
      Some(backendEntry.distributionType).map(type => DistributionType[type]).orElse(undefined),
      backendEntry.email,
      postalAddress
    );
  }

  static mapFrontend(entry: DistributionEntry): any {
    return (entry) ?
      {
        id: entry.id,
        name: entry.name,
        distributionType: entry.uiType,
        email: entry.email,
        postalAddress: (entry.postalAddress) ?
          { streetAddress: entry.postalAddress.streetAddress,
            postalCode: entry.postalAddress.postalCode,
            city: entry.postalAddress.city } : undefined
      }
      : undefined;
  }
}
