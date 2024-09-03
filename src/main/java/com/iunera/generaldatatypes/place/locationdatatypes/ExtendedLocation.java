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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.iunera.generaldatatypes.place.locationdatatypes.bluetooth.BluetoothBeaconScanResult;
import com.iunera.generaldatatypes.place.locationdatatypes.cell.CellInfo;
import com.iunera.generaldatatypes.place.locationdatatypes.wifi.WifiScanResult;
import java.util.ArrayList;
import java.util.List;
import org.geojson.geometry.Point;

/**
 * An extended location that can carry multiple different location information. It's what we at
 * reveal understand as location- a fused combination of data out of different providers and
 * scanners This makes the location extendable and the world better. We call it reveal location
 * because the google location class that gets used in android is called "Location".
 */
@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(Include.NON_NULL)
public final class ExtendedLocation {
  public ExtendedLocation() {}

  public ExtendedLocation(Point p) {
    geoPointLocations = new ArrayList<>(1);
    geoPointLocations.add(p);
  }

  private static float DEFAULT_MAX_PRECISION = 8000;
  // a map gets used that we can use locations out of multiple origins
  // we can have a location out of the gps and another one out of google at
  // the same time
  // we need this for compliance reasons, because it maybe forbidden to store
  // the google network provider locations in our database
  // at the same time, this locations maybe allowed to be used on the client
  // in order to take advantage of this fact we allow stroing multiple
  // locations with the provider origin
  public List<Point> geoPointLocations;

  // 1-10 m; 10- 50; 50-100; 100-500;
  /** Indoors ~ 30 - outdoors 100 m - standart n etwas mehr */
  public WifiScanResult wifiInformation;

  public BluetoothBeaconScanResult bluetoothInformation;

  /**
   * In rural areas: 2-8km - upper boundary is 10 km, because of the signal runtime In cities: 200m-
   * 500m.
   */
  public CellInfo cellInformation;

  public String address;

  public Point getBestPoint() {
    if (geoPointLocations != null && geoPointLocations.size() > 0) {
      Point bestpoint = geoPointLocations.get(0);
      for (Point point : geoPointLocations) {
        if (bestpoint.precisionInMeters < point.precisionInMeters) bestpoint = point;
      }
      return bestpoint;
    }
    return null;
  }

  public boolean containsAtLeastOnePoint() {
    if (geoPointLocations != null && geoPointLocations != null && geoPointLocations.size() > 0)
      return true;
    else return false;
  }

  public boolean containsAtLeastOneWifiInfo() {
    if (wifiInformation != null
        && wifiInformation != null
        && wifiInformation.networksAroundMe != null
        && wifiInformation.networksAroundMe.size() > 0) return true;
    else return false;
  }

  public boolean containsAtLeastOneBTInfo() {
    if (bluetoothInformation != null
        && bluetoothInformation.beaconsAroundMe != null
        && bluetoothInformation.beaconsAroundMe.size() > 0) return true;
    else return false;
  }

  // TODO: also check later if it is the same wifi and so on...
  /** returns the distance in kilometers */
  public float distanceTo(ExtendedLocation loc) {
    if (geoPointLocations != null && geoPointLocations.size() > 0) {
      Point bestpoint = geoPointLocations.get(0);
      for (Point point : geoPointLocations) {
        if (bestpoint.precisionInMeters < point.precisionInMeters) bestpoint = point;
      }
      if (geoPointLocations != null
          && loc.geoPointLocations != null
          && loc.geoPointLocations.size() > 0) {
        Point bestpoint2 = loc.geoPointLocations.get(0);
        for (Point point : loc.geoPointLocations) {
          if (bestpoint2.precisionInMeters < point.precisionInMeters) bestpoint2 = point;
        }
        return (float)
            distance(
                bestpoint.coordinates[1],
                bestpoint.coordinates[0],
                bestpoint2.coordinates[1],
                bestpoint2.coordinates[0],
                'M');
      }
      // no two valid points
      return 10000;
    }
    return 100000;
  }

  private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
    double theta = lon1 - lon2;
    double dist =
        Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
            + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;
    if (unit == 'M') {
      dist = dist * 1.609344 * 1000;
    } else if (unit == 'N') {
      dist = dist * 0.8684;
    }
    return (dist);
  }

  public float getAccuracy() {
    if (geoPointLocations != null && geoPointLocations.size() > 0) {
      Point bestpoint = geoPointLocations.get(0);
      for (Point point : geoPointLocations) {
        if (bestpoint.precisionInMeters < point.precisionInMeters) bestpoint = point;
      }
      return bestpoint.precisionInMeters;
    }
    // no long lat available
    return DEFAULT_MAX_PRECISION;
  }

  /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
  /* :: This function converts decimal degrees to radians : */
  /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */

  private double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
  }

  /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
  /* :: This function converts radians to decimal degrees : */
  /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */

  private double rad2deg(double rad) {
    return (rad * 180 / Math.PI);
  }
}
