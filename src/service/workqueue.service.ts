export class WorkqueueService {
  joblist = [
    {
      title: 'Hakemuksen nimi',
      name: 'Hakemuksen jättäjä',
      type: 'Hakemuksen tyyppi',
      time: 'Hakemuksen ajankohta'
    },
    {
      title: 'Teekkarin Vappu',
      name: 'Sadi Hossain / Vincit Helsinki Oy',
      type: 'Promootio',
      time: '24.04.2016 - 05.05.2016'
    },
    {
      title: 'Team Building Exercises',
      name: 'Jan Nikander / Vincit Helsinki Oy',
      type: 'Alustava hakemus',
      time: '04.05.2016 - 08.05.2016'
    },
    {
      title: 'Vappurun Helsinki',
      name: 'Perttu Taskinen / Vincit Helsinki Oy',
      type: 'Banneri',
      time: '01.05.2016 - 01.05.2016'
    }
  ];

  get(): {title:string, name:string, type:string, time:string}[] {
    return this.joblist;
  }
  add(value: {title:string, name:string, type:string, time: string}): void {
    this.joblist.push(value);
  }
}
