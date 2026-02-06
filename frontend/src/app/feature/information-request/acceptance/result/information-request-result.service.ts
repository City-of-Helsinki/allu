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
    this.patchCustomerWithContacts(application, CustomerRoleType.APPLICANT, data.applicant, data.contacts, data.removedContactIds);
    this.patchCustomerWithContacts(application, CustomerRoleType.REPRESENTATIVE, data.representative, data.contacts, data.removedContactIds);
    this.patchCustomerWithContacts(application, CustomerRoleType.PROPERTY_DEVELOPER, data.propertyDeveloper, data.contacts, data.removedContactIds);
    this.patchCustomerWithContacts(application, CustomerRoleType.CONTRACTOR, data.contractor, data.contacts, data.removedContactIds);

    this.patchOtherInfo(application, data.otherInfo);
    return new InformationRequestResult(requestId, application, data.invoicingCustomer, data.useCustomerForInvoicing);
  }

  private patchCustomerWithContacts(application: Application, role: CustomerRoleType, customer: Customer, contacts: Contact[] = [], removedContactIds: number[] = []) {
    if (customer) {
      // Start from the existing contacts for this role (contacts that pre-existed on the application).
      // These include contacts that the external API did not mention (i.e. "kept" contacts).
      const existingCwc = (application.customersWithContacts || []).find(cwc => cwc.roleType === role);
      const existingContacts: Contact[] = existingCwc ? existingCwc.contacts || [] : [];

      // New/updated contacts dispatched via SetContact (matched from the external API response)
      const newContacts = contacts.filter(c => c.customerId === customer.id);

      // Merge: start with existing contacts, upsert any new/updated contact by id, then filter
      // out explicitly removed ones.
      let merged: Contact[] = [...existingContacts];
      for (const nc of newContacts) {
        const idx = merged.findIndex(c => c.id === nc.id);
        if (idx >= 0) {
          merged[idx] = nc;
        } else {
          merged.push(nc);
        }
      }
      merged = merged.filter(c => !removedContactIds.includes(c.id));

      const customerWithContacts = new CustomerWithContacts(role, customer, merged);
      application.customersWithContacts = ArrayUtil.createOrReplace(
        application.customersWithContacts,
        customerWithContacts,
        cwc => cwc.roleType === role
      );
    } else if (customer === null) {
      // Customer explicitly set to null means removal
      application.customersWithContacts = (application.customersWithContacts || []).filter(
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
