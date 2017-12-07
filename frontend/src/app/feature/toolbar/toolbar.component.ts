import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../service/authorization/auth.service';
import {ConfigService} from '../../service/config/config.service';
import {Observable} from 'rxjs/Observable';

@Component({
  selector: 'toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: [
    './toolbar.component.scss'
  ]
})
export class ToolbarComponent implements OnInit {
  version: Observable<string>;

  constructor(private authService: AuthService, private config: ConfigService) {
  }

  ngOnInit(): void {
    this.version = this.config.getConfiguration()
      .map(config => config.versionNumber);
  }

  get authenticated() {
    return this.authService.authenticated();
  }
}
