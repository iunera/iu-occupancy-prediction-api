package com.iunera.publictransport.data.transportcompanies.areas;

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

import de.schildbach.pte.NetworkProvider;

public class SimpleMixMaxArea implements Area {
  float latitudeYmin, longitudeXmin, latitudeYmax, longitudeXmax;
  NetworkProvider provider;

  public SimpleMixMaxArea(
      float latitudeYmin,
      float longitudeXmin,
      float latitudeYmax,
      float longitudeXmax,
      NetworkProvider provider) {
    this.latitudeYmax = latitudeYmax;
    this.latitudeYmin = latitudeYmin;
    this.longitudeXmax = longitudeXmax;
    this.longitudeXmin = longitudeXmin;
    this.provider = provider;
  }

  @Override
  public boolean isInArea(double lon, double lat) {
    return (lat > latitudeYmin && lat < latitudeYmax && lon > longitudeXmin && lon < longitudeXmax);
  }

  @Override
  public NetworkProvider getNetworkProvider() {
    return provider;
  }

  @Override
  public String getNetworkID() {
    return provider.id().toString().toLowerCase();
  }
}
