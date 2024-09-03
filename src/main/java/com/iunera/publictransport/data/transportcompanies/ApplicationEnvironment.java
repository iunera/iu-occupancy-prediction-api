package com.iunera.publictransport.data.transportcompanies;

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

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ApplicationEnvironment {

  @Autowired private Environment environment;

  @PostConstruct
  private void setStaticVars() {
    API_HOST = environment.getProperty("iunera.thisservice.domain", "http://localhost:10099");
  }

  private static String API_HOST = "http://localhost";

  public static String getPubicTransportApiUrl(String stopid) {
    return getApiUrl() + PUBLIC_TRANSPORT_STOP + "/" + stopid;
  }

  private static String getApiUrl() {
    return API_HOST + "/";
  }

  public static final String PUBLIC_TRANSPORT_STOP = "apiv1/public_transport/stop";

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
