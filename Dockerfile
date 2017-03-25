FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/shoesshop-sample.jar /shoesshop-sample/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/shoesshop-sample/app.jar"]
