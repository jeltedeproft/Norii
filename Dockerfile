FROM adoptopenjdk:11-jdk-hotspot AS builder

COPY . /app
WORKDIR /app

RUN chmod u+x ./gradlew
RUN ./gradlew headless:installDist --no-daemon

FROM adoptopenjdk:11-jre-hotspot

EXPOSE 80

RUN mkdir /app

COPY --from=builder /app/headless/build/install/headless /app/
RUN chmod u+x /app/bin/headless

ENTRYPOINT [ "/app/bin/headless" ]
