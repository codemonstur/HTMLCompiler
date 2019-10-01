clean:
	@mvn -q clean

build:
	@mvn -q clean package shade:shade@shade

release:
	@$(eval STATUS := $(shell git status --porcelain))
	@test "x$(STATUS)" = x
	@mvn -q -DpushChanges=false clean package release:prepare release:clean

install:
	@echo "[HtmlCompiler] Building"
	@mvn -DskipTests=true clean package shade:shade@shade
	@echo "[HtmlCompiler] Installing"
	@sudo cp target/htmlcompiler.jar /usr/local/bin
	@echo "[HtmlCompiler] Execute htmlcompiler using 'java -cp /usr/local/bin/htmlcompiler.jar htmlcompiler.Cmd'"
	@echo "[HtmlCompiler] If you haven't already you can create an alias; alias hc='java -cp /usr/local/bin/htmlcompiler.jar htmlcompiler.Cmd'"

deploy:
	@mvn clean deploy -P release
