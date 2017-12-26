#!/bin/sh
keytool -v -export -file mycert.cer -keystore keystore.jks -storepass storepassword -alias keystore
