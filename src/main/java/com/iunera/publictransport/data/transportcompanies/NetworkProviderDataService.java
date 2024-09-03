package com.iunera.publictransport.data.transportcompanies;

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
import com.iunera.publictransport.data.transportcompanies.areas.Area;
import com.iunera.publictransport.departure.model.DepartureDTO;
import com.iunera.publictransport.departure.model.StopDeparturesDTO;
import de.schildbach.pte.DbProvider;
import de.schildbach.pte.NetworkProvider;
import de.schildbach.pte.dto.Location;
import de.schildbach.pte.dto.LocationType;
import de.schildbach.pte.dto.NearbyLocationsResult;
import de.schildbach.pte.dto.QueryDeparturesResult;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class NetworkProviderDataService {

  private static NetworkProvider bahnprovider =
      new DbProvider(
          "{\"type\":\"AID\",\"aid\":\"USER\"}", "password".getBytes(StandardCharsets.UTF_8));
  public static final String bahnid = bahnprovider.id().name().toLowerCase();

  @Autowired private Map<String, Area> areas;
  //	@Cacheable
  public List<PlaceEntity> getStopsAndStations(
      double longitude, double latitude, int maxDistance, int maxLocations) throws Exception {

    List<PlaceEntity> stops = new ArrayList<PlaceEntity>(maxLocations);
    de.schildbach.pte.dto.Point current =
        de.schildbach.pte.dto.Point.fromDouble(latitude, longitude);

    Location loc = Location.coord(current);
    EnumSet<LocationType> stations = EnumSet.of(LocationType.STATION);
    NearbyLocationsResult res = null;

    try {
      for (Area area : areas.values()) {
        if (area.isInArea(longitude, latitude)) {
          res =
              area.getNetworkProvider()
                  .queryNearbyLocations(stations, loc, maxDistance, maxLocations);

          List<PlaceEntity> resstops =
              EFATransformer.getPlaceEntities(
                  res, area.getNetworkProvider().id().toString().toLowerCase());
          stops = EFATransformer.mergeplaceEntities(stops, resstops);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      PlaceEntity potentialMainStation = null;
      boolean nearStation = false;
      for (int i = 0; i < stops.size(); i++) {
        PlaceEntity pe = stops.get(i);
        // is this near a station that we should call the railroad provider?
        if (pe.name.toLowerCase().contains("bahnhof")
            || pe.name.toLowerCase().contains("hbf")
            || pe.name.toLowerCase().contains("bhf")) nearStation = true;

        if (pe.name.toLowerCase().equals("bahnhof")
            || pe.name.toLowerCase().equals("hauptbahnhof")
            || pe.name.toLowerCase().endsWith("bhf")
            || pe.name.toLowerCase().endsWith("hbf")
            || pe.name.toLowerCase().endsWith("bahnhof")
            || pe.name.toLowerCase().endsWith("hauptbahnhof")) {
          potentialMainStation = pe;
        }
      }
      res = null;
      if (nearStation) {
        res = bahnprovider.queryNearbyLocations(stations, loc, maxDistance, maxLocations);
      }

      // there seems to be a main station near here
      if (potentialMainStation != null) {

        EFATransformer.mergeMainStation(potentialMainStation, res);
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return stops;
  }

  public StopDeparturesDTO getPublictransportDepartures(
      @PathVariable String stopid,
      @RequestParam(required = false) String gid,
      @RequestParam(required = false, value = "db") String dbid)
      throws IOException {
    List<DepartureDTO> mergedres = null;

    String completestop = stopid;
    String provider = "";

    if (stopid.contains(":")) {
      String[] stopfragments = stopid.split(":");
      provider = stopfragments[0];
      stopid = stopfragments[1];
    }

    if (stopid.contains("&")) {
      stopid = stopid.split("&")[0];
    }

    Location resultpoint = null;

    QueryDeparturesResult bahnproviderres = null;

    QueryDeparturesResult res = null;

    res = areas.get(provider).getNetworkProvider().queryDepartures(stopid, new Date(), 30, false);
    mergedres = EFATransformer.transformDepartures(res, EFATransformer.germanTimeZone);

    // merge main stations from deutsche bahn
    try {
      if (dbid != null) {
        QueryDeparturesResult rnvnvvres = bahnprovider.queryDepartures(dbid, new Date(), 30, false);
        List<DepartureDTO> bahndeps =
            EFATransformer.transformDepartures(rnvnvvres, EFATransformer.germanTimeZone);

        mergedres =
            EFATransformer.merge(bahndeps, bahnid, mergedres, new String[] {bahnid, provider});
      }
    } catch (Exception e) {

    }

    // if station
    EFATransformer.clean(mergedres);
    if (mergedres != null) {
      DepartureDTO[] a = mergedres.toArray(new DepartureDTO[mergedres.size()]);

      Arrays.sort(a, DepartureDTO.TIME_COMPARATOR_RESPECTING_DELAYS);
      mergedres = Arrays.asList(a);
    }

    return new StopDeparturesDTO(mergedres);
  }
}
