/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2012 Hannes Janetzek
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.iunera.generaldatatypes.place.place.utils;

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

/** An implementation of the spherical Mercator projection. */
public final class MercatorProjection {
  /** The circumference of the earth at the equator in meters. */
  public static final double EARTH_CIRCUMFERENCE = 40075016.686;

  /** Maximum possible latitude coordinate of the map. */
  public static final double LATITUDE_MAX = 85.05112877980659;

  /** Minimum possible latitude coordinate of the map. */
  public static final double LATITUDE_MIN = -LATITUDE_MAX;

  /** Maximum possible longitude coordinate of the map. */
  public static final double LONGITUDE_MAX = 180;

  /** Minimum possible longitude coordinate of the map. */
  public static final double LONGITUDE_MIN = -LONGITUDE_MAX;

  public static final int TILE_SIZE = 256;

  /**
   * Calculates the distance on the ground that is represented by a single pixel on the map.
   *
   * @param latitude the latitude coordinate at which the resolution should be calculated.
   * @param zoomLevel the zoom level at which the resolution should be calculated.
   * @return the ground resolution at the given latitude and zoom level.
   */
  public static double calculateGroundResolution(double latitude, byte zoomLevel) {
    return Math.cos(latitude * (Math.PI / 180))
        * EARTH_CIRCUMFERENCE
        / ((long) TILE_SIZE << zoomLevel);
  }

  /**
   * Converts a latitude coordinate (in degrees) to a pixel Y coordinate at a certain zoom level.
   *
   * @param latitude the latitude coordinate that should be converted.
   * @param zoomLevel the zoom level at which the coordinate should be converted.
   * @return the pixel Y coordinate of the latitude value.
   */
  public static double latitudeToPixelY(double latitude, byte zoomLevel) {
    double sinLatitude = Math.sin(latitude * (Math.PI / 180));
    return (0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI))
        * ((long) TILE_SIZE << zoomLevel);
  }

  /**
   * Projects a longitude coordinate (in degrees) to the range [0.0,1.0]
   *
   * @param latitude the latitude coordinate that should be converted.
   * @return the position .
   */
  public static double latitudeToY(double latitude) {
    double sinLatitude = Math.sin(latitude * (Math.PI / 180));
    return 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);
  }

  /**
   * Converts a latitude coordinate (in degrees) to a tile Y number at a certain zoom level.
   *
   * @param latitude the latitude coordinate that should be converted.
   * @param zoomLevel the zoom level at which the coordinate should be converted.
   * @return the tile Y number of the latitude value.
   */
  public static long latitudeToTileY(double latitude, byte zoomLevel) {
    return pixelYToTileY(latitudeToPixelY(latitude, zoomLevel), zoomLevel);
  }

  /**
   * @param latitude the latitude value which should be checked.
   * @return the given latitude value, limited to the possible latitude range.
   */
  public static double limitLatitude(double latitude) {
    return Math.max(Math.min(latitude, LATITUDE_MAX), LATITUDE_MIN);
  }

  /**
   * @param longitude the longitude value which should be checked.
   * @return the given longitude value, limited to the possible longitude range.
   */
  public static double limitLongitude(double longitude) {
    return Math.max(Math.min(longitude, LONGITUDE_MAX), LONGITUDE_MIN);
  }

  public static double wrapLongitude(double longitude) {
    if (longitude < -180) return Math.max(Math.min(360 + longitude, LONGITUDE_MAX), LONGITUDE_MIN);
    else if (longitude > 180)
      return Math.max(Math.min(longitude - 360, LONGITUDE_MAX), LONGITUDE_MIN);

    return longitude;
  }

  /**
   * Converts a longitude coordinate (in degrees) to a pixel X coordinate at a certain zoom level.
   *
   * @param longitude the longitude coordinate that should be converted.
   * @param zoomLevel the zoom level at which the coordinate should be converted.
   * @return the pixel X coordinate of the longitude value.
   */
  public static double longitudeToPixelX(double longitude, byte zoomLevel) {
    return (longitude + 180) / 360 * ((long) TILE_SIZE << zoomLevel);
  }

  /**
   * Projects a longitude coordinate (in degrees) to the range [0.0,1.0]
   *
   * @param longitude the longitude coordinate that should be converted.
   * @return the position .
   */
  public static double longitudeToX(double longitude) {
    return (longitude + 180) / 360;
  }

  /**
   * Converts a longitude coordinate (in degrees) to the tile X number at a certain zoom level.
   *
   * @param longitude the longitude coordinate that should be converted.
   * @param zoomLevel the zoom level at which the coordinate should be converted.
   * @return the tile X number of the longitude value.
   */
  public static long longitudeToTileX(double longitude, byte zoomLevel) {
    return pixelXToTileX(longitudeToPixelX(longitude, zoomLevel), zoomLevel);
  }

  /**
   * Converts a pixel X coordinate at a certain zoom level to a longitude coordinate.
   *
   * @param pixelX the pixel X coordinate that should be converted.
   * @param zoomLevel the zoom level at which the coordinate should be converted.
   * @return the longitude value of the pixel X coordinate.
   */
  public static double pixelXToLongitude(double pixelX, byte zoomLevel) {
    return 360 * ((pixelX / ((long) TILE_SIZE << zoomLevel)) - 0.5);
  }

  /**
   * Converts a pixel X coordinate to the tile X number.
   *
   * @param pixelX the pixel X coordinate that should be converted.
   * @param zoomLevel the zoom level at which the coordinate should be converted.
   * @return the tile X number.
   */
  public static int pixelXToTileX(double pixelX, byte zoomLevel) {
    return (int) Math.min(Math.max(pixelX / TILE_SIZE, 0), Math.pow(2, zoomLevel) - 1);
  }

  /**
   * Converts a pixel Y coordinate at a certain zoom level to a latitude coordinate.
   *
   * @param pixelY the pixel Y coordinate that should be converted.
   * @param zoomLevel the zoom level at which the coordinate should be converted.
   * @return the latitude value of the pixel Y coordinate.
   */
  public static double pixelYToLatitude(double pixelY, byte zoomLevel) {
    double y = 0.5 - (pixelY / ((long) TILE_SIZE << zoomLevel));
    return 90 - 360 * Math.atan(Math.exp(-y * (2 * Math.PI))) / Math.PI;
  }

  /**
   * Converts a pixel Y coordinate to the tile Y number.
   *
   * @param pixelY the pixel Y coordinate that should be converted.
   * @param zoomLevel the zoom level at which the coordinate should be converted.
   * @return the tile Y number.
   */
  public static int pixelYToTileY(double pixelY, byte zoomLevel) {
    return (int) Math.min(Math.max(pixelY / TILE_SIZE, 0), Math.pow(2, zoomLevel) - 1);
  }

  /**
   * Converts a tile X number at a certain zoom level to a longitude coordinate.
   *
   * @param tileX the tile X number that should be converted.
   * @param zoomLevel the zoom level at which the number should be converted.
   * @return the longitude value of the tile X number.
   */
  public static double tileXToLongitude(long tileX, byte zoomLevel) {
    return pixelXToLongitude(tileX * TILE_SIZE, zoomLevel);
  }

  /**
   * Converts a tile Y number at a certain zoom level to a latitude coordinate.
   *
   * @param tileY the tile Y number that should be converted.
   * @param zoomLevel the zoom level at which the number should be converted.
   * @return the latitude value of the tile Y number.
   */
  public static double tileYToLatitude(long tileY, byte zoomLevel) {
    return pixelYToLatitude(tileY * TILE_SIZE, zoomLevel);
  }

  private MercatorProjection() {
    throw new IllegalStateException();
  }
}
