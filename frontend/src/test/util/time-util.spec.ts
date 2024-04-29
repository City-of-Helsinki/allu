import moment from 'moment';
import {TimeUtil} from '@util/time.util';

const WINTER_TIME_START = TimeUtil.toStartDate(TimeUtil.dateFromBackend('1973-12-01'));
const WINTER_TIME_END = TimeUtil.toEndDate(TimeUtil.dateFromBackend('1973-05-14'));

describe('Time util', () => {
  it('should show is in winter time', () => {
    expect(TimeUtil.isInTimePeriod(moment('2017-12-01').toDate(), WINTER_TIME_START, WINTER_TIME_END))
      .toBe(true, 'Winter start date was not in winter time');

    expect(TimeUtil.isInTimePeriod(moment('2018-05-13').toDate(), WINTER_TIME_START, WINTER_TIME_END))
      .toBe(true, 'Winter end date was not in winter time');

    expect(TimeUtil.isInTimePeriod(moment('2017-01-01').toDate(), WINTER_TIME_START, WINTER_TIME_END))
      .toBe(true, 'Winter date was not in winter time');
  });

  it('should map given date to the end of winter time current year', () => {
    const original = moment('2017-01-01');
    const winterTimeEnd = TimeUtil.toTimePeriodEnd(original.toDate(), WINTER_TIME_END);
    expect(winterTimeEnd).toEqual(moment(WINTER_TIME_END).year(original.year()).toDate());
  });

  it('should map given date before year change to winter time end next year', () => {
    const original = moment('2016-12-12');
    const winterTimeEnd = TimeUtil.toTimePeriodEnd(original.toDate(), WINTER_TIME_END);
    expect(winterTimeEnd).toEqual(moment(WINTER_TIME_END).year(original.year() + 1).toDate());
  });

  it('should compare undefined at the end', () => {
    const date = new Date();
    expect(TimeUtil.compareTo(date, undefined)).toEqual(-1);
    expect(TimeUtil.compareTo(undefined, date)).toEqual(1);
  });

  it('should compare equal as equal', () => {
    const date = new Date();
    expect(TimeUtil.compareTo(date, date)).toEqual(0);
  });

  it('should compare two undefined as equal', () => {
    expect(TimeUtil.compareTo(undefined, undefined)).toEqual(0);
  });

  it('should compare defined dates correctly', () => {
    const earlier = moment('2019-10-10').toDate();
    const later = moment('2019-11-11').toDate();
    expect(TimeUtil.compareTo(earlier, later)).toEqual(-1);
    expect(TimeUtil.compareTo(later, earlier)).toEqual(1);
  });
});
