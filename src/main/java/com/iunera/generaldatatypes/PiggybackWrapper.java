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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PiggybackWrapper<T> {

  public static final ObjectMapper objMapper = getObjectMapper();

  private static ObjectMapper getObjectMapper() {
    ObjectMapper o = new ObjectMapper();
    // o=o.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
    return o;
  }

  public Map<String, String> piggyBackStuff;

  /**
   * usage: <code>
   * 	returnValue.addInformation(WorldAnnotationResponse.class.getSimpleName(), worldAnnotationResponse);
   * </code>
   */
  public void addInformation(String key, Object informationToStringify) {
    try {
      if (piggyBackStuff == null) piggyBackStuff = new HashMap<String, String>();
      piggyBackStuff.put(key, objMapper.writeValueAsString(informationToStringify));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public PiggybackWrapper(T object) {
    originalResponseBody = object;
  }

  public T originalResponseBody;
}
