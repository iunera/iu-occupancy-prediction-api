package com.iunera.publictransport.occupation.historicdata;

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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.iunera.publictransport.departure.model.EPerceivedOccupation;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(Include.NON_NULL)
public class OccupationForecastResultRow {

  public String stop_label;
  public String line_name;
  public String line_label;
  public String line_geoKeys;
  public String line_direction;
  public Integer i_sum_vehicle_seats;
  public Integer i_sum_vehicle_possibleStandingPassengers;
  public String dayMeta_dailyRideStopKey;

  public Integer i_sum_entries;
  public Integer i_sum_occupationDeparture;
  public Integer i_sum_occupationArrival;
  public String line_transportProduct;
  public Integer i_sum_vehicle_possibleStandingPassengers_max;
  public Integer i_sum_vehicle_seats_max;

  public Double sum_i_sum_occupationDeparture;

  public String meta_dataOrigin;
  public String overview_departure_time;

  public String stop_geoKeys;
  public Integer dayMeta_zonedHour;

  @JsonProperty("coordinates")
  public String coordinates;

  public Integer sketch;

  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  public List<Double> performance_sketch = null;

  public EPerceivedOccupation occupancy = EPerceivedOccupation.UNKNOWN;
}
