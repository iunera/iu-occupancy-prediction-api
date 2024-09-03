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

public class MultiPolygonArea implements Area {
  private org.locationtech.jts.geom.Polygon[] polygons;
  private NetworkProvider provider;
  // Create a GeometryFactory if you don't have one already
  private GeometryFactory geometryFactory = new GeometryFactory();

  public MultiPolygonArea(
      NetworkProvider provider, ProviderAreaDefinition polygonpointslonlatareas) {

    polygons = new org.locationtech.jts.geom.Polygon[polygonpointslonlatareas.polygons.length];
    int polynumber = 0;
    for (ProviderAreaPolygonDefinition polygonpointslonlat : polygonpointslonlatareas.polygons) {
      int length = polygonpointslonlat.points.length;
      boolean closepolygon = false;
      // check if the polygon is closed if not assume the last point should be the
      // first
      if (polygonpointslonlat.points[0][0]
              != polygonpointslonlat.points[polygonpointslonlat.points.length - 1][0]
          || polygonpointslonlat.points[0][1]
              != polygonpointslonlat.points[polygonpointslonlat.points.length - 1][1]) {
        length++;
        closepolygon = true;
      }

      Coordinate[] coordinates = new Coordinate[length];

      for (int i = 0; i < polygonpointslonlat.points.length; i++) {

        coordinates[i] =
            new Coordinate(polygonpointslonlat.points[i][0], polygonpointslonlat.points[i][1]);
      }

      if (closepolygon) {
        coordinates[length - 1] =
            new Coordinate(polygonpointslonlat.points[0][0], polygonpointslonlat.points[0][1]);
        ;
      }

      polygons[polynumber] = geometryFactory.createPolygon(coordinates);
      polynumber++;
    }

    this.provider = provider;
  }

  @Override
  public boolean isInArea(double lon, double lat) {
    Coordinate p = new Coordinate(lon, lat);

    for (org.locationtech.jts.geom.Polygon polygon : polygons) {

      if (polygon.contains(geometryFactory.createPoint(p))) return true;
    }
    return false;
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
