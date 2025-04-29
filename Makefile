# ---- VARIABLES ----
APP_NAME = oomlet
JAR_FILE = target/$(APP_NAME)-0.1.0.jar
DOCKER_IMAGE = $(APP_NAME):latest

# ---- TASKS ----

## Build the application with Maven
build:
	mvn clean package

## Run the application locally (requires prior build)
run: build
	java -jar $(JAR_FILE)

## Build a Docker image using a simple Dockerfile
docker-build: build
	docker build -t $(DOCKER_IMAGE) . --load

## Run the Docker container
docker-run:
	docker run -it -p 8080:8080 --rm $(DOCKER_IMAGE)

## Clean Maven artifacts
clean:
	mvn clean

## Build Docker image using spring-boot:build-image (optional)
docker-buildpack:
	mvn spring-boot:build-image -Dspring-boot.build-image.imageName=$(DOCKER_IMAGE)

## View help
help:
	@echo "Usage: make [target]"
	@echo ""
	@echo "Available targets:"
	@echo "  build            - Clean and build the project with Maven"
	@echo "  run              - Run the built JAR locally"
	@echo "  docker-build     - Build Docker image using Dockerfile"
	@echo "  docker-run       - Run Docker container locally"
	@echo "  docker-buildpack - Build Docker image using buildpacks (if compatible)"
	@echo "  clean            - Clean Maven target directory"