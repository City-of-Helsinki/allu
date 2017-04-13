import {BackendContact} from '../backend-model/backend-contact';
import {Contact} from '../../model/application/contact';

export class ContactMapper {

  public static mapBackend(backendContact: BackendContact): Contact {
    return (backendContact) ? new Contact(
      backendContact.id,
      backendContact.applicantId,
      backendContact.name,
      backendContact.streetAddress,
      backendContact.postalCode,
      backendContact.city,
      backendContact.email,
      backendContact.phone,
      backendContact.active
    ) : undefined;
  }

  public static mapFrontend(contact: Contact): BackendContact {
    return (contact) ?
    {
      id: contact.id,
      applicantId: contact.applicantId,
      name: contact.name,
      streetAddress: contact.streetAddress,
      postalCode: contact.postalCode,
      city: contact.city,
      email: contact.email,
      phone: contact.phone,
      active: contact.active
    } : undefined;
  }
}
