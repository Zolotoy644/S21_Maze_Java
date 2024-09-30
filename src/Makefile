OS := $(shell uname -s)
PACKAGE = A1_Maze_Java
ifeq ($(OS), Linux)
    OPEN_TERMINAL := gnome-terminal -- bash -c
endif

ifeq ($(OS), Darwin)
    OPEN_TERMINAL := osascript -e 'tell app "Terminal" to do script'
endif

ifeq ($(OS), CYGWIN)
    OPEN_TERMINAL := cmd /C start
endif

ifeq ($(OS), MINGW)
    OPEN_TERMINAL := start
endif

all: back ui web
	
install: install-back install-web install-ui
	
back: install-back
	${OPEN_TERMINAL} "cd builds/back && java -jar Maze*.jar"

web: install-web 
	${OPEN_TERMINAL} "cd maze-web && npm start"

ui: install-ui
	${OPEN_TERMINAL} "cd MazeJavaFx && mvn clean javafx:run"

install-back:
	cd Maze && mvn clean install

install-web:
	cd maze-web && npm install
	cd maze-web && npm run build

install-ui:
	cd MazeJavaFx && mvn clean install

uninstall:
	rm -rf builds

clean:
	rm -rf builds
	rm A1_Maze_Java.tar.gz

dvi:
	cd Maze && mvn javadoc:javadoc
	open ./Maze/target/site/apidocs/index.html

dist:
	mkdir $(PACKAGE)
	cp -r ./MazeJavaFx $(PACKAGE)
	cp -r ./Maze $(PACKAGE)
	cp -r ./maze-web $(PACKAGE)
	cp Makefile $(PACKAGE)
	tar -czf $(PACKAGE).tar.gz $(PACKAGE)
	rm -rf $(PACKAGE)

test:
	cd Maze && mvn clean verify && 	open target/site/jacoco/index.html

