## Synopsis

This repo contains:

- a Dockerfile and docker-compose file to run a Yellowfin instance locally.
- a sbt project to run a client using Yellowfin Web Services API to talk to a
  Yellowfin instance.

## Pre-requisite

- Docker and docker-compose to spin up a local Yellowfin instance.
- scala, sbt, and optionally IntelliJ to work with the sbt project.

## How to run a local Yellowfin Instance

- download a Yellowfin installation jar and place it inside `yellowfin` folder.
- edit `.env` and update `YELLOWFIN_VERSION` to match the Yellowfin version of
  the installation jar.
- run `docker-compose up -d`.

## How to run Yellowfin Web Services client

- import the sbt project into IntelliJ.
- update `src/main/resources/application.conf` to point the client at a running
  instance of Yellowfin.
- update `src/main/scala-2.12/com/comprehend/yfws_client/Main.scala` to do
  something useful.
- run `com.comprehend.yfws_client.Main` as the main object.

## Notes

- Yellowfin container will install Yellowfin in the container if Yellowfin is
  not already installed; this usually takes about 2-3 minutes.
- uses an unsecured Postgres for persistence and the DB is not backed by disk.
  So if you have deleted the Postgres container, then you should probably
  delete `yellowfin_installation` folder as well to force re-installation on
  next launch.
- mounts the entire Yellowfin app installation on host at `yellowfin_installation`.
- Yellowfin will be listening on port 7900 on the host by default; this can be
  changed by editing `YELLOWFIN_EXTERNAL_PORT` in `.env`.
- Postgres will be listening on port 7800 on the host by default; this can be
  changed by editing `POSTGRES_EXTERNAL_PORT` in `.env`.
