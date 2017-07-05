import {BackendDefaultRecipient} from '../backend-model/backend-default-recipient';
import {DefaultRecipient} from '../../model/common/default-recipient';
export class DefaultRecipientMapper {
  public static mapBackend(recipient: BackendDefaultRecipient): DefaultRecipient {
    return new DefaultRecipient(recipient.id, recipient.email, recipient.applicationType);
  }

  public static mapFrontend(recipient: DefaultRecipient): BackendDefaultRecipient {
    return { ...recipient };
  }
}
