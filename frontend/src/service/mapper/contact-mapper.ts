import {BackendContact} from '../backend-model/backend-contact';
import {Contact} from '../../model/application/contact';
import {PersonMapper} from './person-mapper';
import {OrganizationMapper} from './organization-mapper';

export class ContactMapper {

  public static mapBackend(backendContact: BackendContact): Contact {
    return (backendContact) ? new Contact(
      backendContact.id,
      PersonMapper.mapBackend(backendContact.person),
      OrganizationMapper.mapBackend(backendContact.organization)) : undefined;
  }

  public static mapFrontend(contact: Contact): BackendContact {
    return (contact) ?
    {
      id: contact.id,
      person: PersonMapper.mapFrontend(contact.person),
      organization: OrganizationMapper.mapFrontend(contact.organization)
    } : undefined;
  }
}
