package com.iunera.generaldatatypes.place.locationdatatypes.bluetooth;

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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
public class BluetoothBeaconInformation {
  public static class BluetoothBeaconDistanceComparator
      implements Comparator<BluetoothBeaconInformation> {

    @Override
    public int compare(BluetoothBeaconInformation b1, BluetoothBeaconInformation b2) {
      return Float.compare(b1.distance, b2.distance);
    }
  }

  public BluetoothBeaconInformation() {}

  /** the timestamp when the beacon was seen */
  public long lastSeenTimeStamp;

  public String eddystoneURL;
  /**
   * The a list of the multi-part identifiers of the beacon. Together, these identifiers signify a
   * unique beacon.
   */
  public Map<String, String> identifiers;

  /**
   * A list of generic non-identifying data fields included in the beacon advertisement. Data fields
   * are limited to the size of a Java long, or six bytes.
   */
  // protected List<Long> mDataFields;

  /**
   * A double that is an estimate of how far the Beacon is away in meters. Note that this number
   * fluctuates quite a bit with RSSI, so despite the name, it is not super accurate.
   */
  public float distance;
  /** The measured signal strength of the Bluetooth packet that led do this Beacon detection. */
  public int rssi;
  /**
   * The calibrated measured Tx power of the Beacon in RSSI This value is baked into an Beacon when
   * it is manufactured, and it is transmitted with each packet to aid in the mDistance estimate
   */
  public int mTxPower;

  /** The bluetooth mac address */
  public String bluetoothAddress;

  /** If multiple RSSI samples were available, this is the running average */
  public Float runningAverageRssi = null;

  /** Used to attach further data of a specific beacon */
  public List<Long> beaconData = null;

  /**
   * The two byte value indicating the type of beacon that this is, which is used for figuring out
   * the byte layout of the beacon advertisement
   */
  public int beaconTypeCode;

  /**
   * A two byte code indicating the beacon manufacturer. A list of registered manufacturer codes may
   * be found here: https://www.bluetooth.org/en-us/specification
   * /assigned-numbers/company-identifiers
   */
  public int manufacturer;

  /**
   * The bluetooth device name. This is a field transmitted by the remote beacon device separate
   * from the advertisement data
   */
  public String bluetoothName;
}
