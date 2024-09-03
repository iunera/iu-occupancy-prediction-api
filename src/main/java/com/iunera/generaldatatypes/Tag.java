package com.iunera.generaldatatypes;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tag {

  public Tag() {}

  public Tag(String type, String name) {
    this.type = type;
    this.name = name;
  }

  public String name;

  // the id of the tag- unique - logical and very likely needed in the future
  // but to complex to build it in and use it currently
  // public String tagID;

  // priavte stappz- geht auch �ber tags= einfach tagkind privacy

  // stappz die nur einer gruppe sichtbar sind - geht auch �ber tagkind
  // - generell private gruppen tags die nur eine authorisierte gruppe
  // verteilen darf

  // Tag wird gebraucht in saving user; worldannotation, userprofile

  // und auch die notification histroy eine collection wo die histroy von
  // angeschauten notifications des users gepflegt wird

  // category, trip, personal, hashtag, group
  // problem: wenn es so etwas wie eine itiniary ist, dann wird unter
  // umst�nden noch mehr information ben�tigt
  // wie nummer des stappz
  public String type;

  // maybe the number of the stap in a trip or whatsoever
  // or maybe the count use of an hashtag - problem hier kann nochmal eine
  // unterklassifikation sein
  public Map<String, String> properties;

  public static String tagKindHashtag = "hashtag";
  public static String tagKindTrip = "trip";
  // a well known category- like restaurant
  public static String tagKindKeyCategory = "keyCategory";
  // tags copied by osm
  public static String tagKindOsmTag = "osmTag";

  public static final String opening_hours = "opening_hours";

  private static char hashtagsymbol = '#';
  private static Pattern hasttagpattern = Pattern.compile(hashtagsymbol + "(\\S+)");

  public static List<String> dedupTagStringList(List<String> list) {

    // add elements to al, including duplicates
    Set<String> hs = new HashSet<>();
    hs.addAll(list);
    try {
      list.clear();
    } catch (UnsupportedOperationException e) {
      // in case of unsupported operations exceptions because of fixed
      // list sizes e.g. while usage of Arrays.asList()
      list = new ArrayList<String>(list.size());
    }
    list.addAll(hs);

    Collections.sort(list);

    return list;
  }

  public static List<String> extractHashTagsFromString(String text) {

    Matcher tagsInText = hasttagpattern.matcher(text);

    List<String> parsedTags = new ArrayList<String>();

    while (tagsInText.find()) {
      String tag = tagsInText.group(1).replace('_', ' ');
      parsedTags.add(tag);
    }

    return dedupTagStringList(parsedTags);
  }

  public static List<Tag> convertHashTagStringListToWorldAnnotationHashTagList(
      List<String> stringList) {

    stringList = dedupTagStringList(stringList);

    List<Tag> hashTags = new ArrayList<Tag>();

    for (String tagName : stringList) {
      Tag tag = new Tag();
      tag.type = Tag.tagKindHashtag;
      tag.name = tagName;

      hashTags.add(tag);
    }

    return hashTags;
  }

  @JsonIgnore
  @Override
  public String toString() {
    return "Tag [name=" + name + ", type=" + type + ", properties=" + properties + "]\n";
  }
}
