sudo add-apt-repository ppa:webupd8team/java -y
sudo apt-get update
sudo apt-get install oracle-java8-installer
sudo apt-get install oracle-java8-set-default
sudo apt-get install -y maven
cd cmu-project/server/src/main/java/pt/ulisboa/tecnico/cmu/server
keytool -genkey -alias server -keyalg RSA -keystore keystore.jks -keysize 2048
cd ~/cmu-project/server
mvn compile exec:exec
