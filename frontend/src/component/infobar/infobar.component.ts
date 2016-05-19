import {Component} from 'angular2/core';


@Component({
  selector: 'infobar',
  moduleId: module.id,
  template: require('./infobar.component.html'),
  styles: [
    require('./infobar.component.scss')
  ],
  directives: []
})
export class InfobarComponent {
  public title: string;

  constructor() {
      this.title = 'UUSI HAKEMUS';
  }
}
