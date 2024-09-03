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
import java.util.ArrayList;
import java.util.List;

/** this class represents a possible action at a place */
@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Action {
  /**
   * logical classification of the action to allow aggregations Furthermore, the action type can be
   * used to group actions together, like multiple payment options with different properties
   *
   * <p>or ordering via different systems * is it a preview action, may be null to indicate a normal
   * action if the preview fails by default always the action gets shown the action type influences
   * how it is rendered and shown the action type defines witch properties the action may have
   */
  private static String actiontypetagtype = "actionType";

  public static String mainWebsiteActionType = "mainWebsiteURL";
  public static String payActionType = "pay";
  public static String previewActionType = "preview";
  public static String navigateToActionType = "navigateTo";
  public static String filterActionType = "lnsFilter";

  public static Action createViewWebsiteAction(String url) {
    return createAction(mainWebsiteActionType, "visit_main_website", "Website", null, url, null);
  }

  public static Action createAction(
      String actiontype,
      String name,
      String label,
      String actionURI,
      String fallbackUrl,
      String dataUrl) {
    Action a = new Action();
    if (actiontype != null && !actiontype.equals("")) {
      a.tags = new ArrayList<Tag>(1);
      a.tags.add(new Tag(actiontypetagtype, actiontype));
    }

    a.fallbackURL = fallbackUrl;
    a.name = name;
    a.label = label;
    a.dataURL = dataUrl;
    a.actionURI = actionURI;
    return a;
  }

  public static Action createFilterAction(String tag, String label) {
    return createAction(
        Action.filterActionType, "equalspots", label, "lns://?tagname=" + tag, null, null);
  }

  public static Action createParkingPreviewAction(String realtimeDataUri) {
    return createAction(
        Action.previewActionType, "parkingInformation", "Free Places", null, null, realtimeDataUri);
  }

  public static Action createChargingPreviewAction(String realtimeDataUri) {
    return createAction(
        Action.previewActionType,
        "chargingStationInformation",
        "Free Places",
        null,
        null,
        realtimeDataUri);
  }

  public static Action createAiqualityNowPreviewAction(String realtimeDataUri) {
    return createAction(
        Action.previewActionType,
        "chargingStationInformation",
        "Free Places",
        null,
        null,
        realtimeDataUri);
  }

  public static Action createNavigateAction(PlaceEntity currententity) {
    return createAction(
        navigateToActionType,
        "navigateTo",
        "Map",
        "geo:0,0?q="
            + currententity.locationAggregate.centers.get(0).getLatitude()
            + ","
            + currententity.locationAggregate.centers.get(0).getLongitude()
            + "("
            + currententity.name
            + ")",
        null,
        null);
  }

  public boolean isMainWebsiteAction() {
    return hasActionType(mainWebsiteActionType);
  }

  public boolean hasActionType(String actionType) {
    if (tags == null || tags.size() == 0) return false;
    for (Tag tag : tags) {
      if (tag.type != null
          && tag.type.equals(actiontypetagtype)
          && tag.name != null
          && tag.name.equals(actionType)) return true;
    }
    return false;
  }

  public boolean isPreviewAction() {
    return hasActionType(previewActionType);
  }

  //
  // public static String openApp = "openApp";
  // denke dar�ber nach �ber orte
  /*
   * actions eine registry die auch andere benutzen actions die
   * unterschiedlich sind ja nach application - also eine bedienung hat andere
   * actions als ein gast --> geht
   *
   * ok contexte - an einem beacon wlan k�nnen verschiedene pl�tze angelegt
   * werden filterbar �ber place categroy - ? wirklich istd as richtig
   */
  // Action -
  // actionname + uri +
  // Fallbackurl?

  /**
   * the name of the action - should be unique - like pay with bitcoin - pay with paypal or
   * paydirekt influences how the action gets processed
   */
  public String name;
  /**
   * the string that is shown on the action - translatable with i118n - maybe removed and resolved
   * by the name in the future the label
   */
  public String label;

  /** the tags of the action maybe used to indicate features of the action like bus departures */
  public List<Tag> tags;
  // properties that may differ from action to action

  /**
   * the data url of the action - e.g. if we load data for a preview or whatsoever the url where we
   * can retrieve the data this maybe null for most actions like opening an app and so on.. for a
   * menu card this maybe the json for the menu and so on - not all action types may have this
   * property
   */
  public String dataURL;

  /**
   * the uri to process for the action. Maybe also another intent/app in case there is no specific
   * app this property maybe null and only the fallback is filled
   */
  public String actionURI;
  /**
   * the web url for the action- this maybe used if the needed intent to process the url is not
   * installed only available if a website is available- maybe null
   */
  public String fallbackURL;
  // this flag indicates if always the fallback url should be used
  // the key reason is that if an action creates a JSON or another html call
  // this should never be shown to the user
  // we do not store it in the database currently, because the
  // public boolean alwaysUseFallBackUrl=true;
  /*
   * public String previewtemplateUri; public String preview
   */
  // payload von der action...
  // Actionname= Open Webpage
  // actionuri= homepageurl
  // application
  // string actiontype (like fiering it in the background and creating a
  // preview in the notification)
  // string actionmetainformation - einfach was eine action zum verarbeiten
  // rbaucht

  // aber generell - diese sache mit der url alles hat eine homepage - und die
  // homepage ist das vom provider
  // actions sind dann teile der homepage - homepage ist sowas wie die
  // komplett "app"

  // und nur app an location also nur actions/uris ohne homepage?
  // das geht auch - dann sind halt nur actions da und daher hei�t es auch
  // nicht url sondern webpage..

  // privathomepages haben da keine actions

  // abfahrtszeieten im handy ticketer anzeigen - geht action ist ja
  // abfahrtszeiten anziegen- daraus kann ich dann einen call machen - sowas
  // wie preview action

  // kurzzstrecken ticket kaufen: ticket kaufen ausw�hlen - radio buttons
  // bekommen - app starten

  // im cafe bezahlen - es gibt einen payment knopf der hei�t zahlen - die url
  // von dem tisch wird immer geupdated - da stehen die getr�nke drauf man
  // sucht sie sich aus und zahlt
  // es gibt einen bestellen knopf, der zum bestellen und zahlen dient

  // stell dir mal payment vor oder die live anzeige von bahn fahrpl�nen im
  // notification center

  /*
   * es sollte primary url sein
   *
   * public action - action: order here, pay, buy ticket, anrufen, open app-
   * der knopf den ich direkt am ort des geschens am meisten brauche im
   * krankenhaus schwester rufen- oder here f�hrt zur eorder, vistenkarten
   * mitnehmen oder dalassen
   *
   * context button... open webpage ist nur eine aktion- 2 push notifications:
   * 1 notification f�r den platz der wirklich super nahe da ist mit 2 context
   * buttons 2. notification - hier kommt man zu allen homepages in der n�he
   *
   * wie wirkt sich der tag filter aus?
   */
}
