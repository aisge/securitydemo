export const environment = {
  backendUrl: 'http://localhost:8080',
  // Keycloak: Dev Services (./mvnw quarkus:dev) bzw. eigener Container auf Port 8180
  keycloakIssuer: 'http://localhost:8180/realms/quarkus-realm',
  keycloakClientId: 'angular-client',
};
