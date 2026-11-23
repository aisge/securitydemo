import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

/** Funktionaler Guard: ohne gültiges Access Token zurück zur Startseite. */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  return authService.hasValidAccessToken() ? true : router.createUrlTree(['/']);
};
