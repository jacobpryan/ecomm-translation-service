FROM docker.artifactory.nml.com/base-images/java11:latest-jdk-alpine3.12 as build
USER root:root

ENV APP_BUILD_PATH="/usr/src/mybuild"
ENV GRADLE_USER_HOME=".gradle/home"

COPY . $APP_BUILD_PATH
WORKDIR $APP_BUILD_PATH

# Run build (compile, unit test, copy newrlic jar to lib)
RUN mkdir -p run/tmp && \
    cp build/libs/*.jar run/ && \
    cp entry-point.sh run/

FROM docker.artifactory.nml.com/base-images/java11:latest-jdk-alpine3.12

ENV APP_PATH="/usr/src/myapp"
ENV APP_BUILD_PATH="/usr/src/mybuild"

WORKDIR $APP_PATH

RUN mkdir -p $APP_PATH/tmp \
    && cp -L /usr/lib/jvm/default-jvm/jre/lib/security/cacerts $APP_PATH/truststore

COPY --from=build $APP_BUILD_PATH/run/ $APP_PATH/

ADD https://s3.amazonaws.com/rds-downloads/rds-combined-ca-bundle.pem $APP_PATH/tmp/rds-combined-ca-bundle.pem
RUN cp $APP_PATH/tmp/rds-combined-ca-bundle.pem $APP_PATH/rds-combined-ca-bundle.pem

# Get the Home Office Internal CA's for connecting to the Home Office
ADD {NM_ROOT_URL} $APP_PATH/tmp/nm-root-ca2.pem
ADD {NM_TEST_ROOT_URL} $APP_PATH/tmp/nm-test-root-ca2.pem


RUN chmod -R ugo+rw $APP_PATH


USER 1000:1000
CMD ["/bin/sh", "entry-point.sh"]
