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
[Open Compensation Token License, Version 0.20](https://github.com/open-compensation-token-license/license/blob/main/LICENSE.md)

