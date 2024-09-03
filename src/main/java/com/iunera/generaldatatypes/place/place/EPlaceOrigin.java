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

public enum EPlaceOrigin {
  /** the is out of the reveal place proposal database and exists in it */
  STAPPZDB,
  /** it is an osm place - origin overpass - rename to overpass */
  OSM_OVERPASS,
  OSM_NOMINATIM,
  /** the user defined the place himself */
  USER_SPECIFIED // ,
  /** a place that got created that has a guid but is not a complete approved place yet */
  ,
  USER_SPECIFIED_IN_WEB,
  SPECIFIED_BY_INTERNAL_API,
  EXTERNAL_API_KVV,
  EXTERNAL_API_EFA
  // PENDING_STAPPZ_PLACES

}
