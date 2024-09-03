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
import com.iunera.generaldatatypes.place.place.EFuzzyPlaceDistance;
import com.iunera.generaldatatypes.place.place.PlaceEntity;
import java.util.Comparator;

/**
 * This class is used to sort places via distance or to compute the likely distance to a place The
 * main reason why this class is not in the placeentity class is that we sometimes need to access
 * cached data in the client Imageine we want to compute the most likely distance to a place and
 * credit
 */
public class DistanceUtils {

  public static class FuzzifiedDistance {
    public int distanceMeasure;
    public String distanceString;
  }

  public static FuzzifiedDistance fuzzyDistanceTo(ExtendedLocation loc, ExtendedLocation loc2) {
    float dist = loc2.distanceTo(loc);
    FuzzifiedDistance fd = new FuzzifiedDistance();
    // accuracy--> los1.accucray
    float thisaccuracy = loc2.getAccuracy();
    float otheraccuracy = loc.getAccuracy();
    Math.abs(dist - thisaccuracy - otheraccuracy);
    if (dist < 10 && thisaccuracy < 65 && otheraccuracy < 65) {
      fd.distanceMeasure = 0;
      fd.distanceString = "<10M";
    } else if (dist < 65 && thisaccuracy < 65 && otheraccuracy < 65) {
      fd.distanceMeasure = 50;
      fd.distanceString = "<50M";
    } else if (dist < 115 && thisaccuracy < 65 && otheraccuracy < 65) {
      fd.distanceMeasure = 100;
      fd.distanceString = "<100M";
    } else if (dist < 220 && thisaccuracy < 95 && otheraccuracy < 95) {
      fd.distanceMeasure = 200;
      fd.distanceString = "<200M";
    } else if (dist < 600 && thisaccuracy < 200 && otheraccuracy < 200) {
      fd.distanceMeasure = 500;
      fd.distanceString = "~500M";
    } else if (dist < 1200 && thisaccuracy < 500 && otheraccuracy < 500) {
      fd.distanceMeasure = 1000;
      fd.distanceString = "~1KM";
    } else if (dist < 2300 && thisaccuracy < 800 && otheraccuracy < 800) {
      fd.distanceMeasure = 2000;
      fd.distanceString = "~2KM";
    } else if (dist < 5500 && thisaccuracy < 800 && otheraccuracy < 800) {
      fd.distanceMeasure = 5000;
      fd.distanceString = "~5KM";
    } else if (dist < 12000 && thisaccuracy < 3000 && otheraccuracy < 3000) {
      fd.distanceMeasure = 10000;
      fd.distanceString = "<10KM";
    } else if (dist < 21000 && thisaccuracy < 3000 && otheraccuracy < 3000) {
      fd.distanceMeasure = 20000;
      fd.distanceString = "<20KM";
    } else if (dist > 21000) {

      fd.distanceMeasure = Math.round(dist);
      int intdist = Math.round(dist / 1000);
      fd.distanceString = "~" + Integer.toString(intdist) + "KM";
    } else {
      fd.distanceString = "N/A";
      fd.distanceMeasure = 20000;
    }
    return fd;
  }

  public static class PlaceVortexDistanceComparator implements Comparator<PlaceEntity> {

    private ExtendedLocation locinfo;

    /** the point of origin according to which the distances get sorted */
    public PlaceVortexDistanceComparator(ExtendedLocation locinfo) {
      this.locinfo = locinfo;
    }

    @Override
    public int compare(PlaceEntity lhs, PlaceEntity rhs) {
      try {
        Integer btdist1 = lhs.locationAggregate.bluetoothSimilaritySignature(locinfo);
        Integer btdist2 = rhs.locationAggregate.bluetoothSimilaritySignature(locinfo);

        if (!btdist1.equals(0) && !btdist2.equals(0)) {
          Integer rssilhs = lhs.locationAggregate.bluetoothDistanceMax(locinfo);
          Integer rssirhs = rhs.locationAggregate.bluetoothDistanceMax(locinfo);
          return rssirhs.compareTo(rssilhs);
        } else if (!btdist1.equals(btdist2)) return btdist2.compareTo(btdist1);

        Float dist1 =
            locinfo.geoPointLocations.get(0).distanceTo(lhs.locationAggregate.centers.get(0));
        Float dist2 =
            locinfo.geoPointLocations.get(0).distanceTo(rhs.locationAggregate.centers.get(0));
        return dist1.compareTo(dist2);
      } catch (Exception e) {

        return Integer.MIN_VALUE;
        // TODO: if(lhs.locationAggregate.center)--> be aware not all
        // locations may have no coordinate
      }
    }
  }

  /**
   * method used to compute a wifi similarity score out of the given reveal location compared to the
   * wifi aggregate of the place currently computed in %
   */
  public float getWifiSignatureSimilarityScore(ExtendedLocation location) {
    return 0;
  }

  public float cellSignatureSimilarityScore(ExtendedLocation location) {
    return 0;
  }

  public static EFuzzyPlaceDistance getFuzzydistanceToPlace(
      PlaceEntity place, ExtendedLocation location) {
    // used to allow bt beacons without a geopoint
    float geodistancesofar = 9;
    try {
      geodistancesofar =
          location.geoPointLocations.get(0).distanceTo(place.locationAggregate.centers.get(0));
    } catch (Exception e) {

    }
    float wifisimilarityscore = 0;
    if (geodistancesofar < 10 || wifisimilarityscore > 80) {
      switch (place.type) {
        case HIGHPRECISION_PLACE:
          {
            return EFuzzyPlaceDistance.LIKELY_AT;
          }

        case NORMAL_PLACE:
          {
            return EFuzzyPlaceDistance.LIKELY_AT;
          }

        case COMPOUND:
          {
            return EFuzzyPlaceDistance.LIKELY_AT;
          }

        case LARGECOMPOUND:
          {
            return EFuzzyPlaceDistance.LIKELY_AT;
          }

        case COARSEAREA:
          {
            return EFuzzyPlaceDistance.IN_AROUNDAREA;
          }

        case LARGEAREA:
          {
            return EFuzzyPlaceDistance.IN_HUGE_AREA;
          }

        default:
          throw new IllegalStateException("new place type reqires refinement of this logic");
      }
    }
    if (geodistancesofar < 50) {
      switch (place.type) {
        case HIGHPRECISION_PLACE:
          {
            return EFuzzyPlaceDistance.REALLY_NEAR;
          }

        case NORMAL_PLACE:
          {
            return EFuzzyPlaceDistance.REALLY_NEAR;
          }

        case COMPOUND:
          {
            return EFuzzyPlaceDistance.LIKELY_AT;
          }

        case LARGECOMPOUND:
          {
            return EFuzzyPlaceDistance.LIKELY_AT;
          }

        case COARSEAREA:
          {
            return EFuzzyPlaceDistance.IN_AROUNDAREA;
          }

        case LARGEAREA:
          {
            return EFuzzyPlaceDistance.IN_AROUNDAREA;
          }

        default:
          throw new IllegalStateException("new place type reqires refinement of this logic");
      }
    }

    if (geodistancesofar < 120) {
      switch (place.type) {
        case HIGHPRECISION_PLACE:
          {
            return EFuzzyPlaceDistance.NEAR_100;
          }
        case NORMAL_PLACE:
          {
            return EFuzzyPlaceDistance.NEAR_100;
          }

        case COMPOUND:
          {
            return EFuzzyPlaceDistance.NEAR_100;
          }

        case LARGECOMPOUND:
          {
            return EFuzzyPlaceDistance.NEAR_100;
          }

        case COARSEAREA:
          {
            return EFuzzyPlaceDistance.IN_AROUNDAREA;
          }

        case LARGEAREA:
          {
            return EFuzzyPlaceDistance.IN_HUGE_AREA;
          }

        default:
          throw new IllegalStateException("new place type reqires refinement of this logic");
      }
    }

    if (geodistancesofar < 220) {
      switch (place.type) {
        case HIGHPRECISION_PLACE:
          {
            return EFuzzyPlaceDistance.NEAR;
          }
        case NORMAL_PLACE:
          {
            return EFuzzyPlaceDistance.NEAR;
          }

        case COMPOUND:
          {
            return EFuzzyPlaceDistance.NEAR;
          }

        case LARGECOMPOUND:
          {
            return EFuzzyPlaceDistance.NEAR;
          }

        case COARSEAREA:
          {
            return EFuzzyPlaceDistance.IN_AROUNDAREA;
          }

        case LARGEAREA:
          {
            return EFuzzyPlaceDistance.IN_HUGE_AREA;
          }

        default:
          throw new IllegalStateException("new place type reqires refinement of this logic");
      }
    }
    if (geodistancesofar < 500) {
      switch (place.type) {
        case HIGHPRECISION_PLACE:
          {
            return EFuzzyPlaceDistance.AROUND;
          }
        case NORMAL_PLACE:
          {
            return EFuzzyPlaceDistance.AROUND;
          }

        case COMPOUND:
          {
            return EFuzzyPlaceDistance.AROUND;
          }

        case LARGECOMPOUND:
          {
            return EFuzzyPlaceDistance.AROUND;
          }

        case COARSEAREA:
          {
            return EFuzzyPlaceDistance.IN_AROUNDAREA;
          }

        case LARGEAREA:
          {
            return EFuzzyPlaceDistance.IN_HUGE_AREA;
          }

        default:
          throw new IllegalStateException("new place type reqires refinement of this logic");
      }
    }

    if (geodistancesofar < 1000) {
      return EFuzzyPlaceDistance.A_KILOMETER_AWAY;
    }

    if (geodistancesofar < 2000) {
      return EFuzzyPlaceDistance.MULTIPLE_KM_AWAY_AROUND_OR_LESS_2KM;
    }

    if (geodistancesofar < 5000) {
      return EFuzzyPlaceDistance.MULTIPLE_KM_AWAY_AROUND_OR_LESS_5KM;
    }
    if (geodistancesofar < 10000) {
      return EFuzzyPlaceDistance.MULTIPLE_KM_AWAY_AROUND_OR_LESS_10KM;
    }
    if (geodistancesofar < 20000) {
      return EFuzzyPlaceDistance.MULTIPLE_KM_AWAY_AROUND_OR_LESS_20KM;
    }

    return EFuzzyPlaceDistance.NA;
  }
}
