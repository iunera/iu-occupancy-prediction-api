package com.iunera.druid;

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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "iunera.druid")
public class DruidConfigurationProperties {

  public String getCoordinator() {
    return coordinator;
  }

  public void setCoordinator(String coordinator) {
    this.coordinator = coordinator;
  }

  public String datasource;

  public String getDatasource() {
    return datasource;
  }

  public void setDatasource(String datasource) {
    this.datasource = datasource;
  }

  public String queryendpoint;
  public String coordinator;
  public String queryuser;
  public String querypassword;

  public static class Health {}

  public String getQueryuser() {
    return queryuser;
  }

  public void setQueryuser(String queryuser) {
    this.queryuser = queryuser;
  }

  public String getQuerypassword() {
    return querypassword;
  }

  public void setQuerypassword(String querypassword) {
    this.querypassword = querypassword;
  }

  public String getQueryendpoint() {
    return queryendpoint;
  }

  public void setQueryendpoint(String queryendpoint) {
    this.queryendpoint = queryendpoint;
  }
}
