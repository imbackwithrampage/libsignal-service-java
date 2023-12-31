package org.whispersystems.signalservice.api.crypto;

import org.signal.libsignal.metadata.InvalidMetadataMessageException;
import org.signal.libsignal.metadata.InvalidMetadataVersionException;
import org.signal.libsignal.metadata.ProtocolDuplicateMessageException;
import org.signal.libsignal.metadata.ProtocolInvalidKeyException;
import org.signal.libsignal.metadata.ProtocolInvalidKeyIdException;
import org.signal.libsignal.metadata.ProtocolInvalidMessageException;
import org.signal.libsignal.metadata.ProtocolInvalidVersionException;
import org.signal.libsignal.metadata.ProtocolLegacyMessageException;
import org.signal.libsignal.metadata.ProtocolNoSessionException;
import org.signal.libsignal.metadata.ProtocolUntrustedIdentityException;
import org.signal.libsignal.metadata.SealedSessionCipher;
import org.signal.libsignal.metadata.SelfSendException;
import org.signal.libsignal.metadata.certificate.CertificateValidator;
import org.signal.libsignal.metadata.protocol.UnidentifiedSenderMessageContent;
import org.signal.libsignal.protocol.InvalidKeyException;
import org.signal.libsignal.protocol.InvalidRegistrationIdException;
import org.signal.libsignal.protocol.NoSessionException;
import org.signal.libsignal.protocol.SignalProtocolAddress;
import org.signal.libsignal.protocol.UntrustedIdentityException;
import org.signal.libsignal.protocol.logging.Log;
import org.whispersystems.signalservice.api.SignalSessionLock;

import java.util.List;

/**
 * A thread-safe wrapper around {@link SealedSessionCipher}.
 */
public class SignalSealedSessionCipher {
  private static final String TAG = SignalSealedSessionCipher.class.getSimpleName();

  private final SignalSessionLock   lock;
  private final SealedSessionCipher cipher;

  public SignalSealedSessionCipher(SignalSessionLock lock, SealedSessionCipher cipher) {
    this.lock   = lock;
    this.cipher = cipher;
  }

  public byte[] encrypt(SignalProtocolAddress destinationAddress, UnidentifiedSenderMessageContent content)
      throws InvalidKeyException, UntrustedIdentityException
  {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      return cipher.encrypt(destinationAddress, content);
    }
  }

  public byte[] multiRecipientEncrypt(List<SignalProtocolAddress> recipients, UnidentifiedSenderMessageContent content)
      throws InvalidKeyException, UntrustedIdentityException, NoSessionException, InvalidRegistrationIdException
  {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      return cipher.multiRecipientEncrypt(recipients, content);
    }
  }

  public SealedSessionCipher.DecryptionResult decrypt(CertificateValidator validator, byte[] ciphertext, long timestamp) throws InvalidMetadataMessageException, InvalidMetadataVersionException, ProtocolInvalidMessageException, ProtocolInvalidKeyException, ProtocolNoSessionException, ProtocolLegacyMessageException, ProtocolInvalidVersionException, ProtocolDuplicateMessageException, ProtocolInvalidKeyIdException, ProtocolUntrustedIdentityException, SelfSendException {
    Log.i(TAG, "SignalSealedSessionCipher pre-try");
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      Log.i(TAG, "SignalSealedSessionCipher in-try");
      SealedSessionCipher.DecryptionResult result = cipher.decrypt(validator, ciphertext, timestamp);
      Log.i(TAG, "SignalSealedSessionCipher post-result");
      return result;
    }
  }

  public int getSessionVersion(SignalProtocolAddress remoteAddress) {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      return cipher.getSessionVersion(remoteAddress);
    }
  }

  public int getRemoteRegistrationId(SignalProtocolAddress remoteAddress) {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      return cipher.getRemoteRegistrationId(remoteAddress);
    }
  }
}
