export class TimePeriod {
  constructor()
  constructor(startTime: string, endTime: string)
  constructor(public startTime?: string, public endTime?: string) {}
}
