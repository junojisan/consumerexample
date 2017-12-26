#!/bin/sh
keytool -genkeypair -alias keystore -keyalg RSA -keysize 2048 -keystore keystore.jks -storetype JKS -validity 90 -keypass keypassword -storepass storepassword
