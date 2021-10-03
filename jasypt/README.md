# Jasypt usage

## Decrypt
mvn -f jasypt/pom.xml exec:java -Pdecrypt -Dpassword='<master key>' \
-Dinput='<hash key>'

## Encrypt
mvn -f jasypt/pom.xml exec:java -Pencrypt -Dpassword='<master key>' \
-Dinput='<input password>'