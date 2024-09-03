package com.iunera.generaldatatypes;

/*-
 * #%L
 * iu-occupancy-prediction-api
 * %%
 * Copyright (C) 2024 Tim Frey, Christian Schmitt
 * %%
 * Licensed under the OPEN COMPENSATION TOKEN LICENSE (the "License").
 *
 * You may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * <https://github.com/open-compensation-token-license/license/blob/main/LICENSE.md>
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @octl.sid: 1b6f7a5d-8dcf-44f1-b03a-77af04433496
 * #L%
 */

// import android.annotation.SuppressLint;
import java.security.SecureRandom;

/** A picture ID is a unique (in the sense of an UUID) ID that is generated by the client. */
public class RGUID {

  private String id;
  private static final char GUID_VERSION = 'a';
  private static final int LENGTH_BYTES = 16; // 16 bytes = 128 bit
  private static final int LENGTH = 2 * LENGTH_BYTES; // 2 hexadecimal chars equal 1 byte
  // if changed update documentation of fromString() methdod

  /* For random id generation. */
  //	@SuppressLint("TrulyRandom") // cf.
  // http://android-developers.blogspot.de/2013/08/some-securerandom-thoughts.html
  private static final SecureRandom RND = new SecureRandom();
  private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

  private static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  /** Static factory method that generates a random picture ID. */
  public static RGUID generateNew() {
    byte[] idBytes = new byte[LENGTH_BYTES];
    RND.nextBytes(idBytes);

    RGUID pictureId = new RGUID();
    pictureId.id = GUID_VERSION + bytesToHex(idBytes);
    return pictureId;
  }

  /**
   * Static factory methods that generates a picture ID from a given string {@code s}.
   *
   * <p>Basically, only the internal id is set to {@code s}.
   *
   * @param s a String with 32 characters length.
   */
  public static RGUID fromString(String s) {
    if (s == null)
      throw new IllegalArgumentException("String for PictureId creation must not be null.");
    if (s.length() != LENGTH + 1) // +1 needed because we save the version of the guid
    throw new IllegalArgumentException(
          String.format(
              "String for PictureId creation must have a length of %i chars (not %i)",
              LENGTH, s.length()));
    // XXX: Should we check for non-hex characters?
    RGUID pictureId = new RGUID();
    pictureId.id = s;
    return pictureId;
  }

  /** returns toString method from internal string */
  @Override
  public String toString() {
    return id.toString();
  }

  /** uses equals method from internal string */
  @Override
  public boolean equals(Object o) {
    if (o == null) return true;
    return id.equals(o.toString());
  }

  /** uses hashCode method from internal string */
  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
