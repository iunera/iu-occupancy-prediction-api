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

/** This enum specifies how a place gets used and selected */
public enum EPlaceOrHappeningSelectionType {
  // AUTO_SELECTED_BY_APPLICATION_BLUETOOTH_BEACON,
  /**
   * the place was just shown by the application and selected by it- no posting action nothing
   * happened
   */
  AUTO_SELECTED_BY_APPLICATION,
  /** the place was selected automatically by the reveal client when a post was done */
  AUTO_SELECTED_BY_POSTING,
  /** the user approved the selected the best place suggestion */
  USER_APPROVED_SUGGESTED_PLACE,
  /** the user did not post but selected a */
  USER_SELECTED_FROM_LIST_WITHOUT_POSTING,
  /** the user posted something and used a popup place suggestion */
  USER_SELECTED_BY_POSTING,
  /** used entered a new place without posting */
  USER_ENTERED_NEW_PLACE
}
