package com.iunera.druid.impl;

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

import com.iunera.druid.DruidConfigurationProperties;
import com.iunera.druid.DruidDataRepository;
import com.iunera.druid.model.DruidGroupByResult;
import com.iunera.publictransport.occupation.historicdata.OccupationForecastResultRow;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
@Configuration
public class HistoricDataRepositoryDruidImpl implements DruidDataRepository {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  DruidConfigurationProperties druidConfigurationProperties;

  private RestTemplate rt = new RestTemplate();

  @Autowired
  public HistoricDataRepositoryDruidImpl(
      DruidConfigurationProperties iuneraConfigurationProperties) {
    this.druidConfigurationProperties = iuneraConfigurationProperties;

    // only use basic auth if username and password are specified
    if (iuneraConfigurationProperties.getQueryuser() != null
        && iuneraConfigurationProperties.getQuerypassword() != null
        && !iuneraConfigurationProperties.getQueryuser().isEmpty()
        && !iuneraConfigurationProperties.getQuerypassword().isEmpty()
        && !iuneraConfigurationProperties.getQueryuser().equals("NONE")
        && !iuneraConfigurationProperties.getQuerypassword().equals("NONE")) {
      rt.getInterceptors()
          .add(
              new BasicAuthenticationInterceptor(
                  iuneraConfigurationProperties.getQueryuser(),
                  iuneraConfigurationProperties.getQuerypassword()));
    }

    rt.getInterceptors()
        .add(
            new ClientHttpRequestInterceptor() {

              @Override
              public ClientHttpResponse intercept(
                  HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                  throws IOException {
                request.getHeaders().set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                return execution.execute(request, body);
              }
            });

    rt.getMessageConverters()
        .add(0, new StringHttpMessageConverter(Charset.forName(StandardCharsets.UTF_8.name())));
  }

  @Override
  public List<OccupationForecastResultRow> query(
      String query,
      LinkedHashMap<String, Object> variables,
      Class<OccupationForecastResultRow> result) {

    if (query.contains("groupBy")) return getGroupBy(query, variables, result);
    throw new UnsupportedOperationException("query operation not supported yet:" + query);
  }

  public List<OccupationForecastResultRow> getGroupBy(
      String query,
      LinkedHashMap<String, Object> variables,
      Class<OccupationForecastResultRow> result) {

    if (variables != null)
      for (Entry<String, Object> parameter : variables.entrySet()) {
        query = query.replace(":" + parameter.getKey(), parameter.getValue().toString());
      }

    ResponseEntity<DruidGroupByResult<OccupationForecastResultRow>> res =
        rt.exchange(
            druidConfigurationProperties.getQueryendpoint(),
            HttpMethod.POST,
            new HttpEntity<String>(query),
            new ParameterizedTypeReference<DruidGroupByResult<OccupationForecastResultRow>>() {});
    DruidGroupByResult<OccupationForecastResultRow> body = res.getBody();

    return body.stream().map(record -> record.event).collect(Collectors.toList());
  }

  private static List<Field> getAllModelFields(Class aClass) {
    List<Field> fields = new ArrayList<>();
    do {
      Collections.addAll(fields, aClass.getDeclaredFields());
      aClass = aClass.getSuperclass();
    } while (aClass != null);
    return fields;
  }
}
