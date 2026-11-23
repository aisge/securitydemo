# securitydemo

Demo-Projekt zur Übung *Security* (Quarkus 3.40, Java 21).
Ausgangspunkt ist ein einfaches REST-Service für Studenten (`/students`).
Jede Security-Variante liegt in einem eigenen Branch:

| Branch               | Inhalt                                                          |
|----------------------|-----------------------------------------------------------------|
| `master`             | Ausgangsprojekt ohne Security (REST + Hibernate ORM Panache)    |
| `elytron.properties` | Basic Auth, User/Rollen in Properties-Files (MD5-Hashes)        |
| `elytron.jdbc`       | Basic Auth, User/Rollen in der Datenbank (bcrypt)               |
| `jwt`                | selbst ausgestellte JSON Web Tokens (SmallRye JWT)              |
| `keycloak`           | OpenID Connect mit Keycloak (Dev Services oder eigener Server)  |

```shell
git clone https://github.com/aisge/securitydemo.git
git branch --all
git checkout jwt
```

## Voraussetzungen

* Java 21
* Datenbank: H2 In-Memory – keine Installation nötig, Daten werden bei jedem Start
  neu aus `import.sql` geladen.
* Nur Branch `keycloak`: Docker oder Podman (Dev Services starten dort Keycloak).

## Starten

```shell
./mvnw quarkus:dev     # Dev-Modus mit Live-Reload, Dev UI: http://localhost:8080/q/dev-ui
./mvnw test            # Tests
```

## Packaging

```shell
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

Auch im prod-Profil wird eine H2 In-Memory-Datenbank verwendet
(`%prod.quarkus.datasource.jdbc.url` in `src/main/resources/application.properties`).

## Branch `keycloak`

Realm-Definition: `src/main/resources/quarkus-realm.json`

* Realm `quarkus-realm`, Realm-Rollen `admin`, `user`
* User `max` / `passme` (admin, user) und `susi` / `passme` (user)
* Client `quarkus-client` (confidential, Secret `secret`, Direct access grants für Tests)
* Client `angular-client` (public, Authorization Code Flow mit PKCE, `http://localhost:4200`)

**Variante A – Dev Services (empfohlen):** `./mvnw quarkus:dev` startet Keycloak automatisch
auf Port 8180 und importiert den Realm. Admin-Konsole: Dev UI → Keycloak.

**Variante B – eigener Keycloak-Container:**

```shell
docker run -d --name keycloak -p 8180:8080 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=passme \
  -v "$PWD/src/main/resources/quarkus-realm.json:/opt/keycloak/data/import/quarkus-realm-realm.json:ro,Z" \
  quay.io/keycloak/keycloak:26.8 start-dev --import-realm
```

Admin-Konsole: http://localhost:8180 (admin / passme).
Beim Import muss die Datei im Container `<realm>-realm.json` heißen, daher der Mount-Name.
Angular-Client: Ordner `frontend` (Angular 22, Code Flow mit PKCE), siehe `frontend/README.md`.

Damit Quarkus diesen Keycloak statt Dev Services verwendet:
`quarkus.oidc.auth-server-url=http://localhost:8180/realms/quarkus-realm` setzen.
