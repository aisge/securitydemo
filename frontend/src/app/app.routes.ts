import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';
import { Home } from './home/home';
import { Students } from './students/students';

export const routes: Routes = [
  { path: 'students', component: Students, canActivate: [authGuard] },
  { path: '**', component: Home },
];
