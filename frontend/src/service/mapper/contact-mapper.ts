import {BackendContact} from '../backend-model/backend-contact';
import {Contact} from '../../model/customer/contact';

export class ContactMapper {

  public static mapBackend(backendContact: BackendContact): Contact {
    return (backendContact) ? new Contact(
      backendContact.id,
      backendContact.customerId,
      backendContact.name,
      backendContact.streetAddress,
      backendContact.postalCode,
      backendContact.city,
      backendContact.email,
      backendContact.phone,
      backendContact.active,
      backendContact.orderer
    ) : undefined;
  }

  public static mapFrontend(contact: Contact): BackendContact {
    return (contact) ?
    {
      id: contact.id,
      customerId: contact.customerId,
      name: contact.name,
      streetAddress: contact.streetAddress,
      postalCode: contact.postalCode,
      city: contact.city,
      email: contact.email,
      phone: contact.phone,
      active: contact.active,
      orderer: contact.orderer
    } : undefined;
  }
}
