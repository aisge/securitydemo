import { AuthConfig } from 'angular-oauth2-oidc';
import { environment } from '../../environments/environment';

/** Authorization Code Flow mit PKCE (Default von angular-oauth2-oidc für responseType 'code'). */
export const authCodeFlowConfig: AuthConfig = {
  issuer: environment.keycloakIssuer,
  clientId: environment.keycloakClientId, // public Client, kein Secret!
  redirectUri: window.location.origin + '/',
  postLogoutRedirectUri: window.location.origin + '/',
  responseType: 'code',
  scope: 'openid profile email',
  showDebugInformation: true,
  requireHttps: false, // nur für lokale Entwicklung!
};
