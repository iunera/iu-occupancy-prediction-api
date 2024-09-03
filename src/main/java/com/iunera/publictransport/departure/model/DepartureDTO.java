package com.iunera.publictransport.departure.model;

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
import java.time.Instant;
import java.util.Comparator;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartureDTO {

  public DepartureDTO() {}

  public DepartureDTO(Instant plannedTime, Instant predictedTime, LineDTO line,
      PositionDTO position, String destination) {
    this.plannedTime = plannedTime;
    this.predictedTime = predictedTime;
    this.line = line;
    this.position = position;
    this.destination = destination;
  }

  public DepartureDTO(Instant plannedTime, Instant predictedTime, LineDTO lineDTO,
      PositionDTO positionDTO, String destination, EPerceivedOccupation predictedOccupation) {
    this(plannedTime, predictedTime, lineDTO, positionDTO, destination);
    this.predictedOccupation = predictedOccupation;
  }

  public Instant plannedTime;
  public Instant predictedTime;
  public LineDTO line;
  public PositionDTO position;
  public String destination;
  public EPerceivedOccupation predictedOccupation = EPerceivedOccupation.UNKNOWN;

  // public int[] capacity;
  // public String message;
  /*
   * public boolean equals(Object object) { return this.hashCode() == object.hashCode(); }
   *
   * public int hashCode() { return (""+plannedTime+predictedTime+destination+"").hashCode(); }
   */
  public static final Comparator<DepartureDTO> ORIGINAL_TIME_COMPARATOR =
      new Comparator<DepartureDTO>() {
        public int compare(final DepartureDTO departure0, final DepartureDTO departure1) {
          return departure0.plannedTime.compareTo(departure1.plannedTime);
        }
      };

  public static class PredictedAndPlannedDeparatureTimeComparator
      implements Comparator<DepartureDTO> {

    public PredictedAndPlannedDeparatureTimeComparator() {}

    @Override
    public int compare(DepartureDTO lhs, DepartureDTO rhs) {
      try {

        if (lhs.predictedTime != null && rhs.predictedTime != null)
          return lhs.predictedTime.compareTo(rhs.predictedTime);

        if (lhs.predictedTime == null && rhs.predictedTime != null)
          return lhs.plannedTime.compareTo(rhs.predictedTime);

        return lhs.plannedTime.compareTo(rhs.plannedTime);

      } catch (Exception e) {

        return Integer.MIN_VALUE;
      }
    }
  }

  public static final Comparator<DepartureDTO> TIME_COMPARATOR_RESPECTING_DELAYS =
      new PredictedAndPlannedDeparatureTimeComparator();;
}
