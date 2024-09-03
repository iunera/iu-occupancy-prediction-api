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

import java.time.Instant;

public class DepartureSimplifiedDTO {
  public String destination;
  public String line;
  public Instant departure;

  public DepartureSimplifiedDTO(String destination, String line, Instant departure, int number) {
    super();
    this.destination = destination;
    this.line = line;
    this.departure = departure;
    this.occupation = number;
  }

  public int occupation;

  public double[] geoposition;
}
