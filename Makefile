.PHONY: help clean compile test build validate release-notes

NAME=`xmllint --xpath "//project/artifactId/text()" pom.xml`
VERSION=`xmllint --xpath "//project/version/text()" pom.xml`
PREVIOUS_TAG=`git tag | sort -r | head -n 1`

# Blue [INFO] prefix before every message, NO_COLOR=1 prints it plain
ifneq ($(NO_COLOR),)
INFO = printf '[INFO] %s\n'
else
INFO = printf '[\033[1;34mINFO\033[m] %s\n'
endif

help:
	@echo "Available targets for $(NAME):"
	@echo "########################################################################################"
	@echo "  - help\t\t\tThis help"
	@echo "  - clean\t\t\tDelete everything in ./target"
	@echo "  - compile\t\t\tCompiles code"
	@echo "  - tests\t\t\tRun tests"
	@echo "  - build\t\t\tBuilds the jar"
	@echo "  - test\t\t\tRuns the tests"
	@echo "  - install\t\t\tCompile, package, and install jar in local repository"
	@echo "  - validate\t\t\tCheck dependency versions, cves, and licenses"
	@echo "  - release-notes\t\tCreate release notes for the latest version"
	@echo "########################################################################################"

clean:
	@$(INFO) "Cleaning"
	@mvn clean

compile:
	@mvn clean compile

tests:
	@mvn clean test

build:
	@mvn -DskipTests clean package

test:
	@mvn test

install:
	@mvn clean install -DskipTests

# Checks to see if there are newer versions of dependencies or plugins
validate:
	@mvn versions:display-dependency-updates
	@mvn versions:display-plugin-updates
	@mvn validate -P vulnerabilities
	@mvn validate -P licenses

release-notes:
	@$(INFO) "Writing release notes to src/docs/releases/release-$(VERSION).txt"
	@echo "$(VERSION)" >> src/docs/releases/release-$(VERSION).txt
	@echo "" >> src/docs/releases/release-$(VERSION).txt
	@git log --pretty="%s" $(PREVIOUS_TAG)... master >> src/docs/releases/release-$(VERSION).txt
