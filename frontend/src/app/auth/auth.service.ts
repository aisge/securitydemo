import { Injectable, computed, inject, signal } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { authCodeFlowConfig } from './auth.config';

/**
 * Kapselt den OAuthService und stellt den Login-Zustand als Signals bereit
 * (Angular ist zoneless - Änderungen werden über Signals sichtbar).
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly oauthService = inject(OAuthService);

  private readonly accessToken = signal<string | null>(null);

  readonly isLoggedIn = computed(() => this.accessToken() !== null);
  readonly claims = computed(() => decodeJwtPayload(this.accessToken()));
  readonly userName = computed(() => (this.claims()['preferred_username'] as string) ?? '');
  readonly givenName = computed(() => (this.claims()['given_name'] as string) ?? '');
  /** Realm-Rollen aus dem Access Token (nur zur Anzeige - geprüft wird im Backend!) */
  readonly roles = computed<string[]>(
    () => ((this.claims()['realm_access'] as { roles?: string[] })?.roles ?? []).filter(
      (r) => r === 'admin' || r === 'user',
    ),
  );

  async init(): Promise<void> {
    this.oauthService.configure(authCodeFlowConfig);
    this.oauthService.events.subscribe(() => this.updateState());
    // verarbeitet den Redirect von Keycloak (?code=...) und holt die Tokens
    await this.oauthService.loadDiscoveryDocumentAndTryLogin();
    // Access Token vor Ablauf automatisch erneuern (Refresh Token)
    this.oauthService.setupAutomaticSilentRefresh();
    this.updateState();
  }

  login(): void {
    this.oauthService.initCodeFlow();
  }

  logout(): void {
    this.oauthService.logOut();
    this.updateState();
  }

  hasRole(role: string): boolean {
    return this.roles().includes(role);
  }

  hasValidAccessToken(): boolean {
    return this.oauthService.hasValidAccessToken();
  }

  private updateState(): void {
    this.accessToken.set(
      this.oauthService.hasValidAccessToken() ? this.oauthService.getAccessToken() : null,
    );
  }
}

function decodeJwtPayload(token: string | null): Record<string, unknown> {
  if (!token) {
    return {};
  }
  const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
  const json = decodeURIComponent(
    Array.from(atob(base64), (c) => '%' + c.charCodeAt(0).toString(16).padStart(2, '0')).join(''),
  );
  return JSON.parse(json);
}
