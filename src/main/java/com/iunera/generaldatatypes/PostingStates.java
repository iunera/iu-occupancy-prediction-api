package com.iunera.generaldatatypes;

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

public enum PostingStates {
  /** The worldannotation is going to be uploaded in the background */
  UPLOAD_SCHEDULED,
  /** The world annotation could not be posted - there occured a problem */
  FAILED,
  /** The world annotation was successfully posted - the same like created */
  POSTED,
  /** It's in the que to be posted. Most likely we are waiting for the picture upload */
  POSTED_PENDING,
  /** The next state after pending - a pending post has been done */
  POSTED_DELAYED,
  /**
   * an accepted post delete or anaything else failed at a later time and gets delivered to the
   * client asyncronous
   */
  FAILED_DELAYED,
  /** The resource does not exist */
  NOT_EXIST,
  /**
   * the resource got deleted if a delete is request- the success of a delete in case a delete fails
   * there may come a failed and so on
   */
  DELETED
}
