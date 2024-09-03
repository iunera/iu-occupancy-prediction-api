package com.iunera.publictransport.departure.model;

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
import com.iunera.publictransport.domain.TransportProductDTO;

@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineDTO {
  public LineDTO() {}

  public LineDTO(
      String providerKey,
      String network,
      TransportProductDTO transportProductDTO,
      String label,
      String name) {
    this.providerKey = providerKey;
    this.network = network;
    this.transportProduct = transportProductDTO;
    this.label = label;
    this.name = name;
  }

  /** The original unique id the line has from the provider */
  public String providerKey;

  public String network;
  public TransportProductDTO transportProduct;
  /** the label of the line that might include the means of transport e.g. BUS 1337 */
  public String label;
  /** The name of the line like 1337 */
  public String name;

  public String geoKey;
}
