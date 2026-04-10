import {StructureMeta} from '@model/application/meta/structure-meta';
import {AttributeMeta} from '@model/application/meta/attribute-meta';

export const CUSTOMER_META = new StructureMeta('Customer', 1, [
  // Customer type (enum)
  new AttributeMeta('/type',                                    'Tyyppi',                          'ENUMERATION', undefined),
  new AttributeMeta('/type/PERSON',                             'Henkilö',                         'ENUM_VALUE',  undefined),
  new AttributeMeta('/type/COMPANY',                            'Yritys',                          'ENUM_VALUE',  undefined),
  new AttributeMeta('/type/ASSOCIATION',                        'Yhdistys',                        'ENUM_VALUE',  undefined),
  new AttributeMeta('/type/OTHER',                              'Muu',                             'ENUM_VALUE',  undefined),

  // Customer basic fields
  new AttributeMeta('/name',                                    'Nimi',                            'STRING',      undefined),
  new AttributeMeta('/email',                                   'Sähköpostiosoite',                'STRING',      undefined),
  new AttributeMeta('/phone',                                   'Puhelin',                         'STRING',      undefined),
  new AttributeMeta('/registryKey',                             'Y-tunnus / Henkilötunnus',        'STRING',      undefined),
  new AttributeMeta('/ovt',                                     'OVT-tunnus',                      'STRING',      undefined),
  new AttributeMeta('/active',                                  'Aktiivinen',                      'BOOLEAN',     undefined),
  new AttributeMeta('/sapCustomerNumber',                       'SAP-asiakasnumero',               'STRING',      undefined),
  new AttributeMeta('/invoicingProhibited',                     'Laskutus estetty',                'BOOLEAN',     undefined),
  new AttributeMeta('/invoicingOperator',                       'Laskutusoperaattori',             'STRING',      undefined),
  new AttributeMeta('/invoicingOnly',                           'Vain laskutusasiakas',            'BOOLEAN',     undefined),
  new AttributeMeta('/countryId',                               'Maa',                             'INTEGER',     undefined),
  new AttributeMeta('/projectIdentifierPrefix',                 'Projektitunnuksen alkuosa',       'STRING',      undefined),

  // Customer postal address
  new AttributeMeta('/postalAddress/streetAddress',             'Katuosoite',                      'STRING',      undefined),
  new AttributeMeta('/postalAddress/postalCode',                'Postinumero',                     'STRING',      undefined),
  new AttributeMeta('/postalAddress/city',                      'Toimipaikka',                     'STRING',      undefined),

  // Contact fields — the numeric contact ID in paths is wildcarded to * during lookup
  new AttributeMeta('/contacts/*/name',                        'Yhteyshenkilö',                   'STRING',      undefined),
  new AttributeMeta('/contacts/*/email',                        'Sähköpostiosoite',                'STRING',      undefined),
  new AttributeMeta('/contacts/*/phone',                        'Puhelin',                         'STRING',      undefined),
  new AttributeMeta('/contacts/*/active',                       'Aktiivinen',                      'BOOLEAN',     undefined),
  new AttributeMeta('/contacts/*/orderer',                      'Tilaaja',                         'BOOLEAN',     undefined),
  new AttributeMeta('/contacts/*/postalAddress/streetAddress',  'Katuosoite',                      'STRING',      undefined),
  new AttributeMeta('/contacts/*/postalAddress/postalCode',     'Postinumero',                     'STRING',      undefined),
  new AttributeMeta('/contacts/*/postalAddress/city',           'Toimipaikka',                     'STRING',      undefined),
]);
