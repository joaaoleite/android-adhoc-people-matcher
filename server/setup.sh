sudo add-apt-repository ppa:webupd8team/java -y
sudo apt-get update
sudo apt-get install oracle-java8-installer
sudo apt-get install oracle-java8-set-default
sudo apt-get install -y maven
sudo apt-get install -y git
git clone -b server https://jdtomazio@gitlab.com/joaaoleite/cmu-project.git
cd cmu-project/server
mvn compile exec:exec
