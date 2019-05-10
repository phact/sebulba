# Sebulba

A drone race coordinator built on DSE for DataStax Accelerate

Sebulba uses quarkus which means you need these prereqs to build:

 - JDK 1.8+ installed with `JAVA_HOME` configured appropriately
 - Apache Maven 3.5.3+
 - DSE or cassandra running on localhost

## For dev

./mvnw compile quarkus:dev  

## For prod binaries (no JDK / JRE needed to run these!)

./mvnw package -Pnative  

## For docker container (slim, small, and fast)

./mvnw package -Pnative -Dnative-image.docker-build=true 

## UI Dev

To run the UI, head into the `ui` folder and follow the directions

## UI Production Build

To build the UI for production, run `npm run build` in the `ui` directory, a production optomized version of the UI will be placed in the right directory for the quarkus app to serve it.
