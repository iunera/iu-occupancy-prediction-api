# iu-occupancy-prediction-api
This project is generating occupancy forecasts based on historic data.
Thereby, it merges EFA information with the occupancy forecasts an provides an API for enriched results.

The specific contributions of this project are:
- A method to merge real-time information from EFA systems with forecasting data
- A concept and example how to integrate Apache Druid queries in a spring boot application (see the com.iunera.druid and the occupation.historicdata package)
- Definition of longitude latitude defined polygon areas of public transport providers areas to match the relevant data to the geopoints (resources providerareas as geojson). The complete concept of this approach can be used to do the same definitions for each public transport area to select suitable datasources based on geo points.
- Adaptive histogram based regression forecasting based on time series data for occupancy. We provide an example implementation for an adaptive forecasting method for sparse data (see queries in the resources and associated services and APIs) 


# Remarks 
Generally this project is not completely groomed and respresents a collection of concepts and how thing scan finally be realized.
In order to compile the code one needs to link the following required resources:
- We thank the work on https://oeffi.schildbach.de/ / https://github.com/schildbach/public-transport-enabler that we used as library to integrate the EFA data. 
- We thank the work on https://github.com/opendatalab-de/geojson-jackson that we use for defining Geojson structures.


# License
## [Open Compensation Token License, Version 0.20](https://github.com/open-compensation-token-license/license/blob/main/LICENSE.md)

```
Licensed under the OPEN COMPENSATION TOKEN LICENSE (the "License").

You may not use this file except in compliance with the License.

You may obtain a copy of the License at
<https://github.com/open-compensation-token-license/license/blob/main/LICENSE.md>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.
See the License for the specific language governing permissions and
limitations under the License.

@octl.sid: 1b6f7a5d-8dcf-44f1-b03a-77af04433496
```
* Why we did [choose the OCTL](https://www.license-token.com/why-octl)
* Why we [do NOT apply Apache 2.0 License] (https://www.license-token.com/wiki/the-downside-of-apache-license-and-why-i-never-would-use-it)?
