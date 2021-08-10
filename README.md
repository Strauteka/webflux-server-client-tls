# Spring Boot Server - client tls
### Reactive Ping-pong with two-way ssl

Example created in Windows environment so possibly changes requires running in other platform.
_docker.sh winpty is for windows docker to run with -i flag_  

_To create CA signed certificate you can follow tutorials in __WWW___  
[Azure example 1](https://docs.microsoft.com/en-us/azure/iot-hub/tutorial-x509-openssl)   
[Azure example 2](https://docs.microsoft.com/en-us/azure/iot-hub/tutorial-x509-scripts)  

## Self-signed certificate with java keytool for .jks

<strong>Run _[run.sh](./run.sh)_</strong>

Or  
create certs folder  
`mkdir certs`  
`cd certs`  

_Generating server self-signed certificate for localhost_  
`keytool -keystore serverkeystore.jks -alias testserver -genkey -keyalg RSA -validity 365 -dname "CN=testserver, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown" -keypass ishallpassserver -storepass ishallpassserver -ext san=dns:localhost,ip:127.0.0.1`

_Generating client self-signed certificate_  
`keytool -keystore clientkeystore.jks -alias clientKey -genkey -keyalg RSA -validity 365 -dname "CN=testserver, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown" -keypass ishallpassclient -storepass ishallpassclient`

_Export servers key public part_  
`keytool -export -alias testserver -keystore serverkeystore.jks -file testserver.crt -keypass ishallpassserver -storepass ishallpassserver`

_Import exported servers .crt to client keystore_  
`keytool -keystore clientkeystore.jks -import -alias testserver -file testserver.crt -trustcacerts -keypass ishallpassserver -storepass ishallpassclient -noprompt`

### For two-way ssl
_Export clients key public part to .crt_

`keytool -export -alias clientKey -keystore clientkeystore.jks -file testclient.crt -keypass ishallpassclient -storepass ishallpassclient`

In some cases there is issue when server requires its own CA in it's truststore, not this case.

_Creating truststore from client .crt_  
`keytool -import -alias clientAlias -file testclient.crt -keystore servertruststore.jks -storepass ishallpasstruststore -noprompt`

## Other
_View client .jks_  
`keytool -list -v -keystore clientkeystore.jks -storepass ishallpassclient`

_Delete alias_  
`keytool -delete -alias testserver -keystore clientkeystore.jks -storepass ishallpassclient`

_Changes alias_  
`keytool -changealias -alias "testserver" -destalias "testserverChanged" -keystore serverkeystore.jks -keypass ishallpassserver -storepass ishallpassserver`

_You can allow use of https://127.0.0.1:8080/ping api from browser, if property set server.ssl.client-auth=want_

_Run [cert.sh](./cert.sh) for generating localhost demo certificates_  
_Run [docker.sh](./docker.sh) to create Docker image and run docker container_  
_Run [run.sh](./run.sh) to create certs, prepare docker files, run docker_

To change certs in container   
[docker run -v /host/path/to/certs:/container/path/to/certs -d IMAGE_ID "update-ca-certificates"](https://stackoverflow.com/questions/26028971/docker-container-ssl-certificates)


