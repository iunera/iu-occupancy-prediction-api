package com.iunera.generaldatatypes.place.place;

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
import com.iunera.generaldatatypes.Tag;
import com.iunera.generaldatatypes.place.locationdatatypes.ExtendedLocation;
import com.iunera.generaldatatypes.place.locationdatatypes.ExtendedLocationAggregate;
import com.iunera.generaldatatypes.place.locationdatatypes.cell.CellInfo;
import com.iunera.generaldatatypes.place.locationdatatypes.cell.CellInfoAggregation;
import com.iunera.generaldatatypes.place.locationdatatypes.wifi.WifiInformation;
import com.iunera.generaldatatypes.place.locationdatatypes.wifi.WifiInformationAggregation;
import java.util.ArrayList;
import java.util.List;

/** A request to create a place in the database */
@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceOrHappeningCreationRequest {
  // used that we can discriminate the clients later
  // as long as we do not
  public String client = "originalstappzandroid";

  public PlaceOrHappeningCreationRequest() {}

  public PlaceOrHappeningCreationRequest(
      PlaceEntity template,
      String tmpCreationRequestClientSideGuid,
      ExtendedLocation currentUserLocation) {
    this.tmpCreationRequestClientSideGUID = tmpCreationRequestClientSideGuid;
    this.dataSource = template.dataSourceOrigin;
    this.dataSourceDetails = template.dataSourceDetails;
    // this.caption = template.caption;
    // this.address = template.address;
    this.userEnteredName = template.name;
    // this.caption = template.caption;
    this.type = template.type;
    this.tags = template.tags;
    this.description = template.description;
    // this.placeEntityUri = template.placeEntityUri;
    // this.alternativeNames = template.alternativeNames;
    this.locationAggregate = template.locationAggregate;
    // this.categories = template.categories;
    this.entityGuid = template.guid;
    this.isHappening = false;
    this.extendedLocation = currentUserLocation;
  }

  public PlaceEntity getPlaceEntityForRequest() {
    return getPlaceEntityForRequest(null);
  }

  /** checks if all necessary properties are set to send the request to the server */
  public boolean isValid() {
    if (userEnteredName == null || userEnteredName.equals("")) return false;
    if (this.type == null) return false;
    if (this.entityGuid != null && this.entityGuid.equals("")) return false;
    if (this.isHappening) return false; // currently, we only support places
    if (extendedLocation == null) return false;
    return true;
  }

  public PlaceEntity getPlaceEntityForRequest(String entityGuid) {
    PlaceEntity p = new PlaceEntity();
    p.dataSourceOrigin = this.dataSource;
    p.dataSourceDetails = this.dataSourceDetails;
    // p.caption = this.caption;
    // p.address = this.address;
    p.name = this.userEnteredName;
    // p.caption = this.caption;
    p.type = this.type;
    p.tags = this.tags;
    p.description = this.description;
    //
    // p.placeEntityUri = this.placeEntityUri;
    // p.alternativeNames = this.alternativeNames;
    p.locationAggregate = this.locationAggregate;
    // p.categories = this.categories;
    if (entityGuid != null) this.entityGuid = entityGuid;
    p.guid = this.entityGuid;

    // now add the extended location to the place entity
    // enrich the
    // data with the user coordinates and the location that the user
    // provided
    if (p.locationAggregate == null) p.locationAggregate = new ExtendedLocationAggregate();
    if (p.locationAggregate.centers == null)
      p.locationAggregate.centers = new ArrayList<org.geojson.geometry.Point>();
    if (extendedLocation != null
        && extendedLocation.geoPointLocations != null
        && extendedLocation.geoPointLocations.size() > 0)
      p.locationAggregate.centers.addAll(extendedLocation.geoPointLocations);

    if (p.locationAggregate.wifiInformationAggregation == null)
      p.locationAggregate.wifiInformationAggregation = new ArrayList<WifiInformationAggregation>();

    // compute the wifi aggregate
    if (extendedLocation != null
        && extendedLocation.wifiInformation != null
        && extendedLocation.wifiInformation.networksAroundMe != null)
      for (WifiInformation w : extendedLocation.wifiInformation.networksAroundMe) {
        WifiInformationAggregation wifiaggr = new WifiInformationAggregation();
        wifiaggr.BSSID = w.bssid;
        wifiaggr.frequency = w.frequency;
        wifiaggr.isMainNetwork = w.isConnected;
        wifiaggr.ssid = w.ssid;
        wifiaggr.signalDbAvg = w.signalDb;
        wifiaggr.signalDbMax = w.signalDb;
        wifiaggr.signalDbMin = w.signalDb;
        p.locationAggregate.wifiInformationAggregation.add(wifiaggr);
      }

    // compute the cell aggregate
    if (extendedLocation != null && extendedLocation.cellInformation != null) {
      if (p.locationAggregate.cellInfoAggregation == null)
        p.locationAggregate.cellInfoAggregation = new ArrayList<CellInfoAggregation>();
      if (p.locationAggregate.neighbourCellAggregates == null)
        p.locationAggregate.neighbourCellAggregates = new ArrayList<CellInfoAggregation>();
      CellInfoAggregation cia = new CellInfoAggregation();
      CellInfo cifo = extendedLocation.cellInformation;
      cia.cid = cifo.cid;
      cia.lac = cifo.lac;
      cia.asuAvg = cifo.asu;
      cia.asuMax = cifo.asu;
      cia.asuMin = cifo.asu;
      cia.mcc = cifo.mcc;
      cia.mnc = cifo.mnc;
      cia.psc = cifo.psc;
      cia.radio = cifo.radio;
      cia.scannerRadioType = new ArrayList<String>(1);
      cia.scannerRadioType.add(cifo.scannerRadioType);
      p.locationAggregate.cellInfoAggregation.add(cia);
      if (cifo.neighbourCellInfos != null && cifo.neighbourCellInfos.size() > 0) {
        for (CellInfo cifo2 : cifo.neighbourCellInfos) {
          cia = new CellInfoAggregation();
          cia.cid = cifo2.cid;
          cia.lac = cifo2.lac;
          cia.asuAvg = cifo2.asu;
          cia.asuMax = cifo2.asu;
          cia.asuMin = cifo2.asu;
          cia.mcc = cifo2.mcc;
          cia.mnc = cifo2.mnc;
          cia.psc = cifo2.psc;
          cia.radio = cifo2.radio;
          cia.scannerRadioType = new ArrayList<String>(1);
          cia.scannerRadioType.add(cifo2.scannerRadioType);
          p.locationAggregate.neighbourCellAggregates.add(cia);
        }
      }
    }
    /*
     * if(p.name.contains("b1337")){ if (extendedLocation!=null &&
     * extendedLocation.bluetoothInformation != null &&
     * extendedLocation.bluetoothInformation.beaconsAroundMe != null &&
     * extendedLocation.bluetoothInformation.beaconsAroundMe.size()>0 ) {
     * p.name= p.name.replace("b1337", "");
     * p.locationAggregate.bluetoothInformationAggregation= new
     * ArrayList<BluetoothBeaconAggregation>(1); BluetoothBeaconAggregation
     * aggr= new BluetoothBeaconAggregation(); BluetoothBeaconInformation
     * stringestinfo
     * =extendedLocation.bluetoothInformation.beaconsAroundMe.get(0);
     * aggr.bluetoothAddresses=new ArrayList<String>(1);
     * aggr.bluetoothAddresses.add(stringestinfo.bluetoothAddress) ;
     * aggr.maxdistance=10.0f ; aggr.minRssi=stringestinfo.rssi/2 ;
     * aggr.regionIdentifiers=stringestinfo.identifiers ;
     * p.locationAggregate.bluetoothInformationAggregation.add(aggr);
     * p.type=EPlaceType.HIGHPRECISION_PLACE;
     * type=EPlaceType.HIGHPRECISION_PLACE; p.showNotificationOnNear=true;
     * p.onNearNotificationMessage="hello beacon"+p.name; } }
     */

    return p;
  }

  /**
   * retruns a tmp stub of the place entity filled with the tmpCreationRequestClientSideGUID that
   * can be used until a valid place gets returned by the server
   */
  public PlaceEntityOrHappeningStub getCorrespondingEntityStubWithRequestGuid() {
    PlaceEntity p = getPlaceEntityForRequest(null);
    PlaceEntityOrHappeningStub stub = p.getCorrespondingStub();
    stub.creationRequestClientSideGUID = tmpCreationRequestClientSideGUID;
    return stub;
  }

  public String entityGuid;

  public EPlaceType type;
  public EPlaceOrigin dataSource;
  public String mainPictureURI;
  /** unique readble */
  // the name of the spot
  public String caption;
  // human readable description of the spot
  public String description;
  public String placeEntityUri;

  public String address;

  public List<LocalizedName> alternativeNames;

  public List<DataSourceDetails> dataSourceDetails;
  /**
   * a possible- pre-defined location aggregate however, if a extended location is given in
   * addition, the wifi, bsids and center of the location gets overrridenn
   */
  public ExtendedLocationAggregate locationAggregate;

  public List<String> categories;
  public List<Tag> tags;

  /** The location of the requesting entity (user) where the place should be created */
  public ExtendedLocation extendedLocation;

  /**
   * The place or happening name the user entered if it was not found in the proposed places -
   * however currenty, if a place entity is given this name is ignored
   */
  public String userEnteredName;
  /**
   * specifies weather a new place is defined by the user or the user picked one from another
   * datasource
   */
  public EPlaceOrHappeningSelectionType placeSelectionType;

  public boolean isHappening;
  // public boolean hascurrentvent;
  /**
   * this reference is needed to reveal the worldannotation and update the worldannotation guid if
   * necessary complusary to be filled - it needs to be the same id like in the stub of in the place
   * or happening stub
   */
  public String tmpCreationRequestClientSideGUID;

  /*
   * public enum EPlaceCreationDataSource { /** the place was entered by the
   * user and comes out of the place suggestions
   */
  // USER_ENTERED_FROM_SUGGESTIONS,
  /** The place was not selected from a data source and the user entered freely a new place */
  // USER_ENTERED_FOREIGEN_DATA_SOURCE_SELF_ENTERED,
  /**
   * The place was used by auto selection - the user did not enter any place and the determination
   * was done by auto selection when the user posted or so
   */
  // AUTO_SELECTED

  // }*/

}
