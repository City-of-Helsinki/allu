import {Component, Input} from '@angular/core';

@Component({
  selector: 'loading',
  template: require('./loading.component.html')
})
export class LoadingComponent {
  @Input() public isLoading: boolean;
}
