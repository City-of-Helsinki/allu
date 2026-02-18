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
import {FieldValues} from '@feature/information-request/acceptance/field-select/field-select.component';
import {TimeUtil} from '@util/time.util';

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
    application.locations = data.locations;
    application.startTime = TimeUtil.minimum(... data.locations.map(l => l.startTime));
    application.endTime = TimeUtil.maximum(... data.locations.map(l => l.endTime));
    this.patchCustomerWithContacts(application, CustomerRoleType.APPLICANT, data.applicant, data.contacts);
    this.patchCustomerWithContacts(application, CustomerRoleType.REPRESENTATIVE, data.representative, data.contacts);
    this.patchCustomerWithContacts(application, CustomerRoleType.PROPERTY_DEVELOPER, data.propertyDeveloper, data.contacts);
    this.patchCustomerWithContacts(application, CustomerRoleType.CONTRACTOR, data.contractor, data.contacts);

    this.patchOtherInfo(application, data.otherInfo);
    return new InformationRequestResult(requestId, application, data.invoicingCustomer, data.useCustomerForInvoicing);
  }

  private patchCustomerWithContacts(application: Application, role: CustomerRoleType, customer: Customer, contacts: Contact[] = []) {
    if (customer) {
      const customerContacts = contacts.filter(c => c.customerId === customer.id);
      const customerWithContacts = new CustomerWithContacts(role, customer, customerContacts);
      application.customersWithContacts = ArrayUtil.createOrReplace(
        application.customersWithContacts,
        customerWithContacts,
        cwc => cwc.roleType === role
      );
    } else if (customer === null) {
      // Customer explicitly set to null means removal
      application.customersWithContacts = application.customersWithContacts.filter(
        cwc => cwc.roleType !== role
      );
    }
  }

  private patchOtherInfo(application: Application, otherInfo: FieldValues): void {
    if (otherInfo) {
      Object.keys(otherInfo).forEach(fieldName => {
        set(application, fieldName, otherInfo[fieldName]);
      });
    }
  }
}
