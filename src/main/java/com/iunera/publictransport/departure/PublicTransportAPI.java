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
import com.iunera.publictransport.data.transportcompanies.ApplicationEnvironment;
import com.iunera.publictransport.data.transportcompanies.NetworkProviderDataService;
import com.iunera.publictransport.departure.model.StopDeparturesDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicTransportAPI {

  @Autowired NetworkProviderDataService service;

  /**
   * @param latitude
   * @param longitude
   * @return
   */
  @RequestMapping(value = "apiv1/places/publictransport", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<List<PlaceEntity>> getStationsAndStops(
      @RequestParam(required = true) double longitude,
      @RequestParam(required = true) double latitude) {

    try {
      List<PlaceEntity> places = service.getStopsAndStations(longitude, latitude, 500, 10);
      return new ResponseEntity<List<PlaceEntity>>(places, HttpStatus.OK);
      // TODO exception handling
    } catch (Exception e) {
      return new ResponseEntity<List<PlaceEntity>>(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * @param latitude
   * @param longitude
   * @param maxDistance
   * @param maxLocations
   * @return
   */
  @RequestMapping(
      value = ApplicationEnvironment.PUBLIC_TRANSPORT_STOP + "/query",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<List<PlaceEntity>> getPublicTransportPlaces(
      @RequestParam(required = true) double latitude,
      @RequestParam(required = true) double longitude,
      @RequestParam(required = true) int maxDistance,
      @RequestParam(required = true) int maxLocations) {

    try {

      Assert.isTrue(maxDistance <= 30000, "maxDistance to high");
      Assert.isTrue(maxLocations <= 100, "maxLocations to high");

      List<PlaceEntity> places =
          service.getStopsAndStations(latitude, longitude, maxDistance, maxLocations);
      return new ResponseEntity<List<PlaceEntity>>(places, HttpStatus.OK);
      // TODO exception handling
    } catch (Exception e) {
      return new ResponseEntity<List<PlaceEntity>>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(
      value = ApplicationEnvironment.PUBLIC_TRANSPORT_STOP + "/{stopid}",
      method = RequestMethod.GET)
  @ResponseBody
  private ResponseEntity<StopDeparturesDTO> getPublictransportDepartures(
      @PathVariable String stopid,
      @RequestParam(required = false) String gid,
      @RequestParam(required = false, value = "db") String dbid) {

    try {
      StopDeparturesDTO departures = service.getPublictransportDepartures(stopid, gid, dbid);

      return new ResponseEntity<StopDeparturesDTO>(departures, HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<StopDeparturesDTO>(HttpStatus.NOT_FOUND);
    }
  }
}
