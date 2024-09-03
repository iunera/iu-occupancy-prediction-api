package com.iunera.generaldatatypes.place.place;

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

/** specifies the likely distance to the place */
@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
public enum EFuzzyPlaceDistance {
  /**
   * The user specified that he is at the place. He selected the place manually or approved it
   * He/she is in a bar, @ home and so on
   */
  AT_APPROVED,
  /**
   * It is likely that the user is at a place or at the same place like another user from all the
   * data that we know. However, we are not certain completely about this < 10 m - in a bar or so -
   * we can say someone is in there and another guy is at the same location
   */
  LIKELY_AT,

  /** <50 m */
  // TODO:rename to beside or next to
  REALLY_NEAR,
  /** around 100 m near me */
  NEAR_100,
  /** Our data indicates that the user is near a place distance <200m */
  NEAR,

  /**
   * <500 - 600 indicates that the user is about 500 m away from a place - likely only used for
   * distances between a user an other posts around him
   */
  AROUND,
  /**
   * Our data indicates that the user is in an around area - same like at a place but just in an
   * area
   */
  IN_AROUNDAREA,

  /** around kilometer for a place or an aroundarea away */
  A_KILOMETER_AWAY,

  /** the user is near an area or a location but multiple km aways from it */
  MULTIPLE_KM_AWAY_AROUND_OR_LESS_2KM,
  MULTIPLE_KM_AWAY_AROUND_OR_LESS_5KM,
  MULTIPLE_KM_AWAY_AROUND_OR_LESS_10KM,
  MULTIPLE_KM_AWAY_AROUND_OR_LESS_20KM

  /** in a huge area like a county or so */
  ,
  IN_HUGE_AREA
  /** the distance is not known */
  ,
  NA;
}
