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

import com.iunera.generaldatatypes.place.locationdatatypes.ExtendedLocation;
import com.iunera.generaldatatypes.place.locationdatatypes.bluetooth.BluetoothBeaconAggregation;
import com.iunera.generaldatatypes.place.locationdatatypes.bluetooth.BluetoothBeaconInformation;
import com.iunera.generaldatatypes.place.locationdatatypes.wifi.WifiInformation;
import com.iunera.generaldatatypes.place.locationdatatypes.wifi.WifiInformationAggregation;
import com.iunera.generaldatatypes.place.place.EFuzzyPlaceDistance;
import com.iunera.generaldatatypes.place.place.EPlaceOrHappeningSelectionType;
import com.iunera.generaldatatypes.place.place.EPlaceType;
import com.iunera.generaldatatypes.place.place.PlaceEntity;
import java.util.List;

public class PlaceDeterminationStrategy {
  //	private static long lastShown = 0;
  public static final String ACTION_OPENPLACEURL = "com.stappz.action.openPlaceUrl";

  public static SelectedPlaceSuggestions computeBestSuggestPlaces(
      ExtendedLocation scanresult, List<PlaceEntity> nearPlaces) {
    try {
      if (nearPlaces == null || nearPlaces.size() == 0) return null;

      // first do a matching based on the high precison place
      // check the uuid or the bluetooth mac of the place...
      // if there are multiple. choose the one with the nearest distance

      PlaceEntity bestplace = nearPlaces.get(0);

      // first check if we have a bluetooth beacon
      try {
        if (scanresult.bluetoothInformation != null
            && scanresult.bluetoothInformation.beaconsAroundMe != null
            && scanresult.bluetoothInformation.beaconsAroundMe.size() > 0) {
          for (BluetoothBeaconInformation blueinfo :
              scanresult.bluetoothInformation.beaconsAroundMe)
            for (PlaceEntity place : nearPlaces) {

              if (place.type.equals(EPlaceType.HIGHPRECISION_PLACE)
                  && place.locationAggregate.bluetoothInformationAggregation != null
                  && place.locationAggregate.bluetoothInformationAggregation.size() > 0) {
                for (BluetoothBeaconAggregation aggr :
                    place.locationAggregate.bluetoothInformationAggregation) {

                  // out of the same guid and major and minor,
                  // we
                  // use the mac here for demo cases
                  if (aggr.bluetoothAddresses != null)
                    for (String mac : aggr.bluetoothAddresses) {
                      if (blueinfo.bluetoothAddress.equals(mac)) {
                        try {
                          SelectedPlaceSuggestions ret =
                              new SelectedPlaceSuggestions(
                                  EFuzzyPlaceDistance.LIKELY_AT,
                                  EPlaceOrHappeningSelectionType.AUTO_SELECTED_BY_APPLICATION,
                                  place);
                          //	showCheckInNotification(place.name,
                          //			place.onNearNotificationMessage);
                          return ret;
                        } catch (Exception e) {

                        }
                      }
                    } // end of mac test
                  // check region equality
                  /*
                   * if(equalMaps(aggr.regionIdentifiers,blueinfo
                   * .identifiers)){ SelectedPlaceSuggestions
                   * ret = new SelectedPlaceSuggestions(
                   * EFuzzyPlaceDistance.LIKELY_AT,
                   * EPlaceOrHappeningSelectionType.
                   * AUTO_SELECTED_BY_APPLICATION_BLUETOOTH_BEACON
                   * , place);
                   *
                   * return ret; }
                   */
                }
              }
            }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      float bestdistancesofar = -100000;
      // = bestplace.locationAggregate
      //		.shortestDistanceBasedOngeoPoints(scanresult);

      float bistwifiditancesofar = 0; // --> use the distance utils to
      // compute the wifi distance

      // the following loop computes the best suggestion for a place
      // simply explained:
      /*
       * Till 20 m near it tries to find the nearest high precison place
       * if the best place is more than 20 m away till 200 m it will try
       * to use a normal place till 300 m the best place will be a
       * compound more far it will use the coarse area - the reason why
       * all of this is so complicated is that a coarse area may be given
       * by a center that is even nearer to the current location than a
       * normal place- therefore we need to respect this and work around
       * in a complex loop
       */
      for (PlaceEntity place : nearPlaces) {
        // TODO: be aware there may also be a place where you just got
        // wifi
        // also add cell distance
        if (bestplace.locationAggregate == null
            || bestplace.locationAggregate.centers == null
            || place.locationAggregate == null
            || place.locationAggregate.centers == null) {
          // this needs to be checked to be error free in any case -
          // currently we just ignore the places that do not have a
          // location
          // in the future wifi stuff can be used here
          continue;
        }
        float dist = 1000000;
        try {
          dist = place.locationAggregate.shortestDistanceBasedOngeoPoints(scanresult);
        } catch (Exception e) {

        }

        float wifidistance = 0; // --> Compute the wifi distance
        if (place.locationAggregate.wifiInformationAggregation != null
            && place.locationAggregate.wifiInformationAggregation.size() > 0
            && scanresult.wifiInformation != null
            && scanresult.wifiInformation.networksAroundMe != null
            && scanresult.wifiInformation.networksAroundMe.size() > 0) {
          boolean found = false;
          for (WifiInformation wifinfo : scanresult.wifiInformation.networksAroundMe) {

            for (WifiInformationAggregation aggr :
                place.locationAggregate.wifiInformationAggregation) {
              if (aggr.BSSID != null && wifinfo.bssid != null && aggr.BSSID.equals(wifinfo.bssid)) {
                wifidistance++;
                found = true;
                break;
              }
            }
            if (found) {
              found = false;
              continue;
            }
          }
        }

        // TODO: replace the fixed integer values here with a retrival
        // from the fuzzy place distance
        // do not compute high precision places other than bluetooth
        // based what we did priorly
        if (bestplace.type.equals(EPlaceType.HIGHPRECISION_PLACE)) {
          bestplace = place;
          try {
            bestdistancesofar =
                place.locationAggregate.shortestDistanceBasedOngeoPoints(scanresult);

          } catch (Exception e) {
            // the beacon has no long and lat...
            // we set it to 10 meters beacuse it is a high precision place
            bestdistancesofar = 10;
          }
        }
        if (place.type.equals(EPlaceType.HIGHPRECISION_PLACE)) continue;
        /*
         * if (((dist < bestdistancesofar || wifidistance >
         * bestdistancesofar) || ((dist <= 20 || wifidistance > 0) &&
         * (!(bestplace.type .equals(EPlaceType.HIGHPRECISION_PLACE)))))
         *
         * && place.type.equals(EPlaceType.HIGHPRECISION_PLACE)) {
         *
         * // go with the high precison area bestdistancesofar = dist;
         * wifidistance = bistwifiditancesofar; bestplace = place;
         * continue; }
         */ else if (((dist < bestdistancesofar
                    || bestdistancesofar < 0
                    || wifidistance > bistwifiditancesofar)
                || (!(bestplace.type.equals(EPlaceType.NORMAL_PLACE)
                        || bestplace.type.equals(EPlaceType.HIGHPRECISION_PLACE))
                    && (dist <= 50 || (wifidistance > 0 && dist <= 200))))
            && place.type.equals(EPlaceType.NORMAL_PLACE)) {
          // wifidistance > bistwifiditancesofar || (wifidistance ==
          // bistwifiditancesofar && dist < bestdistancesofar)
          // ( &&
          // || dist < bestdistancesofar && (bistwifiditancesofar==0
          // ||
          // go with the high precison area
          bestdistancesofar = dist;
          bistwifiditancesofar = wifidistance;
          bestplace = place;
          continue;
        } else if (((dist < bestdistancesofar
                    || bestdistancesofar < 0
                    || wifidistance > bistwifiditancesofar)
                || (!(bestplace.type.equals(EPlaceType.COMPOUND))
                    || !(bestplace.type.equals(EPlaceType.LARGECOMPOUND))
                    || !(bestplace.type.equals(EPlaceType.NORMAL_PLACE)
                        || bestplace.type.equals(EPlaceType.HIGHPRECISION_PLACE) && dist <= 200)))
            && (place.type.equals(EPlaceType.COMPOUND)
                || place.type.equals(EPlaceType.LARGECOMPOUND))) {

          // go with the high precison area
          bestdistancesofar = dist;
          bistwifiditancesofar = wifidistance;
          bestplace = place;
          continue;
        } else if ((dist < bestdistancesofar
                || bestdistancesofar < 0
                || wifidistance > bistwifiditancesofar)
            || ((bestplace.type.equals(EPlaceType.COARSEAREA)
                    || bestplace.type.equals(EPlaceType.LARGEAREA)))
                && ((bestdistancesofar > 200 && (bestplace.type.equals(EPlaceType.COMPOUND))
                        || (bestplace.type.equals(EPlaceType.LARGECOMPOUND)))
                    || (bestdistancesofar > 50 && (bestplace.type.equals(EPlaceType.NORMAL_PLACE)))
                    || (bestdistancesofar > 20
                        && (bestplace.type.equals(EPlaceType.HIGHPRECISION_PLACE))))
                && (place.type.equals(EPlaceType.COARSEAREA)
                    || place.type.equals(EPlaceType.LARGEAREA))) {

          // go with the high precison area
          bestdistancesofar = dist;
          bistwifiditancesofar = wifidistance;
          bestplace = place;
          continue;
        }
      }

      // if the distance so far is too large, assure it is a coarse area
      // that gets used
      SelectedPlaceSuggestions ret =
          new SelectedPlaceSuggestions(
              DistanceUtils.getFuzzydistanceToPlace(bestplace, scanresult),
              EPlaceOrHappeningSelectionType.AUTO_SELECTED_BY_APPLICATION,
              bestplace);

      return ret;
    } catch (Exception e) {
      // TODO: avoid exceptions if just a wifi position is available
      e.printStackTrace();
      return null;
    }
  }

  // private static boolean equalMaps(Map<?, ?> map1, Map<?, ?> map2) {
  //
  // if (map1 == null || map2 == null || map1.size() != map2.size()) {
  // return false;
  // }
  //
  // for (Object key : map1.keySet()) {
  // if (!map1.get(key).equals(map2.get(key))) {
  // return false;
  // }
  // }
  // return true;
  // }
}
