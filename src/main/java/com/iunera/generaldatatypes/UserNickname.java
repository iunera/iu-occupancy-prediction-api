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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;

@JsonPropertyOrder(alphabetic = true)
@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserNickname implements Serializable {

  public String nickName;

  /**
   * a generated number we increase with the count of double Usernicknames e.g. Chris Chris1 ..
   * Chris123
   */
  public String uniqueNickName;

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }

    if (!(other instanceof UserNickname) || other == null) return false;

    UserNickname userNicknameother = (UserNickname) other;

    if (((this.nickName != null
                && userNicknameother.nickName != null
                && userNicknameother.nickName.equals(this.nickName))
            || (this.nickName == null && userNicknameother.nickName == null))
        && ((this.uniqueNickName != null
                && userNicknameother.uniqueNickName != null
                && userNicknameother.uniqueNickName.equals(this.uniqueNickName))
            || (this.uniqueNickName == null && userNicknameother.uniqueNickName == null)))
      return true;

    return false;
  }
}
