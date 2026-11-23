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
