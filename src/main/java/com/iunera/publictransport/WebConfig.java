package com.iunera.publictransport;

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

import javax.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class WebConfig {
  @Bean
  public Filter filter() {
    ShallowEtagHeaderFilter filter = new ShallowEtagHeaderFilter();
    // A weakly validating ETag match only indicates that the two representations
    // are semantically equivalent, meaning that for practical purposes they are
    // interchangeable and that cached copies can be used. However, the resource
    // representations are not necessarily byte-for-byte identical, and thus weak
    // ETags are not suitable for byte-range requests. Weak ETags may be useful for
    // cases in which strong ETags are impractical for a Web server to generate,
    // such as with dynamically-generated content.

    // we use weak etags to allow GZipping the content later in a reverse proxy
    filter.setWriteWeakETag(true);
    return filter;
  }
}
