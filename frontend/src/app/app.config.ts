import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { provideOAuthClient } from 'angular-oauth2-oidc';
import { routes } from './app.routes';
import { AuthService } from './auth/auth.service';
import { environment } from '../environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    // Interceptor von angular-oauth2-oidc ist DI-basiert -> withInterceptorsFromDi()
    provideHttpClient(withInterceptorsFromDi()),
    provideOAuthClient({
      resourceServer: {
        allowedUrls: [environment.backendUrl], // nur dorthin wird das Token geschickt
        sendAccessToken: true,
      },
    }),
    // Login-Redirect verarbeiten, bevor das Routing startet
    provideAppInitializer(() => inject(AuthService).init()),
  ],
};
