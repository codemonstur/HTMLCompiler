clean:
	@mvn -q clean

build:
	@mvn -q -e clean package #shade:shade@shade

check-versions:
	@mvn versions:display-dependency-updates
	@mvn versions:display-plugin-updates

deploy:
	@mvn clean deploy -P release
