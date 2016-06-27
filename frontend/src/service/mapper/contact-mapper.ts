import {BackendContact} from '../backend-model/backend-contact';
import {Contact} from '../../model/application/contact';
import {PersonMapper} from './person-mapper';
import {OrganizationMapper} from './organization-mapper';

export class ContactMapper {

  public static mapBackend(backendContact: BackendContact): Contact {
    return (backendContact) ? new Contact(
      backendContact.id,
      backendContact.organizationId,
      backendContact.name,
      backendContact.streetAddress,
      backendContact.postalCode,
      backendContact.city,
      backendContact.email,
      backendContact.phone) : undefined;
  }

  public static mapFrontend(contact: Contact): BackendContact {
    return (contact) ?
    {
      id: contact.id,
      organizationId: contact.organizationId,
      name: contact.name,
      streetAddress: contact.streetAddress,
      postalCode: contact.postalCode,
      city: contact.city,
      email: contact.email,
      phone: contact.phone
    } : undefined;
  }
}
