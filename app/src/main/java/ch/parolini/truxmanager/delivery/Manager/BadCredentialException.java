package ch.parolini.truxmanager.delivery.Manager;

/**
 * Created by toni on 05.10.14.
 */
public class BadCredentialException extends Exception {
    public BadCredentialException(String detailMessage) {
        super(detailMessage);
    }
}
