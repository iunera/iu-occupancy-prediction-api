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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.schildbach.pte.AvvProvider;
import de.schildbach.pte.BayernProvider;
import de.schildbach.pte.BvgProvider;
import de.schildbach.pte.DubProvider;
import de.schildbach.pte.GvhProvider;
import de.schildbach.pte.KvvProvider;
import de.schildbach.pte.MvgProvider;
import de.schildbach.pte.MvvProvider;
import de.schildbach.pte.NasaProvider;
import de.schildbach.pte.NvvProvider;
import de.schildbach.pte.VbbProvider;
import de.schildbach.pte.VbnProvider;
import de.schildbach.pte.VgsProvider;
import de.schildbach.pte.VmsProvider;
import de.schildbach.pte.VrnProvider;
import de.schildbach.pte.VrrProvider;
import de.schildbach.pte.VrsProvider;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class ProviderFactory {

  // find further provider with access in:
  // https://gitlab.com/oeffi/oeffi/-/blob/master/oeffi/src/de/schildbach/oeffi/network/NetworkProviderFactory.java

  // GeoJson for regions: http://opendatalab.de/projects/geojson-utilities/
  private ObjectMapper om = new ObjectMapper();

  @Bean
  public Map<String, Area> getAreas() {

    List<Area> areas = new ArrayList<>();
    try {
      /*
       * Problem of unfound config files: Files cannot be resolved to absolute file
       * path because it does not reside in the file system: jar
       *
       * But using new ClassPathResource(filename).getInputStream() will solve this
       * problem. The reason is that the configuration file in the jar does not exist
       * in the operating system's file tree,so must use getInputStream().
       */
      areas.add(
          new MultiPolygonArea(
              new VrnProvider(),
              om.readValue(
                  new ClassPathResource("providerareas/vrn_rnn.json").getInputStream(),
                  ProviderAreaDefinition.class)));
      //
      areas.add(new SimpleMixMaxArea(48.750704f, 8.196176f, 49.202411f, 9.3f, new KvvProvider()));

      areas.add(
          new SimpleMixMaxArea(
              47.3056346f, 9.5872153f, 50.597487f, 13.522509f, new BayernProvider()));
      areas.add(
          new MultiPolygonArea(
              new NvvProvider("auth"),
              om.readValue(
                  new ClassPathResource("providerareas/rmvnvv.json").getInputStream(),
                  ProviderAreaDefinition.class)));
      //	 berlin ubahn
      areas.add(
          new SimpleMixMaxArea(
              52.2f,
              13.0f,
              52.674189f,
              13.757130f,
              new BvgProvider("{\"aid\":\"1Rxs112shyHLatUX4fofnmdxK\",\"type\":\"AID\"}")));

      // berlin brandenburg
      areas.add(
          new MultiPolygonArea(
              new VbbProvider(
                  "{\"type\":\"AID\",\"aid\":\"hafas-vbb-apps\"}",
                  "RCTJM2fFxFfxxQfI".getBytes(StandardCharsets.UTF_8)),
              om.readValue(
                  new ClassPathResource("providerareas/vbb.json").getInputStream(),
                  ProviderAreaDefinition.class)));

      // emden + ZVBN - TODO: proper area definition
      areas.add(
          new SimpleMixMaxArea(
              52.495641f,
              7.586751f,
              53.448299f,
              9.509358f,
              new VbnProvider(
                  "{\"aid\":\"rnOHBWhesvc7gFkd\",\"type\":\"AID\"}",
                  "SP31mBufSyCLmNxp".getBytes(StandardCharsets.UTF_8))));

      // m�nchen
      areas.add(
          new MultiPolygonArea(
              new MvvProvider(),
              om.readValue(
                  new ClassPathResource("providerareas/mvv.json").getInputStream(),
                  ProviderAreaDefinition.class)));

      // saarland
      areas.add(
          new MultiPolygonArea(
              new VgsProvider("dd", new byte[3]),
              om.readValue(
                  new ClassPathResource("providerareas/vgs.json").getInputStream(),
                  ProviderAreaDefinition.class)));

      // NRW
      areas.add(
          new MultiPolygonArea(
              new VrrProvider(),
              om.readValue(
                  new ClassPathResource("providerareas/vrr.json").getInputStream(),
                  ProviderAreaDefinition.class)));

      areas.add(
          new MultiPolygonArea(
              new AvvProvider(),
              om.readValue(
                  new ClassPathResource("providerareas/avv.json").getInputStream(),
                  ProviderAreaDefinition.class)));

      areas.add(
          new MultiPolygonArea(
              new VrsProvider(),
              om.readValue(
                  new ClassPathResource("providerareas/vrs.json").getInputStream(),
                  ProviderAreaDefinition.class)));
      // MVG M�rkische Verkehrsgesellschaft GmbH
      // L�denscheid - Ruhr lippe - VRL??
      areas.add(
          new MultiPolygonArea(
              new MvgProvider(),
              om.readValue(
                  new ClassPathResource("providerareas/mvg.json").getInputStream(),
                  ProviderAreaDefinition.class)));

      // neue...
      // braunschweig
      //			areas.add(new MultiPolygonArea(new BsvagProvider(), om.readValue(
      //					new ClassPathResource("providerareas/bsvag.json"), ProviderAreaDefinition.class)));

      // Gro�raum Verkehr Hannover GVH
      areas.add(
          new MultiPolygonArea(
              new GvhProvider(),
              om.readValue(
                  new ClassPathResource("providerareas/gvh.json").getInputStream(),
                  ProviderAreaDefinition.class)));

      // VMS Verkehrsverbund Mittelsachsen
      areas.add(
          new MultiPolygonArea(
              new VmsProvider(),
              om.readValue(
                  new ClassPathResource("providerareas/vms.json").getInputStream(),
                  ProviderAreaDefinition.class)));

      // Sachsen-Anhalt - Mitteldeutscher Verkehrsverbund
      areas.add(
          new MultiPolygonArea(
              new NasaProvider("dd"),
              om.readValue(
                  new ClassPathResource("providerareas/mdv.json").getInputStream(),
                  ProviderAreaDefinition.class)));

      // dubai
      areas.add(
          new SimpleMixMaxArea(
              24.7890449f, 54.6248103f, 25.382307f, 55.639114f, new DubProvider()));

    } catch (Exception e) {
      e.printStackTrace();
    }

    Map<String, Area> idAreas = new HashMap<String, Area>();
    for (Area area : areas) {
      idAreas.put(area.getNetworkProvider().id().toString().toLowerCase(), area);
    }

    return idAreas;
  }
  // --unbekannt f�r was
  // public Area vvm = new SimpleMixMaxArea(new VvmProvider(),
  // om.readValue(new ClassPathResource("providerareas/gvh.json"),
  // ProviderAreaDefinition.class) );
  // freiburg - ist derzeit im bw provider
  // public Area vagfr = new SimpleMixMaxArea(24.7890449f, 54.6248103f,
  // 25.382307f, 55.639114f, new VagfrProvider());

  // Schleswig Hohlstein Provider
  // public Area sh = new SimpleMixMaxArea(24.7890449f, 54.6248103f,
  // 25.382307f, 55.639114f, new ShProvider());
  // Verkehrsverbund Bremen/Niedersachsen api authorisation needed
  // public Area vbn = new SimpleMixMaxArea(24.7890449f, 54.6248103f,
  // 25.382307f, 55.639114f, new VbnProvider(jsonApiAuthorization));

  // se provider = schweden?
  // railteam = rt provider
  //// public  Area bw = new SimpleMixMaxArea(47.546343f, 7.446198f,
  //// 49.848104f, 10.286164f, new BwProvider());

  // always define from inner to the outer
  //	public Area[] areas = new Area[] { vrn_rnn, kvv,
  ////				//bw,
  //			bayern, rmvnvv, vbb, bvg,
  ////				//bvb,
  //			mvv, vgs, vrr, avv, vrs, bsvag, gvh, vms, mdv, dubai
  //
  //	};

  //	 berlin busse
  //	areas.add(new SimpleMixMaxArea(52.341100f, 13.074604f,
  //			52.674189f, 13.757130f, new Bvb("dd")));

}
