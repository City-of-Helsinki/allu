package fi.hel.allu.scheduler.domain;

public class SFTPSettings {

    private String host;
    private String user;
    private int port;
    private String password;
    private String knownHosts;
    private String signatureAlgorithm;
    private String keyAlgorithm;
    private int timeout;

    public SFTPSettings(String host, String user, int port, String password, String knownHosts,
                        String signatureAlgorithm, String keyAlgorithm, int timeout) {
        this.host = host;
        this.user = user;
        this.port = port;
        this.password = password;
        this.knownHosts = knownHosts;
        this.signatureAlgorithm = signatureAlgorithm;
        this.keyAlgorithm = keyAlgorithm;
        this.timeout = timeout;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKnownHosts() {
        return knownHosts;
    }

    public void setKnownHosts(String knownHosts) {
        this.knownHosts = knownHosts;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    public void setKeyAlgorithm(String keyAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}