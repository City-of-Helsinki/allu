const TestUtil = require('../util/test-util');
const rp = require('request-promise');

TestUtil.assertEnv();

describe('Application draft', () => {

  let applicationStartTime = TestUtil.getISODateString(-1);
  let applicationEndTime = TestUtil.getISODateString(30);

  let applicantCustomersWithContactsCreated;

  function createCustomers() {
		const applicantContactNew = {
			'id' : null,
			'name' : 'Aku Ankka',
			'streetAddress' : 'Paratiisitie 13',
			'postalCode' : '121212',
			'city' : 'Ankkalinna',
			'email' : 'aku@ankka.fi',
			'phone' : '03-13131313',
			'active' : true,
			'country': 'FI'
		};

		const applicantCustomerWithContactsNew = {
			'roleType' : 'APPLICANT',
			'customer' : {
				'id' : null,
				'type' : 'COMPANY',
				'person' : null,
				'name' : 'Kattivaaran Margariinitehdas Oy.',
				'registryKey' : '131313-3',
				'postalAddress' : {
					'streetAddress' : 'Kattivaarantie 10',
					'postalCode' : '13131',
					'city' : 'Ankkalinna'
				},
				'email' : 'katti@vaara.fi',
				'phone' : '13-131313',
				'active' : true,
				'country': 'FI'
			},
			'contacts' : [ applicantContactNew ]
		};

   let applicantCustOptions = TestUtil.getPostOptions('/api/customers/withcontacts', applicantCustomerWithContactsNew);
    return TestUtil.login('kasittelija')
    .then(token => TestUtil.addAuthorization(applicantCustOptions, token))
    .then(() => rp(applicantCustOptions))
    .then(cwc => applicantCustomersWithContactsCreated = cwc);
    console.log(applicantCustomersWithContactsCreated);
  }

  beforeAll(done => {
    TestUtil.beforeAll(createCustomers)
    .then(done)
    .catch(err => done.fail(err));
  });

  it('Create', done => {
    const draft = {
      'type': 'SHORT_TERM_RENTAL',
      'kindsWithSpecifiers': {'BRIDGE_BANNER' : []},
      'locations': [
          {
            'id': null,
            'startTime': applicationStartTime,
            'endTime': applicationEndTime,
            'geometry': {
              'type': 'GeometryCollection',
              'crs': {
                'properties': {
                  'name': 'EPSG:3879'
                },
                'type': 'name'
              },
              'bbox': null,
              'geometries': [
                {
                  'type': 'Polygon',
                  'crs': null,
                  'bbox': [
                	  2.549815796449239E7,
                	  6672928.0334480135,
                	  2.5498190016256575E7,
                	  6672968.0474401545],
                  'coordinates': [
                    [
                      [
                        2.549815796449239E7,
                        6672928.049400462
                      ],
                      [
                        2.5498157984586794E7,
                        6672968.0474401545
                      ],
                      [
                        2.5498190016256575E7,
                        6672968.031487824
                      ],
                      [
                        2.5498189996511605E7,
                        6672928.0334480135
                      ],
                      [
                        2.549815796449239E7,
                        6672928.049400462
                      ]
                    ]
                  ]
                }
              ]
            },
            'area': 1281.2113072694833,
            'areaOverride': null,
            'postalAddress': {
              'streetAddress': null,
              'postalCode': null,
              'city': null
            },
            'fixedLocationIds': []
          }
        ],
      'name': 'Alustava varaus siltamainoksesta',
      'customersWithContacts': [applicantCustomersWithContactsCreated],
      'extension': {
        'applicationType': 'SHORT_TERM_RENTAL'
      },

    };


    let options = TestUtil.getPostOptions('/api/drafts', draft);
    TestUtil.login('kasittelija')
    .then(token => TestUtil.addAuthorization(options, token))
    .then(() => rp(options))
    .then(done, done.fail);
  });

});
