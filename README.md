# Spring Boot Server - client tls
### TODO: create documentation!

https://erfin-feluzy.medium.com/tutorial-secure-your-api-with-x509-mutual-authentication-with-spring-boot-on-openshift4-416a00a47af8


keytool -keystore serverkeystore.jks -alias testserver -genkey -keyalg RSA -validity 30 -ext san=ip:127.0.0.1

keytool -keystore clientkeystore.jks -genkey -keyalg RSA -validity 30

keytool -export -alias testserver -keystore serverkeystore.jks -file testserver.crt

keytool -keystore clientkeystore.jks -import -alias testserver -file testserver.crt -trustcacerts

keytool -export -alias mykey -keystore clientkeystore.jks -file client.crt

servertruststore no need servers ca!

keytool -import -trustcacerts  -alias client -file client.crt -keystore servertruststore.jks