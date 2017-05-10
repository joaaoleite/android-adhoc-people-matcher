sudo add-apt-repository ppa:webupd8team/java -y
sudo apt-get update
sudo apt-get install oracle-java8-installer
sudo apt-get install oracle-java8-set-default
sudo apt-get install -y maven
cd ~/server/src/main/java/pt/ulisboa/tecnico/cmu/server
sudo rm keystore.jks
keytool -genkey -alias server \
    -keyalg RSA -keysize 2048 -keystore keystore.jks \
    -dname "CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, S=Unknown, C=Unknown" \
    -storepass cmu123 -keypass cmu123
cd ~/server
screen -dm -S maven mvn compile exec:java
exit
