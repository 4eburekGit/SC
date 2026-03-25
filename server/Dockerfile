FROM python:3.13 AS python-build
WORKDIR /cfreader

RUN git clone https://github.com/Xewdy444/CF-Clearance-Scraper.git /cfreader

RUN apt-get update \
    && apt-get install -y \
    chromium  \
    && rm -rf /var/lib/apt/lists/* \
    && pip install --no-cache-dir -r requirements.txt && touch cookies.json

RUN python main.py -f cookies.json https://sergiodemo.com/security/challenge/legacy-challenge

FROM ubuntu:22.04 AS server-runtime

WORKDIR /app

RUN apt-get update && apt-get install -y \
    build-essential \
    openjdk-25-jdk \
    maven

COPY src /app/src
COPY pom.xml /app
COPY images /app/images
RUN mvn clean package -DskipTests

# ENV DB_HOST=localhost
# ENV DB_PORT=5432

COPY --from=python-build /cfreader/cookies.json ./
COPY server.sh /app
EXPOSE 8000
ENTRYPOINT ["./server.sh"]

