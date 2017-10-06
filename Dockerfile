FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/intowow.jar /intowow/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/intowow/app.jar"]
