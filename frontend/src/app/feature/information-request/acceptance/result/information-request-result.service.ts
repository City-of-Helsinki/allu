import {Injectable} from '@angular/core';
import * as fromInformationRequest from '@feature/information-request/reducers';
import * as fromInformationRequestResult from '@feature/information-request/reducers/information-request-result-reducer';
import {Store} from '@ngrx/store';
import {InformationRequestResult} from '@feature/information-request/information-request-result';
import {Observable} from 'rxjs/index';
import {CustomerRoleType} from '@model/customer/customer-role-type';
import {ArrayUtil} from '@util/array-util';
import {map, take} from 'rxjs/internal/operators';
import {CustomerWithContacts} from '@model/customer/customer-with-contacts';
import {ObjectUtil} from '@util/object.util';
import {Application} from '@model/application/application';
import set from 'lodash/set';
import {Customer} from '@model/customer/customer';
import {Contact} from '@model/customer/contact';
import {FieldNameMapping} from '@feature/information-request/acceptance/other/application-acceptance-field-mapping';
import {FieldValues} from '@feature/information-request/acceptance/field-select/field-select.component';

@Injectable()
export class InformationRequestResultService {
  constructor(private store: Store<fromInformationRequest.State>) {}

  getResult(requestId?: number): Observable<InformationRequestResult> {
    return this.store.select(fromInformationRequest.getResultData).pipe(
      take(1),
      map(data => this.toResult(data, requestId))
    );
  }

  private toResult(data: fromInformationRequestResult.State, requestId?: number): InformationRequestResult {
    const application = ObjectUtil.clone(data.application);
    application.kindsWithSpecifiers = data.kindsWithSpecifiers;
    this.patchCustomerWithContacts(application, data.customer, data.contacts);
    this.patchOtherInfo(application, data.otherInfo);
    return new InformationRequestResult(requestId, application, data.invoicingCustomer, data.useCustomerForInvoicing);
  }

  private patchCustomerWithContacts(application: Application, customer: Customer, contacts: Contact[]) {
    const customerWithContacts = new CustomerWithContacts(CustomerRoleType.APPLICANT, customer, contacts);
    application.customersWithContacts = ArrayUtil.createOrReplace(
      application.customersWithContacts,
      customerWithContacts,
      cwc => cwc.roleType === CustomerRoleType.APPLICANT
    );
  }

  private patchOtherInfo(application: Application, otherInfo: FieldValues): void {
    if (otherInfo) {
      Object.keys(otherInfo).forEach(fieldName => {
        const valueField = FieldNameMapping[fieldName];
        set(application, valueField, otherInfo[fieldName]);
      });

      if (otherInfo.startTime) {
        application.singleLocation.startTime = otherInfo.startTime;
      }
      if (otherInfo.endTime) {
        application.singleLocation.endTime = otherInfo.endTime;
      }
    }
  }
}
