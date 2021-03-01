package se.jsquad.integration;

import io.restassured.RestAssured;
import org.jasypt.util.text.BasicTextEncryptor;

public abstract class RyukIntegration {
    protected static final String CONTAINER_ENVIRONMENT = System.getenv("CONTAINER_ENVIRONMENT");
    
    protected static final String BASE_PATH_ACTUATOR = "/actuator";
    protected static final String BASE_PATH_API = "/api";
    protected static final String PROTOCOL_HTTP = "http";
    protected static final String PROTOCOL_HTTPS = "https";
    
    protected static final int HTTP_K8_PORT = 80;
    protected static final int HTTPS_K8_PORT = 443;
    protected static final int MOCK_SERVER_PORT = 1080;
    protected static final int MONITORING_PORT = 8081;
    protected static final int POSTGRES_PORT = 5432;
    protected static final int SERVICE_PORT = 8443;
    
    protected static final String DEFAULT_NAME_SPACE = "default";
    protected static final String DOCKER_COMPOSE_SERVICE_NAME_ENDING = "_1";
    protected static final String KUBERNETES_STARTER_SERVICE_NAME = "kubernetes-starter";
    protected static final String LOCALHOST = "localhost";
    protected static final String OPENBANK_DATABASE_NAME = "openbankdb";
    protected static final String SECURITY_DATABASE_NAME = "securitydb";
    protected static final String SERVICE_NAME = "openbank";
    
    public static final RyukService OPENBANK_SERVICE;
    public static final RyukService OPENBANK_MONITORING;
    public static final RyukService OPENBANK_DATABASE;
    public static final RyukService SECURITY_DATABASE;
    
    static {
        if (CONTAINER_ENVIRONMENT.equals("DOCKER")) {
            OPENBANK_SERVICE = new RyukService(SERVICE_NAME, SERVICE_PORT);
            OPENBANK_MONITORING = new RyukService(SERVICE_NAME, MONITORING_PORT);
            OPENBANK_DATABASE = new RyukService(OPENBANK_DATABASE_NAME, POSTGRES_PORT);
            SECURITY_DATABASE = new RyukService(SECURITY_DATABASE_NAME, POSTGRES_PORT);
        } else {
            OPENBANK_SERVICE = new RyukService(KUBERNETES_STARTER_SERVICE_NAME, HTTPS_K8_PORT);
            OPENBANK_MONITORING = new RyukService(KUBERNETES_STARTER_SERVICE_NAME, HTTP_K8_PORT);
            OPENBANK_DATABASE = null;
            SECURITY_DATABASE = null;
        }
    }
    
    protected static void setupRestAssured() {
        String encryptedPassword = "RMiukf/2Ir2Dr1aTGd0J4CXk6Y/TyPMN";
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(System.getenv("MASTER_KEY"));
        RestAssured.trustStore("src/test/resources/test/ssl/truststore/jsquad.jks",
            textEncryptor.decrypt(encryptedPassword));
        
        if (CONTAINER_ENVIRONMENT.equals("KUBERNETES")) {
            RestAssured.useRelaxedHTTPSValidation();
        }
        
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    
    public static class RyukService {
        private final String serviceName;
        private final int port;
        
        public RyukService(final String serviceName, final int port) {
            this.serviceName = serviceName;
            this.port = port;
        }
        
        public String getServiceName() {
            return serviceName;
        }
        
        public int getPort() {
            return port;
        }
    }
}
