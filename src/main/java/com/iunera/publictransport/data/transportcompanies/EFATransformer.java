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

import com.iunera.generaldatatypes.Tag;
import com.iunera.generaldatatypes.place.locationdatatypes.ExtendedLocationAggregate;
import com.iunera.generaldatatypes.place.place.Action;
import com.iunera.generaldatatypes.place.place.DataSourceDetails;
import com.iunera.generaldatatypes.place.place.EPlaceOrigin;
import com.iunera.generaldatatypes.place.place.EPlaceType;
import com.iunera.generaldatatypes.place.place.PlaceEntity;
import com.iunera.publictransport.departure.model.DepartureDTO;
import com.iunera.publictransport.departure.model.LineDTO;
import com.iunera.publictransport.domain.LineKeyGen;
import com.iunera.publictransport.domain.TransportProductDTO;
import de.schildbach.pte.NetworkId;
import de.schildbach.pte.dto.Location;
import de.schildbach.pte.dto.NearbyLocationsResult;
import de.schildbach.pte.dto.Product;
import de.schildbach.pte.dto.QueryDeparturesResult;
import de.schildbach.pte.dto.StationDepartures;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.geojson.geometry.Point;
import org.springframework.util.Assert;

/** This class allows transforming the efa results to good and proper documents */
public class EFATransformer {
  private static MessageDigest md = getSHA1();

  private static MessageDigest getSHA1() {
    try {
      return MessageDigest.getInstance("SHA1");
    } catch (Exception e) {

      e.printStackTrace();
    }
    return null;
  }

  public static String makeSHA1Hash(String input)
      throws NoSuchAlgorithmException, UnsupportedEncodingException {

    md.reset();
    byte[] buffer = input.getBytes("UTF-8");
    md.update(buffer);
    byte[] digest = md.digest();

    String hexStr = "";
    for (int i = 0; i < digest.length; i++) {
      hexStr += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
    }
    return hexStr;
  }

  public static List<PlaceEntity> mergeplaceEntities(List<PlaceEntity> one, List<PlaceEntity> two) {
    List<PlaceEntity> resulttoadd = new ArrayList<PlaceEntity>();
    if (one == null || one.size() == 0) {

      return two;
    }
    if (two == null || two.size() == 0) return one;
    if (two != null && two.size() != 0) {
      for (PlaceEntity o : one) {
        boolean contained = false;
        for (PlaceEntity t : two) {
          String[] tsplit = t.guid.split(":");
          String[] osplit = o.guid.split(":");
          if (tsplit[2].equals(osplit[2])) {

            contained = true;

            break;
          }
        }
        if (!contained) resulttoadd.add(o);
      }
    }
    if (resulttoadd.size() > 0) two.addAll(resulttoadd);
    return two;
  }

  public static void mergeMainStation(PlaceEntity one, NearbyLocationsResult two) {
    if (two == null || two.locations == null || two.locations.size() == 0) return;
    Location trainstation = null;
    for (Location t : two.locations) {
      boolean station = false;
      for (Product p : t.products) {
        if (p.equals(Product.HIGH_SPEED_TRAIN)
            || p.equals(Product.REGIONAL_TRAIN)
            || p.equals(Product.SUBURBAN_TRAIN)) {
          station = true;
          break;
        }
      }
      if (station) {
        trainstation = t;
        break;
      }
    }
    if (null == trainstation) return;
    String name = one.name.toLowerCase();
    if (one.actions.get(0).isPreviewAction()) {
      if (one.actions.get(0).dataURL.contains("?"))
        one.actions.get(0).dataURL =
            one.actions.get(0).dataURL
                + "&"
                + NetworkId.DB.toString().toLowerCase()
                + "="
                + trainstation.id;
      else
        one.actions.get(0).dataURL =
            one.actions.get(0).dataURL
                + "?"
                + NetworkId.DB.toString().toLowerCase()
                + "="
                + trainstation.id;
    }
  }

  public static List<PlaceEntity> getPlaceEntities(
      NearbyLocationsResult kvvstops, String provider) {

    List<PlaceEntity> entities = new ArrayList<PlaceEntity>(kvvstops.locations.size());
    for (Location stop : kvvstops.locations) {
      if (stop.hasName() && stop.name != null && stop.name.length() > 1)
        entities.add(getPlaceEntity(stop, provider));
    }
    return entities;
  }

  public static PlaceEntity getPlaceEntity(Location efastop, String networkprovider) {
    PlaceEntity placeEntity = new PlaceEntity();
    placeEntity.locationAggregate = new ExtendedLocationAggregate();
    Point center = new Point(efastop.getLonAsDouble(), efastop.getLatAsDouble(), 10);
    center.timeStamp = new Date().getTime();
    center.locationProviderOrigin = networkprovider;
    placeEntity.locationAggregate.centers.add(center);
    placeEntity.type = EPlaceType.NORMAL_PLACE;
    placeEntity.name = efastop.name;

    placeEntity.description = "public transport stop";
    placeEntity.dataSourceOrigin = EPlaceOrigin.EXTERNAL_API_EFA;
    placeEntity.dataSourceDetails = new ArrayList<DataSourceDetails>(1);
    DataSourceDetails d = new DataSourceDetails();
    d.sourceId = efastop.id;
    placeEntity.dataSourceDetails.add(d);

    placeEntity.lnsVersion = PlaceEntity.lnsVersion_1;
    try {
      placeEntity.guid = "eefa" + ":" + networkprovider + ":" + efastop.id.replace("&", ":gid=");
    } catch (Exception e) {
      // this should never happen if this happens, something went terribly
      // wrong
      placeEntity.guid = "eefa" + "hashingguiderror";
    }
    placeEntity.isPending = true;
    placeEntity.latestUpdate = new Date();
    placeEntity.placePrimaryFeature = PlaceEntity.primaryFeaturePublicTransport;
    Assert.notNull(placeEntity.placePrimaryFeature);
    placeEntity.applicationScope = PlaceEntity.applicationScope_Publictransport;
    placeEntity.revision = 0;
    placeEntity.addShowNotificationOnNear();

    placeEntity.actions = new ArrayList<com.iunera.generaldatatypes.place.place.Action>(3);
    com.iunera.generaldatatypes.place.place.Action action = null;
    // action to go to the public transport site and top show the preview

    String stopid = networkprovider + ":" + efastop.id.replace("&", "?");

    String realtimeDataUrl = ApplicationEnvironment.getPubicTransportApiUrl(stopid);

    action =
        Action.createAction(
            Action.previewActionType, "departures", "Abfahrtszeiten", null, null, realtimeDataUrl);

    placeEntity.actions.add(action);

    action =
        Action.createAction(
            Action.navigateToActionType,
            "navigateTo",
            "Map",
            "geo:0,0?q="
                + center.getLatitude()
                + ","
                + center.getLongitude()
                + "("
                + placeEntity.name
                + ")",
            null,
            null);
    placeEntity.actions.add(action);

    action =
        Action.createAction(
            Action.filterActionType,
            "equalspots",
            "More stops",
            "lns://?tagname=public_transport_stop",
            null,
            null);
    placeEntity.actions.add(action);

    if (placeEntity.tags == null) placeEntity.tags = new ArrayList<Tag>(1);

    Tag tagtoadd = new Tag();
    tagtoadd.type = Tag.tagKindKeyCategory;
    tagtoadd.name = "public_transport_stop";
    placeEntity.tags.add(tagtoadd);

    return placeEntity;
  }

  public static ZoneId germanTimeZone = ZoneId.of("Europe/Berlin");

  public static List<DepartureDTO> transformDepartures(QueryDeparturesResult res, ZoneId zoneId) {

    List<DepartureDTO> ret = null;
    if (res == null) return null;
    if (res.stationDepartures == null) return null;
    for (StationDepartures sd : res.stationDepartures) {
      if (sd.departures != null) {
        ret = new ArrayList<DepartureDTO>(sd.departures.size());
        for (de.schildbach.pte.dto.Departure departure : sd.departures) {
          try {
            LineDTO l = null;
            com.iunera.publictransport.departure.model.PositionDTO p = null;

            try {
              p =
                  new com.iunera.publictransport.departure.model.PositionDTO(
                      departure.position.name, departure.position.section);
            } catch (Exception e) {
            }
            String destinationname = departure.destination.name;
            try {
              String numericorlogicalname = departure.line.label;
              String label = departure.line.name;

              if (numericorlogicalname == null || numericorlogicalname.isEmpty())
                numericorlogicalname = departure.line.name;
              if (departure.line.label != null) {
                numericorlogicalname = departure.line.label.replaceAll(" \\(.*?\\)", "");
              }
              // be sure there is a sabel
              if (departure.line.label == null || departure.line.label.equals("")) {
                if (departure.line.product != null) {
                  numericorlogicalname = new String(departure.line.product.code + "");
                } else {
                  if (departure.destination.name.matches("^\\d.*")) {
                    numericorlogicalname = destinationname.replaceAll("\\D+", "");
                  } else {
                    if ((departure.destination.name.startsWith("S")
                            && departure.destination.name.replaceFirst("S", "").matches("^\\d.*"))
                        || (departure.destination.name.startsWith("U")
                            && departure
                                .destination
                                .name
                                .replaceFirst("U", "")
                                .matches("^\\d.*"))) {

                      numericorlogicalname =
                          departure.destination.name.charAt(0)
                              + departure.destination.name.replaceAll("\\D+", "");
                    }
                  }
                }
              }

              if (destinationname.startsWith(numericorlogicalname)
                  && destinationname.replaceFirst(numericorlogicalname, "").matches("^\\d.*")) {

                String label2 =
                    numericorlogicalname
                        + destinationname
                            .replaceFirst(numericorlogicalname, "")
                            .replaceAll("\\D+", "");
                destinationname = destinationname.replaceFirst(label2 + " ", "");
              } else if (departure.destination.name.matches("^\\d.*")
                  && departure.destination.name.startsWith(numericorlogicalname)) {
                // label=label+destinationname.replaceFirst(label,"").replaceAll("\\D+","");
                destinationname = destinationname.replaceFirst(numericorlogicalname + " ", "");
              }

              if (label == null || label.isEmpty()) label = departure.line.label;

              l =
                  new LineDTO(
                      departure.line.id,
                      departure.line.network,
                      TransportProductDTO.fromCode(departure.line.product.code),
                      label,
                      numericorlogicalname);
              l.providerKey = numericorlogicalname;
              l.geoKey =
                  LineKeyGen.getGeoKey(
                      sd.location.getLonAsDouble(),
                      sd.location.getLatAsDouble(),
                      numericorlogicalname,
                      TransportProductDTO.fromString(departure.line.product.name()),
                      LineKeyGen.DEFAULT_PRECISION);

            } catch (Exception e) {
            }
            Instant plannedtime = null;
            if (departure.plannedTime != null)
              plannedtime =
                  departure
                      .plannedTime
                      .toInstant()
                      .atZone(zoneId)
                      .toLocalDateTime()
                      .atZone(zoneId)
                      .toInstant();

            Instant predictedtime = null;
            if (departure.predictedTime != null)
              predictedtime =
                  departure
                      .predictedTime
                      .toInstant()
                      .atZone(zoneId)
                      .toLocalDateTime()
                      .atZone(zoneId)
                      .toInstant();
            ;

            DepartureDTO d = new DepartureDTO(plannedtime, predictedtime, l, p, destinationname);
            ret.add(d);
          } catch (Exception e) {
            // this should not happen, but better it h appens and we
            // continue then breaking the service
            // e.printStackTrace();
          }
        }
      }
    }
    return ret;
  }

  public static List<DepartureDTO> merge(
      List<DepartureDTO> one,
      String oneoriginprovider,
      List<DepartureDTO> two,
      String[] twooriginproviders) {

    if (one == null || one.size() == 0) return two;
    if (two == null || two.size() == 0) return one;
    List<DepartureDTO> result = new ArrayList<DepartureDTO>(15);

    Set<DepartureDTO> secondmatches = new HashSet<DepartureDTO>(15);
    for (DepartureDTO dep11 : one) {
      boolean contained = false;

      for (DepartureDTO dep22 : two) {
        try {
          if ((dep22.plannedTime.equals(dep11.plannedTime)
                  || (dep22.predictedTime != null
                      && dep11.predictedTime != null
                      && dep22.predictedTime.equals(dep11.predictedTime)))
              && ((dep22.line.label.equals(dep11.line.label))
                  || (dep22.line.label.contains(dep11.line.label))
                  || (dep11.line.label.contains(dep22.line.label))
                  || (dep22.destination.contains(dep11.line.label))
                  || (dep11.destination.contains(dep22.line.label)))
              && (dep22.destination.contains(dep11.destination)
                  || dep11.destination.contains(dep22.destination))) {

            // it seems to be the same line and departure time
            // we trust that the origin provider of this region os
            // the better data
            boolean oneofthesecondprovidermatches = false;
            for (String provider : twooriginproviders)
              if (dep22.line.network.equals(provider)) {
                oneofthesecondprovidermatches = true;
                break;
              }
            if (!oneofthesecondprovidermatches)
              for (String provider : twooriginproviders)
                if (dep11.line.network.equals(provider)) {
                  oneofthesecondprovidermatches = true;
                  break;
                }

            if (oneofthesecondprovidermatches) {
              result.add(dep22);
              contained = true;
              secondmatches.add(dep22);
              break;
            } else if (dep11.line.network.equals(oneoriginprovider)) {
              result.add(dep11);
              contained = true;
              secondmatches.add(dep22);
              break;
            } else {
              // none of the providers matches
              // this should not happen, but the datasource maybe
              // crap - here we could determine who has the more
              // complete information
              // currently we just add the first one
              result.add(dep22);
              contained = true;
              secondmatches.add(dep22);
              break;
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      if (!contained) {
        // it was not contained in the second result- therefore add
        // the first one
        result.add(dep11);
      } else {
        // it
      }
    }
    // now add the second ones that did not match
    for (DepartureDTO dep22 : two) {
      if (!secondmatches.contains(dep22)) result.add(dep22);
    }

    return result;
  }

  public static void clean(List<DepartureDTO> departureDTOs) {
    if (departureDTOs == null || departureDTOs.size() == 0) return;
    for (DepartureDTO dep : departureDTOs) {

      if (dep.line != null
          && dep.line.label != null
          && dep.destination != null
          && dep.destination.contains(dep.line.label + " "))
        dep.destination = dep.destination.replace(dep.line.label + " ", "");
    }
  }
}
