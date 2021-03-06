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

RUN adduser --disabled-login --gecos '' node

USER node

ENV NODE_ENV production

ENV PORT 3000

EXPOSE 3000

CMD bin/www
