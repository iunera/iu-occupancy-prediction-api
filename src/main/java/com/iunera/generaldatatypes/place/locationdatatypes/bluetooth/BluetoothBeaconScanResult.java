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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
public class BluetoothBeaconScanResult {

  // the vadility till we count a beacon as vanished
  private static final long beaconLastSeenVadilityDuration = 16 * 1000;

  public BluetoothBeaconScanResult() {}

  public long timeStamp;

  // includes the maybe connected one
  public List<BluetoothBeaconInformation> beaconsAroundMe =
      new ArrayList<BluetoothBeaconInformation>();

  // public BluetoothBeaconInformation computeDominantRegion(){

  // }
  public static enum EBluetoothMovementType {
    REFINEMENT,
    TOPBEACONCHANGE,
    REGIONCHANGE
  };
  /** erases the old scan results that are invalid */
  public BluetoothBeaconScanResult clearInvalid() {
    if (this.beaconsAroundMe != null) {
      List<BluetoothBeaconInformation> uncleaned = this.beaconsAroundMe;
      this.beaconsAroundMe = new ArrayList<BluetoothBeaconInformation>(this.beaconsAroundMe.size());
      long currenttime = new Date().getTime();
      for (BluetoothBeaconInformation i : uncleaned) {
        if (currenttime - i.lastSeenTimeStamp < beaconLastSeenVadilityDuration)
          this.beaconsAroundMe.add(i);
      }
    }
    return this;
  }
  /** adds the beacons of an old scan results that are still valid */
  public BluetoothBeaconScanResult mergeWithOlderScanresult(
      BluetoothBeaconScanResult oldScanResult) {

    if (oldScanResult != null
        && oldScanResult.beaconsAroundMe != null
        && oldScanResult.beaconsAroundMe.size() > 0) {
      long currenttime = new Date().getTime();
      if (this.beaconsAroundMe == null)
        this.beaconsAroundMe = new ArrayList<BluetoothBeaconInformation>();
      for (BluetoothBeaconInformation i : oldScanResult.beaconsAroundMe) {
        // check if the beacon is not in the new scan result
        boolean contains = false;
        for (BluetoothBeaconInformation ii : this.beaconsAroundMe) {
          if (ii.bluetoothAddress.equals(i.bluetoothAddress)
              && ((ii.identifiers != null
                      && i.identifiers != null
                      && ii.identifiers.equals(i.identifiers))
                  || (i.eddystoneURL != null
                      && ii.eddystoneURL != null
                      && (i.eddystoneURL.equals(ii.eddystoneURL))))) {
            contains = true;
            break;
          }
        }
        if (!contains)
          if (currenttime - i.lastSeenTimeStamp < beaconLastSeenVadilityDuration)
            this.beaconsAroundMe.add(i);
      }
    }
    return this;
  }

  public static EBluetoothMovementType computeMovementType(
      BluetoothBeaconScanResult oldScanResult, BluetoothBeaconScanResult newScanResult) {
    try {
      if ((oldScanResult == null && newScanResult == null)
          || (oldScanResult != null
              && oldScanResult.beaconsAroundMe == null
              && newScanResult != null
              && newScanResult.beaconsAroundMe == null)
          || (oldScanResult != null
              && oldScanResult.beaconsAroundMe != null
              && oldScanResult.beaconsAroundMe.size() == 0
              && newScanResult != null
              && newScanResult.beaconsAroundMe != null
              && newScanResult.beaconsAroundMe.size() == 0))
        return EBluetoothMovementType.REFINEMENT;
      else if ((oldScanResult == null
              || oldScanResult.beaconsAroundMe == null
              || oldScanResult.beaconsAroundMe.size() == 0)
          && (newScanResult != null
              && newScanResult.beaconsAroundMe != null
              && newScanResult.beaconsAroundMe.size() > 0))
        return EBluetoothMovementType.REGIONCHANGE;
      else if (oldScanResult != null
          && (oldScanResult.beaconsAroundMe != null && oldScanResult.beaconsAroundMe.size() > 0)
          && (newScanResult == null
              || newScanResult.beaconsAroundMe == null
              || newScanResult.beaconsAroundMe.size() == 0))
        return EBluetoothMovementType.REGIONCHANGE;
      else if ((oldScanResult != null
              && oldScanResult.beaconsAroundMe != null
              && (newScanResult == null || newScanResult.beaconsAroundMe == null))
          || (oldScanResult != null
              && oldScanResult.beaconsAroundMe == null
              && newScanResult.beaconsAroundMe != null)) return EBluetoothMovementType.REGIONCHANGE;
      else if ((oldScanResult != null
              && oldScanResult.beaconsAroundMe != null
              && oldScanResult.beaconsAroundMe.size() > 0
              && (newScanResult == null || newScanResult.beaconsAroundMe.size() == 0))
          || (oldScanResult == null
              || oldScanResult.beaconsAroundMe == null
              || oldScanResult.beaconsAroundMe.size() == 0
                  && (newScanResult != null
                      && newScanResult.beaconsAroundMe != null
                      && newScanResult.beaconsAroundMe.size() > 0)))
        return EBluetoothMovementType.REGIONCHANGE;
      else if (oldScanResult != null
          && oldScanResult.beaconsAroundMe != null
          && oldScanResult.beaconsAroundMe.get(0) != null
          && oldScanResult.beaconsAroundMe.get(0).identifiers != null
          && newScanResult != null
          && newScanResult.beaconsAroundMe != null
          && newScanResult.beaconsAroundMe.get(0) != null
          && newScanResult.beaconsAroundMe.get(0).identifiers != null
          && equals(oldScanResult.beaconsAroundMe, newScanResult.beaconsAroundMe))
        return EBluetoothMovementType.REFINEMENT;
      else if (newScanResult != null
          && newScanResult.beaconsAroundMe != null
          && newScanResult.beaconsAroundMe.get(0) != null
          && newScanResult.beaconsAroundMe.get(0).identifiers != null
          && newScanResult.beaconsAroundMe.get(0).identifiers.get("id0") != null
          && oldScanResult != null
          && oldScanResult.beaconsAroundMe != null
          && oldScanResult.beaconsAroundMe.get(0) != null
          && oldScanResult.beaconsAroundMe.get(0).identifiers != null
          && oldScanResult
              .beaconsAroundMe
              .get(0)
              .identifiers
              .get("id0")
              .equals(newScanResult.beaconsAroundMe.get(0).identifiers.get("id0"))) {
        return EBluetoothMovementType.TOPBEACONCHANGE;
      } else {
        return EBluetoothMovementType.REGIONCHANGE;
      }

    } catch (Exception e) {
      e.printStackTrace();
      return EBluetoothMovementType.REGIONCHANGE;
    }
  }

  private static boolean equals(
      List<BluetoothBeaconInformation> one, List<BluetoothBeaconInformation> two) {
    try {
      if (one == null && two == null) return true;
      if (one != null && one.size() > 0 && (two == null || two.size() == 0)) return false;
      if (two != null && two.size() > 0 && (one == null || one.size() == 0)) return false;

      for (BluetoothBeaconInformation o : one) {
        for (BluetoothBeaconInformation t : two) {
          if (!equalMaps(o.identifiers, t.identifiers)) return false;
        }
      }

      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  private static boolean equalMaps(Map<?, ?> map1, Map<?, ?> map2) {

    if (map1 == null || map2 == null || map1.size() != map2.size()) {
      return false;
    }

    for (Object key : map1.keySet()) {
      if (!map1.get(key).equals(map2.get(key))) {
        return false;
      }
    }
    return true;
  }
}
