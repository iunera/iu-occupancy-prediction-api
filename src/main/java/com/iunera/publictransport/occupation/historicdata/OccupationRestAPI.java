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

import com.iunera.publictransport.datatypes.EDayGroup;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/apiv1/occupation")
public class OccupationRestAPI {
  private static ZoneId germanyZone = ZoneId.of("Europe/Berlin");
  @Autowired OccupationStatisticsService occupationStatisticsService;

  /**
   * This endpoint returns all departures, starting from the current time of the client, of a
   * specific line and the associated forecasted occupations at different stops.
   */
  @RequestMapping(value = "/line/overview", method = RequestMethod.GET)
  public ResponseEntity<List<OccupationForecastResultRow>> predictionOccupationData(
      @RequestParam Instant time, @RequestParam String lineId) {

    // TODO: the locale and departure time can be extracted from the line id, because a line is
    // placed at a geographic location
    // for now, we add only germany
    ZoneId zone = germanyZone;
    Locale locale = new Locale("de", "DE");

    ResponseEntity<List<OccupationForecastResultRow>> lineOccupationDetails =
        new ResponseEntity<List<OccupationForecastResultRow>>(
            occupationStatisticsService.getOccupationOverview(
                lineId,
                String.valueOf(time.atZone(zone).toLocalTime().getHour()),
                EDayGroup.getDayGroup(time, zone, locale).toString()),
            HttpStatus.OK);
    // this commenting has to be changed with implemented list
    //		lineOccupationDetails.getHeaders().add(HttpHeaders.CACHE_CONTROL,
    //				CacheControl.maxAge(Duration.ofHours(1)).cachePrivate().getHeaderValue());
    return lineOccupationDetails;
  }
}
