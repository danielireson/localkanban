FROM clojure:openjdk-11-slim-buster

RUN apt-get update

RUN apt-get install curl gnupg -yq && \
    curl -sL https://deb.nodesource.com/setup_12.x | bash && \
    apt-get install -y nodejs

RUN apt-get clean

WORKDIR /app

COPY . .

RUN npm ci

RUN npm run build

RUN adduser --disabled-login web

USER web

EXPOSE 5200

CMD node node_modules/serve/bin/serve.js --single web --listen 5200
