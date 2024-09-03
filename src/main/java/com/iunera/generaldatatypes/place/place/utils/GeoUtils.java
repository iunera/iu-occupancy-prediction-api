package com.iunera.generaldatatypes.place.place.utils;

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

public class GeoUtils {

  /**
   * returns a Point which is n- Meters (distance in meters) aways from the given Point. The bearing
   * is 0. Use it if you want to change a geocoord.
   *
   * <p>Bearing ist der winkel der richtung. 0 in den norden, 90 in den osten (uhrzeigersinn)
   *
   * @param double[0] the lon
   * @param double[1] the lat
   * @param radius in meters
   */
  public static double[] calcEndPoint(double[] center, int distance, double bearing) {

    double[] gp = new double[2];

    double R = 6371000; // meters , earth Radius approx
    double PI = 3.1415926535;
    double RADIANS = PI / 180;
    double DEGREES = 180 / PI;

    double lat2;
    double lon2;

    double lat1 = center[1] * RADIANS;
    double lon1 = center[0] * RADIANS;
    double radbear = bearing * RADIANS;

    lat2 =
        Math.asin(
            Math.sin(lat1) * Math.cos(distance / R)
                + Math.cos(lat1) * Math.sin(distance / R) * Math.cos(radbear));
    lon2 =
        lon1
            + Math.atan2(
                Math.sin(radbear) * Math.sin(distance / R) * Math.cos(lat1),
                Math.cos(distance / R) - Math.sin(lat1) * Math.sin(lat2));

    gp[0] = lon2 * DEGREES;
    gp[1] = lat2 * DEGREES;

    return (gp);
  }

  /** Returns double[0] = westernLon double[1] = northernLat */
  public static double[] getBBPointNorthWestern(double[] center, int distance) {
    return calcEndPoint(center, distance, -45);
  }

  /** Returns double[0] = easternLon double[1] = southernLat */
  public static double[] getBBSouthEastern(double[] center, int distance) {
    return calcEndPoint(center, distance, 135);
  }

  /**
   * Due to commodity reasons we allow an non-exact radius query here. Mean the boundingbox fits
   * inside the radius circle and furthermore that we loose the data where the radius overlapse the
   * boundingbox. TODO Return needs to be implemented
   */
  public static void pointToBoundingBox(double lat, double lon, float radius) {
    double center[] = {lon, lat};

    double[] se = GeoUtils.getBBSouthEastern(center, (int) radius);
    double easternLon = se[0];
    double southernLat = se[1];

    double[] nw = GeoUtils.getBBPointNorthWestern(center, (int) radius);
    double westernLon = nw[0];
    double northernLat = nw[1];

    // TODO Return the results in a Boundingbox object

  }
}
