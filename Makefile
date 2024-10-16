.PHONY: help compile test build update

help:
	@echo "Available targets:"
	@echo "########################################################################################"
	@echo "  - help\t\t\tThis help"
	@echo "  - compile\t\t\tCompiles code"
	@echo "  - tests\t\t\tRun tests"
	@echo "  - build\t\t\tBuilds the jar"
	@echo "  - install\t\t\tCompile, package, and install jar in local repository"
	@echo "  - update\t\t\tChecks for outdated libraries"
	@echo "########################################################################################"

compile:
	@mvn clean compile

tests:
	@mvn clean test

build:
	@mvn clean package -DskipTests

install:
	@mvn clean install -DskipTests

# Checks to see if there are newer versions of dependencies or plugins
update:
	@mvn versions:display-property-updates
	@mvn versions:display-plugin-updates
	-@mvn validate -P cve-check
	-@mvn validate -P licenses
