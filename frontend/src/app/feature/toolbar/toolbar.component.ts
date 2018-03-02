import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../service/authorization/auth.service';
import {ConfigService} from '../../service/config/config.service';
import {Observable} from 'rxjs/Observable';
import {EnvironmentType} from '../../model/config/environment-type';

@Component({
  selector: 'toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: [
    './toolbar.component.scss'
  ]
})
export class ToolbarComponent implements OnInit {
  version: Observable<string>;
  logo: string;

  constructor(private authService: AuthService, private config: ConfigService) {
  }

  ngOnInit(): void {
    this.version = this.config.getConfiguration()
      .map(config => config.versionNumber);

    this.config.getConfiguration().subscribe(config =>
      this.logo = config.environment === EnvironmentType.PRODUCTION
        ? 'assets/svg/allu-logo.svg'
        : 'assets/svg/allu-testi-logo.svg');
  }

  get authenticated() {
    return this.authService.authenticated();
  }
}
