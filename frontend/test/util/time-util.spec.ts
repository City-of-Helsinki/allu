import moment from 'moment';
import {TimeUtil} from '../../src/app/util/time.util';

const WINTER_TIME_START = '1973-12-01';
const WINTER_TIME_END = '1973-05-14';

describe('Time util', () => {
  it('should show is in winter time', () => {
    expect(TimeUtil.isInWinterTime(moment('2017-12-01').toDate(), WINTER_TIME_START, WINTER_TIME_END))
      .toBe(true, 'Winter start date was not in winter time');

    expect(TimeUtil.isInWinterTime(moment('2018-05-13').toDate(), WINTER_TIME_START, WINTER_TIME_END))
      .toBe(true, 'Winter end date was not in winter time');

    expect(TimeUtil.isInWinterTime(moment('2017-01-01').toDate(), WINTER_TIME_START, WINTER_TIME_END))
      .toBe(true, 'Winter date was not in winter time');
  });

  it('should map given date to the end of winter time current year', () => {
    const original = moment('2017-01-01');
    const winterTimeEnd = TimeUtil.toWinterTimeEnd(original.toDate(), WINTER_TIME_END);
    expect(winterTimeEnd).toEqual(moment(WINTER_TIME_END).year(original.year()).toDate());
  });

  it('should map given date before year change to winter time end next year', () => {
    const original = moment('2016-12-12');
    const winterTimeEnd = TimeUtil.toWinterTimeEnd(original.toDate(), WINTER_TIME_END);
    expect(winterTimeEnd).toEqual(moment(WINTER_TIME_END).year(original.year() + 1).toDate());
  });
});
