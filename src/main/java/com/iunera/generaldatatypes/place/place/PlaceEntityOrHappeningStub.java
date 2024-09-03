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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * A stub class of a place entity. This maybe used as mini version of the place entity May it be to
 * refer to a maybe not yet existing entity or to just have a minimal version of the place entity as
 * reference Hence the id of the place entity can be null. However, the name has to be filled for
 * certain
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonPropertyOrder(alphabetic = true)
public class PlaceEntityOrHappeningStub {

  /** The used name of the place */
  public String usedName;

  /** the guid of the associated place entity- can be null */
  // this id is not known in all cases- because if there is a new place
  // creation request this is null
  // then the server needs to set this
  // however - how can the client access this then? when he browses the
  // worldannotation
  // how does the client get to know the guid when the sever inserts it?
  public boolean hasGuid() {
    if (guid == null || guid.equals("")) return false;
    return true;
  }

  public String guid;

  /**
   * flag used to indicate weather the usedName and the guid reference refers to a placeenity or a
   * Happening
   */
  public boolean isHappening;
  /*
   * all elements that carry this guid have to be updated to a real guid in
   * case the placenity gets created only needed if we have no real guid
   * available- in case the real guid is avilable this field is set to null if
   * you do not have a guid this guid needs to be set
   */
  public String creationRequestClientSideGUID;

  /** from where did the place come? if it is resolved to a real place, it will be */
  public EPlaceOrigin dataSource;

  public boolean isValid() {
    if ((guid == null || guid.equals(""))
        && (creationRequestClientSideGUID == null || creationRequestClientSideGUID.equals("")))
      return false;
    return true;
  }
}
