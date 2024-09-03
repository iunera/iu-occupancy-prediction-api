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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.iunera.generaldatatypes.Tag;
import com.iunera.generaldatatypes.place.locationdatatypes.ExtendedLocation;
import java.util.List;

/** used to save the current selected place from the list by a user */
@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceOrHappeningUsed {
  // use one of the following ids
  public String placeOrHappeningGuid;
  /**
   * the client side guid of the place that the client used before - and the client does not have an
   * offical guid yet
   */
  public String tmpCreationRequestClientSideGUID;

  public boolean isHappening;
  public boolean isPendingPlaceOrHappening;
  public ExtendedLocation currentUserLocation;
  public EPlaceOrHappeningSelectionType placeSelectionType;
  public String usedPlaceName;

  /** The id of the current device that the user uses */
  public String deviceId;

  public List<Tag> tags;
}
