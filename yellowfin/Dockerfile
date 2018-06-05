FROM ubuntu:14.04

RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" \
  | tee "/etc/apt/sources.list.d/webupd8team-java.list" \
  && echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" \
  | tee -a "/etc/apt/sources.list.d/webupd8team-java.list" \
  && apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886 \
  && apt-get update -qq
RUN echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" \
  | /usr/bin/debconf-set-selections \
  && apt-get -qq -y --force-yes install oracle-java8-installer wget

WORKDIR /root

ARG YELLOWFIN_VERSION
ENV YELLOWFIN_JAR yellowfin-$YELLOWFIN_VERSION-full.jar

COPY $YELLOWFIN_JAR .
COPY install.properties .
COPY run.sh .

ENTRYPOINT ./run.sh
