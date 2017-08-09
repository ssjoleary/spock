HOST_IP    = $$(ifconfig en0 | grep inet | grep -v inet6 | awk '{print $$2}')
APP_ENV    ?= dev

GIT_SHA    = $$(git rev-parse --short HEAD)

install:
	sed -e "s/{{HOST_IP}}/\"$(HOST_IP)\"/" config.sample.edn > dev/resources/config.edn;
	sed -e "s/{{HOST_IP}}/$(HOST_IP)/" nginx.conf > tmp/nginx/nginx.conf;

uberjar:
	lein do clean, test;
	lein with-profile prod uberjar;

build:
	@read -p "Enter Docker Repo:" REPO; \
	echo "$$REPO/spock-$(GIT_SHA)"; \
	docker build -t $$REPO/spock:$(GIT_SHA) .; \
	docker push $$REPO/spock:$(GIT_SHA);

tag:
	@read -p "Enter Docker Repo:" REPO; \
	read -p "Enter Version Tag:" TAG; \
	echo "$$REPO/spock-$$TAG"; \
	echo "$$REPO/spock-$(GIT_SHA)"; \
	docker tag $$REPO/spock:$(GIT_SHA) $$REPO/spock:$$TAG; \
	docker push $$REPO/spock:$$TAG;

deploy:
	@read -p "Enter Docker Repo:" REPO; \
	read -p "Enter Version Tag / git SHA:" TAG; \
	ssh spock-lightsail "\
            docker pull $$REPO/spock:$$TAG && \
            docker kill spock_server && \
            docker run -d --name=spock_server  \
	      --rm --net=bridge \
              -p 3000:3000 \
              $$REPO/spock:$$TAG";
