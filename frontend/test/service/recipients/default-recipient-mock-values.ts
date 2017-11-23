import {DefaultRecipient} from '../../../src/app/model/common/default-recipient';
import {ApplicationType} from '../../../src/app/model/application/type/application-type';

export const RECIPIENT_ONE = new DefaultRecipient(1, 'first@test.fi', ApplicationType[ApplicationType.EVENT]);
export const RECIPIENT_TWO = new DefaultRecipient(2, 'second@test.fi', ApplicationType[ApplicationType.NOTE]);
export const RECIPIENT_NEW = new DefaultRecipient(undefined, 'new@test.fi', ApplicationType[ApplicationType.AREA_RENTAL]);
export const RECIPIENTS_ALL = [RECIPIENT_ONE, RECIPIENT_TWO];
