import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PhonesComponent } from './phones/phones.component';
import { WrapperComponent } from './wrapper/wrapper.component';

const routes: Routes = [
  {
    path: '',
    component: WrapperComponent,
    children: [
      {
        path: '',
        component: PhonesComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CoreRoutingModule {}
