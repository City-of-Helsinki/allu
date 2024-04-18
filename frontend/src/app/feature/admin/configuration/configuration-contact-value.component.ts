import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormControl, Validators} from '@angular/forms';
import {Configuration} from '@model/config/configuration';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromConfiguration from '@feature/admin/configuration/reducers';
import * as fromCustomerRegistry from '@feature/customerregistry/reducers';
import {Save} from '@feature/admin/configuration/actions/configuration-actions';
import {Observable, Subject} from 'rxjs';
import {Contact} from '@model/customer/contact';
import {ComplexValidator} from '@util/complex-validator';
import {debounceTime, filter, take, takeUntil} from 'rxjs/internal/operators';
import * as ContactSearchAction from '@feature/customerregistry/actions/contact-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {ContactNameSearchMinChars, ContactSearchQuery} from '@service/customer/contact-search-query';
import {Sort} from '@model/common/sort';
import {FindById} from '@feature/customerregistry/actions/contact-actions';
import {NumberUtil} from '@util/number.util';

@Component({
  selector: 'configuration-contact-value',
  templateUrl: './configuration-contact-value.component.html',
  styleUrls: ['./configuration-value.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigurationContactValueComponent implements OnInit, OnDestroy {

  @Input() configuration: Configuration;

  matching$: Observable<Contact[]>;

  valueCtrl: UntypedFormControl;

  private destroy = new Subject<boolean>();

  constructor(
    private fb: UntypedFormBuilder,
    private store: Store<fromRoot.State>
  ) {}

  ngOnInit(): void {
    this.matching$ = this.store.pipe(select(fromConfiguration.getMatchingContacts));
    this.initValue(this.configuration.value);

    this.valueCtrl.valueChanges.pipe(
      takeUntil(this.destroy),
      debounceTime(300),
      filter(contact => typeof contact === 'string'),
      filter(ContactNameSearchMinChars),
    ).subscribe(name => this.searchContact(name));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  submit(): void {
    const configuration: Configuration = {
      ...this.configuration,
      value: this.valueCtrl.value.id || ''
    };
    this.store.dispatch(new Save(configuration));
  }

  contactName(contact?: Contact): string | undefined {
    return contact ? contact.name : undefined;
  }

  private initValue(contactId: string): void {
    this.valueCtrl = this.fb.control(undefined, [ComplexValidator.idValid]);
    if (NumberUtil.isNumeric(contactId)) {
      this.fetchInitialContact(+contactId);
    }
  }

  private searchContact(name: string): void {
    const query: ContactSearchQuery = {name, active: true};
    this.store.dispatch(new ContactSearchAction.Search(ActionTargetType.Configuration, {query, sort: new Sort('name', 'asc')}));
  }

  private fetchInitialContact(contactId: number): void {
    this.store.dispatch(new FindById(contactId));
    this.store.pipe(
      select(fromCustomerRegistry.getContact(contactId)),
      filter(contact => !!contact),
      take(1)
    ).subscribe(contact => this.valueCtrl.patchValue(contact));
  }
}
