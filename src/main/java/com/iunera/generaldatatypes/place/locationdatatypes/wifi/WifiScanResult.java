package com.iunera.generaldatatypes.place.locationdatatypes.wifi;

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
import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(alphabetic = true)
public class WifiScanResult {
  public WifiScanResult() {}

  public long timeStamp;

  // includes the maybe connected one
  public List<WifiInformation> networksAroundMe = new ArrayList<WifiInformation>();

  public List<WifiInformation> getconnectedNetworks() {
    if (networksAroundMe == null) return null;

    List<WifiInformation> connectedNetworks = new ArrayList<WifiInformation>();

    for (WifiInformation connwifi : networksAroundMe) {
      if (connwifi.isConnected) connectedNetworks.add(connwifi);
    }
    if (connectedNetworks.size() == 0) return null;
    return connectedNetworks;
  }
}
