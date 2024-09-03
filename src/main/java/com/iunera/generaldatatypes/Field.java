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

import java.util.List;
import java.util.Map;

public class Field {
  public Field() {}

  // the semantic name of the field - indicates the meaning - like caption, subcaption...
  // i118 n
  public String propertyName;

  // e.g. salad, drinks... or tags or separate?
  public List<String> propertyCategories;

  // human readable description... like how a cocktail is produced
  public String description;

  // the values of the property and all of them can have a name- like beginning at- ending at...
  public DataTypeValuePair dataType;

  // in case of composite data the semanic meaning of the
  public Map<String, DataTypeValuePair> compositeDataType;
}
