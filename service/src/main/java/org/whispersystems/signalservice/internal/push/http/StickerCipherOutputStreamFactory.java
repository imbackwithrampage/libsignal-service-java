package org.whispersystems.signalservice.internal.push.http;


import org.whispersystems.signalservice.api.crypto.AttachmentCipherOutputStream;
import org.whispersystems.signalservice.api.crypto.DigestingOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public class StickerCipherOutputStreamFactory implements OutputStreamFactory {

  private final byte[] key;

  public StickerCipherOutputStreamFactory(byte[] key) {
    this.key = key;
  }

  @Override
  public DigestingOutputStream createFor(OutputStream wrap) throws IOException {
    return new AttachmentCipherOutputStream(key, null, wrap);
  }
}
