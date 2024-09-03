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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.iunera.generaldatatypes.Tag;
import com.iunera.generaldatatypes.UserNickname;
import com.iunera.generaldatatypes.place.locationdatatypes.ExtendedLocationAggregate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Transient;

// detectable by wifi data and a range around / typically a house or so
// 2 dimensional like floor - if it is multi-dimensional and too large like a large floor it becomes
// a compound
@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceEntity {
  // used to indicate it is a physical web entity that we can dump the old
  // entities lateron or extract the relevant easily
  public static String lnsVersion_1 = "1";
  public String lnsVersion = null;

  public static String tagTypeVisibilityOption = "visibilityOption";
  /**
   * This indicates if a notification gets shown when the user is near. For normal and ordinary
   * homapges this is false. It is mainly used to compare between normal place entities and the ones
   * resulting in pushes IT maybe removed in the future therefore only access it over the
   * isShowNotificationOnNear(function)
   */
  public static String tagTypeVisibilityOptionShowNotificationWithActionsOnNear =
      "showNotificationOnNear";
  // flag to use if it is a private place of a user - for instance my home-
  // this is timhome
  // private places are not really of interest for the public but i can use
  // them and then in my worldannotation gets shown @Ha���mersheim
  // additionally such private places maybe used for security settings

  // all //-- means it is not in the database, yet
  // --public String onNearNotificationMessage;
  /** the nickname of the creator in case we know it maybe null */
  public UserNickname creatorUserNickName;

  /** this method checks if a place contains the given name */
  public boolean hasName(String name) {
    if (this.name.equals(name)) return true;
    // if (this.alternativeNames != null
    // && this.alternativeNames.contains(name))
    // return true;
    return false;
  }

  /**
   * the provider of the data - where is it from e.g. Pending world annotations, Well-known places
   * collection, user specific selected datasources collection foriegn data sources like open
   * streetmap where stappz has no persitance
   */
  @Transient public String dataProvider;

  public Date latestUpdate;
  // in app logic
  // the ds - open streetmap - own datasource and so on
  public EPlaceOrigin dataSourceOrigin;

  public boolean hasGuid() {
    if (guid == null || guid.equals("")) return false;
    return true;
  }

  // the temporary id of new or persistence pending objects
  public String guid;

  // gets incremented on each update
  public long revision = 0;

  /** unique readble */
  // public String placeEntityUri; // - > create this

  public boolean hasPreviewAction() {
    if (actions == null || actions.size() == 0) return false;
    for (Action a : actions) {
      if (a.isPreviewAction()) return true;
    }
    return false;
  }

  public Action getPreviewAction() {
    if (actions == null || actions.size() == 0) return null;
    for (Action a : actions) {
      if (a.isPreviewAction()) return a;
    }
    return null;
  }

  /**
   * delete this in the future option for the client to identify local place reuqests uniquelly to
   * superset their information with more accurate server data
   */
  @Transient @Deprecated public String tmpPlaceEntityClientSideGUID;
  /** the database id of the place */
  public String id;
  /**
   * a place that is just a suggestion but not approved yet completely - it is used to indicate that
   * a place gets suggested to other users but that it is not a fixed and existing place yet If it
   * is in the reveal database this flag is always true - it is just as long as pending as there it
   * is a place creation request
   */
  public boolean isPending = true;

  /**
   * a score how valid the placedata is - do we have lots of users using it, plenty of approvals are
   * availabe? can be used later to collect only on occasion data from teh clients
   */
  public int validityScore;
  /**
   * the unique uri how the place can be identified- like the guid unique but more in a human
   * readable form
   */
  // public String placeEntityUri;
  /** The main name of the place */
  public String name;

  /**
   * Synonyms for the place. For instance a place maybe called "Grund und Hauptschule
   * Ha���������mersheim" Synonyms maybe "Friedrich Heu��������� Schule", "Hauptschule", "FHS" or
   * "GHS"
   */
  // public List<LocalizedName> alternativeNames;

  // the application of orign, e.g. revealmoments
  // public String originApplication; // filled as a complusary

  // ascending to the granularity entries of larger areas- normally stops at
  // the coarsearea
  // REMARK: DO not forget coarse areas may be surrounded by other coarse
  // areas (e.g. suburb by district and district by city)
  // public List<PlaceEntityOrHappeningStub> surroundingEntities;

  // this defines the precison of the place - is it large, so the accuary is
  // large, is it small, then the acccuracy is small
  // this means- if the accuracy is better, it is smaller - therefore you
  // should alsoways select the place with the smallest accuary
  public int accuracy;
  // the address
  public Address address;

  public List<DataSourceDetails> dataSourceDetails;

  // the events happening at this place
  // public List<HappeningStub> events;

  /**
   * the classification of the place - is it a small place or a large place like a stadium and on on
   */
  public EPlaceType type;

  /**
   * Is it a place like a named place (busstop) or a place to eat or drink this property might be
   * removed in the future - therefore do not rely on it in case it is needed- the main category
   * maybe defined in a monotaxonomy like amenity/restaurant/italian amenity/restaurant/asian/thai
   * public_transport/busstop restaurant/table - for instance to define a table as place in case of
   * open streetmap this is the primary feature: http://wiki.openstreetmap.org/wiki/Map_Features
   * however- OSM has things like a biergarten is attached to a restaurant and those are different
   * entities... in our thinking of STAPPZ this would be one entity the main feature is an indicator
   * what kind of services are offered
   *
   * <p>for the sake of consistency we try to be as near as possible on the OSM nomenclatur as
   * possible However, we also introduce
   *
   * <p>for a personal homepage or a crowd-url this is the main category of the content of the
   * homepage
   *
   * <p>in case it is a moveable object like a becacon key attachment or a dog collar with beacon -
   * it is also indicated by this main categroy
   */
  public String placePrimaryFeature;

  public static String primaryFeatureMoveable = "moveable";
  public static String primaryFeaturehomepage = "webLink";

  public static final String primaryFeaturePublicTransport = "public_transport/stop";
  public static final String primaryFeatureParking = "amenity/parking";
  public static final String primaryFeatureCharging = "amenity/charging_station";
  public static final String primaryFeatureAirqualityNow = "airquality/now";
  public static final String primaryFeatureFuel = "amenity/fuel";

  /**
   * the realtime data key is the key from what you can query the corresponding details or
   * realtimedata. The switch about the api is the placePrimaryFeature means:
   * <placePrimaryFeature>/<realtimeDataKey> e.g.
   * amenity/charging_station/1062_EnBW_Stuttgart_echargingv1 the controller should return
   * PlaceEntities with actions and dataURL like this
   * http://cloud.iunera.com/iunera-location-data/apiv1/amenity/charging_station/062_EnBW_Stuttgart_echargingv1
   *
   * <p>it can also be the guid of the placeEntity which is the default
   */
  public String realtimeDataKey;

  // this property might be removed in the future - therefore do not rely on
  // it - check for null
  /**
   * the application scope is determining how for the entity gets seen We plan to allow application
   * scopes for third parties who upload their things You register and get your very own and special
   * applicationScope
   */
  public String applicationScope = applicationScope_unknown;

  public static String applicationScope_unknown = "unknown";
  public static String applicationScope_ordinary_homepage = "community_weblink";
  public static String applicationScope_osm_page = "osm";
  public static String applicationScope_physcial_web = "physical_web";
  /**
   * every restaurant, a vending machine, an atm and so on is a pro service with a verified owner -
   * this means verified data - this maybe more deeply refined lateron
   */
  public static String applicationScope_professional_service = "professional_service";

  // stadtwiki - wikipedia would be an extra data source

  public static String applicationScope_Publictransport = "public_transport";
  public static String applicationScope_news = "news";
  public static String applicationScope_test_entity = "test_service";
  public static String[] defaultShownApplicationScopes =
      new String[] {
        applicationScope_unknown,
        applicationScope_osm_page,
        applicationScope_physcial_web,
        applicationScope_professional_service,
        applicationScope_Publictransport
      };

  public void addShowNotificationOnNear() {
    addTag(tagTypeVisibilityOption, tagTypeVisibilityOptionShowNotificationWithActionsOnNear);
  }

  public boolean isShowNotificationWithActions() {
    return hasTag(
        tagTypeVisibilityOption, tagTypeVisibilityOptionShowNotificationWithActionsOnNear);
  }

  public boolean hasTag(String tagtype, String tagname) {
    if (tags == null || tags.size() == 0) return false;
    for (Tag tag : tags) {
      if (tag.type != null
          && tag.type.equals(tagtype)
          && tag.name != null
          && tag.name.equals(tagname)) return true;
    }
    return false;
  }

  /*
   * boolean ispureservice is a service? - wie w�re das bei einem restaurant
   * bei einer apotheke
   */
  /**
   * the uri associated with the place. This maybe a homepage URL or an app identifier please note
   * that urls only mark adresses on the web - we need uris here a ressource may have several uris -
   * maybe even payment uris https://bitcore.io/api/lib/uri what are the uris... are they actions
   *
   * <p>public String mainWebpageURL;
   */
  public List<Action> actions;

  public ExtendedLocationAggregate locationAggregate;

  // the name of the spot
  // public String caption;
  // human readable description of the spot
  public String description;

  // a main pricture of the geospot
  // public String mainPictureURI;

  // public List<String> pictureURIs;

  // what is this geosspot- a bar? a restaurant?, a social place?... and so on
  // public List<String> categories;

  // tags of this geosspot - limited to a certain amount
  // a tag and how important it is for the place

  public void addTag(String tagtype, String tagname) {
    if (tags == null) tags = new ArrayList<Tag>(1);
    tags.add(new Tag(tagtype, tagname));
  }

  public List<Tag> tags;

  public Tag findFirstTagByType(String type) {
    for (Tag tag : tags) {
      if (tag.type.equals(type)) return tag;
    }
    return null;
  }

  // semantic relation to fields that are linked with that relation
  // public Map<String, List<Field>> properties;

  public PlaceEntityOrHappeningStub getCorrespondingStub() {
    PlaceEntityOrHappeningStub stub = new PlaceEntityOrHappeningStub();
    stub.guid = this.guid;
    stub.isHappening = false;
    stub.usedName = this.name;
    return stub;
  }

  @JsonIgnore
  @Override
  public String toString() {
    return "PlaceEntity [lnsVersion="
        + lnsVersion
        + ", creatorUserNickName="
        + creatorUserNickName
        + ", dataProvider="
        + dataProvider
        + ", latestUpdate="
        + latestUpdate
        + ", dataSourceOrigin="
        + dataSourceOrigin
        + ", guid="
        + guid
        + ", revision="
        + revision
        + ", tmpPlaceEntityClientSideGUID="
        + tmpPlaceEntityClientSideGUID
        + ", id="
        + id
        + ", isPending="
        + isPending
        + ", validityScore="
        + validityScore
        + ", name="
        + name
        + ", accuracy="
        + accuracy
        + ", address="
        + address
        + ", dataSourceDetails="
        + dataSourceDetails
        + ", type="
        + type
        + ", placePrimaryFeature="
        + placePrimaryFeature
        + ", realtimeDataKey="
        + realtimeDataKey
        + ", applicationScope="
        + applicationScope
        + ", actions="
        + actions
        + ", locationAggregate="
        + locationAggregate
        + ", description="
        + description
        + ", tags="
        + tags
        + "]\n";
  }
}
