import {PostalAddress} from '../../model/common/postal-address';
import {BackendOrganization} from '../backend-model/backend-organization';
import {Organization} from '../../model/common/organization';

export class OrganizationMapper {

  public static mapBackend(backendOrganization: BackendOrganization): Organization {
    if (!backendOrganization) {
      return undefined;
    }
    let postalAddress = new PostalAddress(backendOrganization.streetAddress, backendOrganization.postalCode, backendOrganization.city);
    return new Organization(
      backendOrganization.id,
      backendOrganization.name,
      backendOrganization.businessId,
      postalAddress,
      backendOrganization.email,
      backendOrganization.phone);
  }

  public static mapFrontend(organization: Organization): BackendOrganization {
    return (organization) ?
    {
      id: undefined,
      name: organization.name,
      businessId: organization.businessId,
      streetAddress: organization.postalAddress.streetAddress,
      postalCode: organization.postalAddress.postalCode,
      city: organization.postalAddress.city,
      email: organization.email,
      phone: organization.phone
    } : undefined;
  }
}
