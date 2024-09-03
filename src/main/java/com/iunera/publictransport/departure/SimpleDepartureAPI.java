package com.iunera.publictransport.departure;

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

import com.iunera.generaldatatypes.place.place.PlaceEntity;
import com.iunera.publictransport.departure.model.DepartureDTO;
import com.iunera.publictransport.departure.model.EPerceivedOccupation;
import com.iunera.publictransport.departure.model.LineDTO;
import com.iunera.publictransport.departure.model.PositionDTO;
import com.iunera.publictransport.departure.model.Stop;
import com.iunera.publictransport.departure.model.StopDeparturesDTO;
import com.iunera.publictransport.domain.RideKeysGeneration;
import com.iunera.publictransport.domain.TransportProductDTO;
import com.iunera.publictransport.occupation.historicdata.OccupationForecastResultRow;
import com.iunera.publictransport.occupation.historicdata.OccupationStatisticsService;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.geojson.geometry.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/apiv1")
public class SimpleDepartureAPI {

  @Autowired PublicTransportAPI publicTransportAPI;

  @Autowired OccupationStatisticsService occupationStatisticsService;

  RestTemplate resttemplate = new RestTemplate();

  private static ZoneId germanyZone = ZoneId.of("Europe/Berlin");

  private static int defaultBusSeats = 40;
  private static int bucketSize = 2;

  @CrossOrigin(origins = "*")
  @RequestMapping(value = "test/departures", method = RequestMethod.GET)
  public List<StopDeparturesDTO> getTestAllDepartures() {

    List<StopDeparturesDTO> stopDeparturesDTOs = new ArrayList<>();

    List<DepartureDTO> a = new ArrayList<>();

    a.add(
        new DepartureDTO(
            Instant.now(),
            Instant.now(),
            new LineDTO("id", "network", TransportProductDTO.BUS, "S2", "name"),
            new PositionDTO("positionname", "section"),
            "destination"));

    stopDeparturesDTOs.add(new StopDeparturesDTO(a, null));
    return stopDeparturesDTOs;
  }

  @CrossOrigin(origins = "*")
  @RequestMapping(value = "departure/overview", method = RequestMethod.GET)
  public List<OccupationForecastResultRow> predictionOccupationData(@RequestParam String stopId) {

    return occupationStatisticsService.getDeprtureOverview(stopId);
  }

  private double[] defaultLonLat = new double[] {9.125, 50.197};

  @CrossOrigin(origins = "*")
  @RequestMapping(value = "departures", method = RequestMethod.GET)
  public List<StopDeparturesDTO> getAllDepartures(
      @RequestParam(required = false) String coordinates) {
    ResponseEntity<List<PlaceEntity>> stopsandstations;
    double[] longlat = new double[2];
    try {
      String[] longlatsplit = null;
      if (coordinates.contains(",")) longlatsplit = coordinates.split(",");
      else longlatsplit = coordinates.split(";");

      longlat[0] = Double.parseDouble(longlatsplit[0]);
      longlat[1] = Double.parseDouble(longlatsplit[1]);

    } catch (Exception e) {

      longlat = defaultLonLat;
    }

    stopsandstations = publicTransportAPI.getStationsAndStops(longlat[0], longlat[1]);
    // fetching occupation statistics from service
    Map<String, OccupationForecastResultRow> results =
        occupationStatisticsService.getCurrentNearOccupations(longlat[0], longlat[1]);

    List<StopDeparturesDTO> stopDeparturesDTOs = new ArrayList<>();

    stopsandstations
        .getBody()
        .forEach(
            efaStop -> {

              // get the coordinate of the stop
              Point stopCoordinate = efaStop.locationAggregate.centers.get(0);

              // a geolocation has actions and one of these actions is to get the departures
              efaStop.actions.forEach(
                  action -> {
                    // preview actions are originally for view real time departures and showing them
                    // as popup
                    // we will need to change the public transport API here in the future
                    if (action.isPreviewAction()) {

                      ResponseEntity<StopDeparturesDTO> depdto =
                          resttemplate.getForEntity(action.dataURL, StopDeparturesDTO.class);
                      StopDeparturesDTO stopdepartures = depdto.getBody();

                      Stop stop = new Stop();
                      stop.label = efaStop.name;
                      // TODO: use the real unique id  of the provider here
                      stop.providerKey = efaStop.name;
                      // check for occupation data of the departure
                      // and add it to the object if available
                      if (stopdepartures.departureDTOs != null)
                        stopdepartures.departureDTOs.forEach(
                            departure -> {
                              ZonedDateTime zdt = departure.plannedTime.atZone(germanyZone);
                              // ensure the departure is only minute fine grained
                              LocalTime departureTime =
                                  zdt.toLocalTime().truncatedTo(ChronoUnit.MINUTES);

                              // generate the unique key for the departure
                              String departurekey =
                                  RideKeysGeneration.getRideDayKeyForStop(
                                      stopCoordinate.getLongitude(),
                                      stopCoordinate.getLatitude(),
                                      departureTime,
                                      departure.line.name,
                                      departure.line.transportProduct);

                              // for all rides where we do not know the occupation, we set unknown
                              departure.predictedOccupation = EPerceivedOccupation.UNKNOWN;

                              if (results.containsKey(departurekey)) {
                                OccupationForecastResultRow occupationDetails =
                                    results.get(departurekey);
                                // determine the most common bin
                                int index =
                                    occupationDetails.performance_sketch.indexOf(
                                        Collections.max(occupationDetails.performance_sketch));
                                // multiplying with 2 as it has 2 bucket in query
                                int passengers = (index + 1) * bucketSize;
                                // compute the likely occupation
                                departure.predictedOccupation =
                                    calculateOccupationCapacity(
                                        occupationDetails.i_sum_vehicle_seats_max,
                                        occupationDetails
                                            .i_sum_vehicle_possibleStandingPassengers_max,
                                        passengers);

                                stop.label = efaStop.name;
                                stop.providerKey = occupationDetails.stop_geoKeys;
                              } else {
                                // we do not have occupation data for this ride
                                // TODO: we could do some fallback or so - for now we leave it to
                                // unknown
                              }
                            });
                      stopdepartures.originStop = stop;
                      stopDeparturesDTOs.add(stopdepartures);
                    }
                  });
            });

    return stopDeparturesDTOs;
  }

  // finding the occupation capacity with seating, standing and passengers
  public static EPerceivedOccupation calculateOccupationCapacity(
      Integer seatingCapacity, Integer standingCapacity, int passengers) {

    int seats = 0;
    int standing = 0;

    if (seatingCapacity != null) seats = Integer.valueOf(seatingCapacity);
    if (standingCapacity != null) standing = Integer.valueOf(standingCapacity);

    if (seats == 0) seats = defaultBusSeats;

    double capacityPercentage = (passengers * 100) / (seats + standing);
    double seatingCapacityPercentage = (passengers * 100) / seats;

    // this can never happen in germany because the places are defined that even if defined full it
    // is still full
    // crowded 75% of all (seats+standing) places are taken
    if (capacityPercentage >= 70) return EPerceivedOccupation.CROWDED;

    // EPerceivedOccupation.full 100% of the seats are taken
    if (seatingCapacityPercentage > 100) return EPerceivedOccupation.SEATING_OCCUPIED;

    // EPerceivedOccupation.half above 50% of the seating capacity
    if (seatingCapacityPercentage > 50) return EPerceivedOccupation.SEATING_HALF_OCCUPIED;

    // EPerceivedOccupation.EMPTY - seating capacity under 50%
    return EPerceivedOccupation.SPARSE;
  }
}
