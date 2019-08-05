package ch.parolini.truxmanager.delivery;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Didier on 18.11.2016.
 */

public class EasyX509TrustManager implements X509TrustManager {

    private X509TrustManager standardTrustManager = null;

    /**
     * Constructor for EasyX509TrustManager.
     */
    public EasyX509TrustManager(KeyStore keystore)
            throws NoSuchAlgorithmException, KeyStoreException {
        super();
        TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        factory.init(keystore);
        TrustManager[] trustmanagers = factory.getTrustManagers();
        if (trustmanagers.length == 0) {
            throw new NoSuchAlgorithmException("no trust manager found");
        }
        this.standardTrustManager = (X509TrustManager) trustmanagers[0];
    }



    /**
     * @see X509TrustManager#checkServerTrusted(X509Certificate[], String authType)
     */
    public void checkServerTrusted(X509Certificate[] certificates, String authType)
            throws CertificateException {
        if ((certificates != null) && (certificates.length == 1)) {
            certificates[0].checkValidity();
        } else {
            standardTrustManager.checkServerTrusted(certificates, authType);
        }
    }

    @Override
    public
    void checkClientTrusted (X509Certificate[] chain, String authType) throws CertificateException {
        X509Certificate[] certificates=null;
        standardTrustManager.checkClientTrusted(certificates, authType);
    }

    /**
     * @see X509TrustManager#getAcceptedIssuers()
     */
    public X509Certificate[] getAcceptedIssuers() {
        return this.standardTrustManager.getAcceptedIssuers();
    }

}
