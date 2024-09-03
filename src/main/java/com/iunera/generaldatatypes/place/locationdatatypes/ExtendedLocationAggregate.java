package com.iunera.generaldatatypes.place.locationdatatypes;

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
import com.iunera.generaldatatypes.place.locationdatatypes.bluetooth.BluetoothBeaconAggregation;
import com.iunera.generaldatatypes.place.locationdatatypes.bluetooth.BluetoothBeaconInformation;
import com.iunera.generaldatatypes.place.locationdatatypes.cell.CellInfo;
import com.iunera.generaldatatypes.place.locationdatatypes.cell.CellInfoAggregation;
import com.iunera.generaldatatypes.place.locationdatatypes.wifi.WifiInformation;
import com.iunera.generaldatatypes.place.locationdatatypes.wifi.WifiInformationAggregation;
import java.util.ArrayList;
import java.util.List;
import org.geojson.geometry.Point;

// a location aggregate for a place- this means a precision that is atequate for
// a place
// static in the way that the location cannot move on earth
// fixed- the palce is fixed no car no ship no plane, no train- a fixed place
// not moving
@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ExtendedLocationAggregate {

  public ExtendedLocationAggregate() {
    cellInfoAggregation = new ArrayList<CellInfoAggregation>();
    wifiInformationAggregation = new ArrayList<WifiInformationAggregation>();
    centers = new ArrayList<Point>(3);
  }

  public List<CellInfoAggregation> cellInfoAggregation;
  public List<CellInfoAggregation> neighbourCellAggregates;
  public List<WifiInformationAggregation> wifiInformationAggregation;

  public List<BluetoothBeaconAggregation> bluetoothInformationAggregation;
  // public PointAggregation geoPositionPointAggregation;

  // look this up- because we need valid geojson here that mongodb can index
  // it - this is wrong- replace it with a valid geojson index
  // that we can have polygons https://docs.mongodb.com/manual/core/2dsphere/
  public List<Point> centers;

  /**
   * computes a similarity score of the current and given wifi signature - 0 means nothing is the
   * same- the higher the more similar currently, it measures just the same wifis - later it will
   * also use the signal strengh
   */
  public int wifiSimilaritySignature(ExtendedLocation location) {
    if (wifiInformationAggregation == null
        || wifiInformationAggregation.size() == 0
        || location.wifiInformation == null
        || location.wifiInformation.networksAroundMe == null
        || location.wifiInformation.networksAroundMe.size() == 0) return 0;
    int similarity = 0;
    for (WifiInformationAggregation wifi : wifiInformationAggregation) {
      for (WifiInformation clientwifi : location.wifiInformation.networksAroundMe) {
        if (clientwifi.bssid.equals(wifi.BSSID)) {
          similarity++;
          continue;
        }
      }
    }
    return similarity;
  }

  /** the higherthe value the better */
  public int bluetoothDistanceMax(ExtendedLocation location) {
    if (bluetoothInformationAggregation == null
        || bluetoothInformationAggregation.size() == 0
        || location.bluetoothInformation == null
        || location.bluetoothInformation.beaconsAroundMe == null
        || location.bluetoothInformation.beaconsAroundMe.size() == 0) return 0;
    int similarity = 0;
    for (BluetoothBeaconAggregation beaconaggr : bluetoothInformationAggregation) {
      for (BluetoothBeaconInformation beacon : location.bluetoothInformation.beaconsAroundMe) {
        if (beaconaggr.bluetoothAddresses != null && beaconaggr.bluetoothAddresses.size() > 0)
          for (String mac : beaconaggr.bluetoothAddresses)
            if (mac.equals(beacon.bluetoothAddress)) {
              similarity = similarity + beacon.rssi;
              continue;
            }
      }
    }
    return similarity;
  }

  public int bluetoothSimilaritySignature(ExtendedLocation location) {
    if (bluetoothInformationAggregation == null
        || bluetoothInformationAggregation.size() == 0
        || location.bluetoothInformation == null
        || location.bluetoothInformation.beaconsAroundMe == null
        || location.bluetoothInformation.beaconsAroundMe.size() == 0) return 0;
    int similarity = 0;
    for (BluetoothBeaconAggregation beaconaggr : bluetoothInformationAggregation) {
      for (BluetoothBeaconInformation beacon : location.bluetoothInformation.beaconsAroundMe) {
        if (beaconaggr.bluetoothAddresses != null && beaconaggr.bluetoothAddresses.size() > 0)
          for (String mac : beaconaggr.bluetoothAddresses)
            if (mac.equals(beacon.bluetoothAddress)) {
              similarity++;
              continue;
            }
      }
    }
    return similarity;
  }

  /** computes the shortest distance if geopositions are available */
  public float shortestDistanceBasedOngeoPoints(ExtendedLocation location) {
    if (location.geoPointLocations == null
        || location.geoPointLocations.size() == 0
        || this.centers == null
        || this.centers.size() == 0) throw new IllegalArgumentException("no positions available");

    float shortestDistance = -1;
    for (Point p : centers) {
      for (Point loc : location.geoPointLocations) {
        float tmp = p.distanceTo(loc);
        if (tmp < shortestDistance || shortestDistance < 0) shortestDistance = tmp;
      }
    }
    if (shortestDistance < 0) throw new IllegalArgumentException("no positions available");
    return shortestDistance;
  }

  /**
   * computes the cell similarity, the high the better similarity if the same cell is found it is
   * rated as 5 if a nighbording cell is the same it is counted as one --> if the score is above 5
   * you can be certain the client is in the same cell if it is above 0 it means a small similarity
   * exists TODO: add some normalisatiion parameter based on the amount of found cells
   */
  public int cellSimilaritySignature(ExtendedLocation location) {
    if (cellInfoAggregation == null
        || cellInfoAggregation.size() == 0
        || location.cellInformation == null) return 0;
    int similarity = 0;
    for (CellInfoAggregation cellinfo : cellInfoAggregation) {
      if (cellinfo.cid == location.cellInformation.cid) {
        similarity = similarity + 5;
        continue;
      }
    }

    // now check the similarity to the nighbor cells
    if (location.cellInformation.neighbourCellInfos != null
        && location.cellInformation.neighbourCellInfos.size() > 0
        && this.neighbourCellAggregates != null
        && this.neighbourCellAggregates.size() > 0) {

      for (CellInfo cellinfo : location.cellInformation.neighbourCellInfos) {
        // check if a neighbor is one of the main cells
        for (CellInfoAggregation cellinfo2 : cellInfoAggregation) {
          if (cellinfo2.cid == cellinfo.cid) {
            similarity = similarity + 1;
            break;
          }
        }
        // check if a nighobor cell is one of the neighbor cells
        for (CellInfoAggregation cellinfo2 : neighbourCellAggregates) {
          if (cellinfo2.cid == cellinfo.cid) {
            similarity = similarity + 1;
            break;
          }
        }
      }
    }
    return similarity;
  }
}
