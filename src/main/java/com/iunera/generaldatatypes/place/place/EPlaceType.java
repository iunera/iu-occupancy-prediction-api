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

public enum EPlaceType {

  // a place that is defined within a few meters, like a table in a
  // restaurant, a shelf in supermarket
  // or a room in a house
  // auto -selection is done based on:
  // user entered information, barcodes, bluetooth beacons,
  // earth magnetic field (heise article)
  HIGHPRECISION_PLACE,

  // a place, something like a small store, a house where a familiy is living,
  // a bar
  // more or less how far a wifi signal goes in normal buildings and around
  // them
  // ~ 40 m precision-
  // it also can be a floor of a large hotel or something similar
  // selection is done based on: wifi data, long/lat, bluetooth beacons,
  // cellid
  NORMAL_PLACE,

  // a larger facility, just imagine a large shop or a hotel with multiple
  // floors
  // selection is done based on: wifi data, long/lat, bluetooth beacons,
  // cellid
  COMPOUND,

  // something like the "Ettlinger tor center" in Karlsruhe. We have multiple
  // small places (small shops) and compounds(multi-floor shops) in it
  // selection is done based on: wifi data, long/lat, bluetooth beacons,
  // cellid
  LARGECOMPOUND,

  // selection is based on georgprahic boundaries or cell ids
  // selection based on cellid and all the contained wifs
  // coarse area maybe included geographically by other coarse areas
  COARSEAREA,
  // a really large area where the selection is do obvious that is done via
  // the mobile provider or so
  // county or so
  LARGEAREA
}
