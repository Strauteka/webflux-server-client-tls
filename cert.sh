#! /bin/bash
. ./cert.conf

function pause(){
 read -s -n 1 -p "Press any key to continue . . ."
 echo ""
}

EXT=${LOCAL_EXT}

if [[ ! -z "$1" ]]; then
  EXT=${1}
fi

echo "Creating ${OUT_FILE_DIR} dir"
mkdir "${OUT_FILE_DIR}"
cd "${OUT_FILE_DIR}" || exit
echo "Deleting files"
rm -rf ./*.jks
rm -rf ./*.crt

echo "Creating certificate files"
#Generating server self-signed certificate for localhost
keytool -keystore serverkeystore.jks -alias "${SERVER_ALIAS}" -genkey -keyalg RSA -validity 365 -dname "CN=${SERVER_ALIAS}, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown" -keypass "${SERVER_CERT_PASSWORD}" -storepass "${SERVER_CERT_PASSWORD}" -ext "${EXT}"
#Generating client self-signed certificate
keytool -keystore clientkeystore.jks -alias "${CLIENT_ALIAS}" -genkey -keyalg RSA -validity 365 -dname "CN=${CLIENT_ALIAS}, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown" -keypass  "${CLIENT_CERT_PASSWORD}"  -storepass  "${CLIENT_CERT_PASSWORD}"
#Export servers key public part
keytool -export -alias "${SERVER_ALIAS}" -keystore serverkeystore.jks -file "${SERVER_ALIAS}".crt -keypass "${SERVER_CERT_PASSWORD}" -storepass "${SERVER_CERT_PASSWORD}"
#Import exported servers .crt to client keystore
keytool -keystore clientkeystore.jks -import -alias "${SERVER_ALIAS}" -file "${SERVER_ALIAS}".crt -trustcacerts -storepass  "${CLIENT_CERT_PASSWORD}"  -noprompt
#Export clients key public part to .cr
keytool -export -alias "${CLIENT_ALIAS}" -keystore clientkeystore.jks -file testclient.crt -keypass "${CLIENT_CERT_PASSWORD}" -storepass "${CLIENT_CERT_PASSWORD}"
#Creating truststore from client .crt
keytool -import -alias "${CLIENT_ALIAS}" -file testclient.crt -keystore servertruststore.jks -storepass "${TRUSTSTORE_CERT_PASSWORD}" -noprompt

echo "Deleting .crt files from dir ${OUT_FULL_PATH}"
rm -rf ./*.crt
#cmd /k
pause

