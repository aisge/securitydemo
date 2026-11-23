# Angular-Client (Keycloak / OpenID Connect)

Angular 22 (standalone, zoneless) mit [angular-oauth2-oidc](https://github.com/manfredsteyer/angular-oauth2-oidc):
Login über Keycloak mit **Authorization Code Flow + PKCE**, das Access Token wird automatisch
an das Quarkus-Backend geschickt.

## Starten

```shell
# 1. Backend + Keycloak (Dev Services, Port 8180) - im Projekt-Root
./mvnw quarkus:dev

# 2. Frontend
cd frontend
npm install
npm start          # http://localhost:4200
```

Login mit `max` / `passme` (admin, user) oder `susi` / `passme` (user).
`susi` darf Students lesen, beim Anlegen/Löschen liefert das Backend 403.

## Aufbau

| Datei | Inhalt |
|---|---|
| `src/environments/environment.ts` | Backend-URL, Keycloak-Issuer, Client-ID |
| `src/app/auth/auth.config.ts` | `AuthConfig` für den Code Flow |
| `src/app/auth/auth.service.ts` | kapselt `OAuthService`, Login-Zustand/User/Rollen als Signals |
| `src/app/auth/auth.guard.ts` | funktionaler Guard für `/students` |
| `src/app/app.config.ts` | `provideOAuthClient` (Interceptor für Backend-URL), `provideAppInitializer` |
| `src/app/students/` | Liste, Anlegen, Löschen über `StudentService` |

Keycloak-Client `angular-client` (siehe `src/main/resources/quarkus-realm.json`):
public (kein Secret), Redirect-URI `http://localhost:4200/*`, Web Origin `http://localhost:4200`, PKCE S256.
CORS im Backend: `quarkus.http.cors.*` in `application.properties`.

## Tests

```shell
npm test           # Vitest: App-Komponente und authGuard
```
