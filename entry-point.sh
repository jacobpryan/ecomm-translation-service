#!/bin/bash
# set -x
set -e
set -u
set -o pipefail

echo "Certificates"
echo "App Path: ${APP_PATH}"

export KEY_STORE=${APP_PATH}/keystore
export KEY_STORE_PASSWORD=changeit
export TRUST_STORE=${APP_PATH}/truststore
export TRUST_STORE_PASSWORD=changeit
export DEFAULT_JRE_TRUSTSTORE_PASSWORD=changeit
export TEMP_DIR=${APP_PATH}/tmp


LASTMILE_DIR=/var/run/secrets/kubernetes.io/lastmile

# Read these certs as Kubernetes secrets from the Hub environment
mkdir -p ${TEMP_DIR}
[[ -e "${LASTMILE_DIR}/server.crt" ]] && cp ${LASTMILE_DIR}/server.crt ${TEMP_DIR}/lastmilecert.pem || echo "lastmile/server.crt secret is not defined"
[[ -e "${LASTMILE_DIR}/server.key" ]] && cp ${LASTMILE_DIR}/server.key ${TEMP_DIR}/lastmilekey.pem || echo "lastmile/server.key secret is not defined"
[[ -e "${LASTMILE_DIR}/ca.crt" ]] && cp ${LASTMILE_DIR}/ca.crt ${TEMP_DIR}/lastmileca.pem || echo "lastmile/ca.crt secret is not defined"

echo "Generate truststore"
keytool -noprompt -importcert -alias lastmile -file ${TEMP_DIR}/lastmileca.pem -keystore ${TRUST_STORE} -storepass ${TRUST_STORE_PASSWORD}
keytool -noprompt -importcert -alias nm-root-ca -file ${TEMP_DIR}/nm-root-ca2.pem -keystore ${TRUST_STORE} -storepass ${TRUST_STORE_PASSWORD}
keytool -noprompt -importcert -alias nm-test-root-ca -file ${TEMP_DIR}/nm-test-root-ca2.pem -keystore ${TRUST_STORE} -storepass ${TRUST_STORE_PASSWORD}

# Setup RDS/AURORA JKS
# Break the bundle into individual pem files
# For some reason specifying a name for the prefix does not work while in Docker. Works fine outside.
# For now, the pem files will just be appended with an incrementer.
awk '/-----BEGIN CERTIFICATE-----/{x=++i;}{print > x;}' ${TEMP_DIR}/rds-combined-ca-bundle.pem
find . -regex ".*/[0-9]\{1,2\}" -print0 | \
xargs -0 -I {} keytool -noprompt -import -alias awsCACert{} -file {} -keystore ${TRUST_STORE} -storepass ${TRUST_STORE_PASSWORD}
# Default JRE Certs - For verifying certificates from AWS Resources or other Third Parties
# keytool -noprompt -importkeystore -srckeystore ${TEMP_DIR}/cacerts -srcstorepass ${DEFAULT_JRE_TRUSTSTORE_PASSWORD} -destkeystore ${TRUST_STORE} -deststorepass ${TRUST_STORE_PASSWORD}

echo "Generate keystore"
openssl pkcs12 -export -in ${TEMP_DIR}/lastmilecert.pem -inkey ${TEMP_DIR}/lastmilekey.pem -certfile ${TEMP_DIR}/lastmilecert.pem -name "lastmile" -out ${TEMP_DIR}/client.p12 -passout pass:${KEY_STORE_PASSWORD}
keytool -noprompt -importkeystore -srckeystore ${TEMP_DIR}/client.p12 -srcstorepass ${KEY_STORE_PASSWORD} -srcstoretype pkcs12 -destkeystore ${KEY_STORE} -storepass ${KEY_STORE_PASSWORD}

# Remove the temporary files
rm -rf ${TEMP_DIR}

#NEWRELIC="-javaagent:${APP_PATH}/newrelic.jar -Dnewrelic.environment=${SPRING_PROFILE}"
NEWRELIC=""
SSL_ARGS="-Djavax.net.ssl.keyStore=${KEY_STORE} -Djavax.net.ssl.keyStorePassword=${KEY_STORE_PASSWORD} -Djavax.net.ssl.trustStore=${TRUST_STORE} -Djavax.net.ssl.trustStorePassword=${TRUST_STORE_PASSWORD}"

exec java ${NEWRELIC} -Dspring.profiles.active=${SPRING_PROFILE} ${SSL_ARGS} -Djava.security.egd=file:/dev/./urandom -jar ${APP_PATH}/app.jar
