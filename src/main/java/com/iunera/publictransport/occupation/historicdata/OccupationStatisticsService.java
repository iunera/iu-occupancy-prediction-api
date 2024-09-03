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

import com.iunera.druid.DruidDataRepository;
import com.iunera.publictransport.departure.SimpleDepartureAPI;
import com.iunera.publictransport.departure.model.EPerceivedOccupation;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/** Service use dto provide occupation statistics */
@Service
public class OccupationStatisticsService {

  @Autowired private DruidDataRepository druidDataRepository;

  private static int bucketSize = 2;

  /**
   * Helper method to transform the contents of a resource to a string
   *
   * @param res the resource to be transformed
   * @return the content of the resource
   */
  @SuppressWarnings("resource")
  private static String getResourceContentsAsString(Resource res) throws IOException {
    InputStream inputStream = res.getInputStream();
    try {
      return new Scanner(inputStream, "utf-8").useDelimiter("\\Z").next();
    } finally {
      inputStream.close();
    }
  }

  private String setQueryOccupationForecast;
  private String setQueryOccupationOverview;
  private String setQueryDepartureOverview;

  @Value(value = "classpath:druid/query-occupation-forecast.json")
  private void setQueryOccupationForecast(Resource aggregationQueryFile) throws IOException {
    setQueryOccupationForecast = getResourceContentsAsString(aggregationQueryFile);

    //		getCurrentNearOccupations(null);
  }

  @Value(value = "classpath:druid/query-occupation-overview.json")
  private void setQueryOccupationOverview(Resource aggregationQueryFile) throws IOException {
    setQueryOccupationOverview = getResourceContentsAsString(aggregationQueryFile);
  }

  @Value(value = "classpath:druid/query-departure-overview.json")
  private void setQueryDepartureOverview(Resource aggregationQueryFile) throws IOException {
    setQueryDepartureOverview = getResourceContentsAsString(aggregationQueryFile);
  }

  /**
   * @param to the max amount till when the time series is wanted - if not given the current time is
   *     taken
   * @return time series in of different data windows in granularity decreasing time windows
   */
  @Cacheable("lastQueryResults")
  public Map<String, OccupationForecastResultRow> getCurrentNearOccupations(
      double longitude, double latitude) {

    LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
    params.put("latitude", latitude);
    params.put("longitude", longitude);

    return druidDataRepository
        .query(setQueryOccupationForecast, params, OccupationForecastResultRow.class).stream()
        .collect(
            Collectors.toMap(
                e -> e.dayMeta_dailyRideStopKey,
                e -> e,
                (e1, e2) -> {
                  return e1;
                }));
  }

  /**
   * @param line, stop, time and weekday
   * @return occupation overview result set with above parameters
   */
  @Cacheable("overviewQueryResults")
  public List<OccupationForecastResultRow> getOccupationOverview(
      String line, String time, String dayGroup) {
    LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
    params.put("line_geoKey", line);
    params.put("time", time);
    params.put("daygroup", dayGroup);

    Map<String, OccupationForecastResultRow> occupationOverview =
        druidDataRepository
            .query(setQueryOccupationOverview, params, OccupationForecastResultRow.class).stream()
            .collect(
                Collectors.toMap(
                    e -> e.dayMeta_dailyRideStopKey,
                    e -> e,
                    (e1, e2) -> {
                      return e1;
                    }));
    for (Map.Entry<String, OccupationForecastResultRow> entry : occupationOverview.entrySet()) {
      // determine the most common bin
      int index =
          entry
              .getValue()
              .performance_sketch
              .indexOf(Collections.max(entry.getValue().performance_sketch));
      // multiplying with 2 as it has 2 bucket in query
      int passengers = (index + 1) * bucketSize;
      EPerceivedOccupation occupancy =
          SimpleDepartureAPI.calculateOccupationCapacity(
              entry.getValue().i_sum_vehicle_seats_max,
              entry.getValue().i_sum_vehicle_possibleStandingPassengers_max,
              passengers);

      entry.getValue().occupancy = occupancy;
    }
    return new ArrayList<OccupationForecastResultRow>(occupationOverview.values());
  }

  /**
   * @param stop_id
   * @return departure overview of the day with stop id
   */
  @Cacheable("departureOverviewQueryResults")
  public List<OccupationForecastResultRow> getDeprtureOverview(String stopId) {
    LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
    params.put("stopId", stopId);

    Map<String, OccupationForecastResultRow> departureOverview =
        druidDataRepository
            .query(setQueryDepartureOverview, params, OccupationForecastResultRow.class).stream()
            .collect(
                Collectors.toMap(
                    e -> e.overview_departure_time + e.line_label,
                    e -> e,
                    (e1, e2) -> {
                      return e1;
                    }));

    for (Map.Entry<String, OccupationForecastResultRow> entry : departureOverview.entrySet()) {
      // determine the most common bin
      int index =
          entry
              .getValue()
              .performance_sketch
              .indexOf(Collections.max(entry.getValue().performance_sketch));
      // multiplying with 2 as it has 2 bucket in query
      int passengers = (index + 1) * bucketSize;
      EPerceivedOccupation occupancy =
          SimpleDepartureAPI.calculateOccupationCapacity(
              entry.getValue().i_sum_vehicle_seats_max,
              entry.getValue().i_sum_vehicle_possibleStandingPassengers_max,
              passengers);

      entry.getValue().occupancy = occupancy;
    }

    return new ArrayList<OccupationForecastResultRow>(departureOverview.values());
  }
}
