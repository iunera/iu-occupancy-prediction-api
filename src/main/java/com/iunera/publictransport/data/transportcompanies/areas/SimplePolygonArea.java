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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

public class SimplePolygonArea implements Area {
  private Polygon polygon;
  private NetworkProvider provider;
  private GeometryFactory geometryFactory = new GeometryFactory();

  public SimplePolygonArea(NetworkProvider provider, double[][] polygonpointslonlat) {
    int length = polygonpointslonlat.length;
    boolean closepolygon = false;
    // check if the polygon is closed if not assume the last point should be the first
    if (polygonpointslonlat[0][0] != polygonpointslonlat[polygonpointslonlat.length - 1][0]
        || polygonpointslonlat[0][1] != polygonpointslonlat[polygonpointslonlat.length - 1][1]) {
      length++;
      closepolygon = true;
    }

    Coordinate[] coordinates = new Coordinate[length];

    for (int i = 0; i < polygonpointslonlat.length; i++) {

      coordinates[i] = new Coordinate(polygonpointslonlat[i][0], polygonpointslonlat[i][1]);
      ;
    }

    if (closepolygon) {
      coordinates[length - 1] =
          new Coordinate(polygonpointslonlat[0][0], polygonpointslonlat[0][1]);
      ;
    }

    polygon = geometryFactory.createPolygon(coordinates);

    this.provider = provider;
  }

  @Override
  public boolean isInArea(double lon, double lat) {
    Coordinate p = new Coordinate(lon, lat);
    return (polygon.contains(geometryFactory.createPoint(p)));
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
