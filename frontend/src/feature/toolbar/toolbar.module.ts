import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {ToolbarComponent} from './toolbar.component';
import {NavbarComponent} from './navbar/navbar.component';

@NgModule({
  imports: [
    RouterModule.forChild([])
  ],
  declarations: [
    ToolbarComponent,
    NavbarComponent
  ],
  exports: [
    ToolbarComponent
  ]
})
export class ToolbarModule {}
