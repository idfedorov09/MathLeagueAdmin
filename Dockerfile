FROM openjdk:17-oracle

ARG path='/usr/MathLeagueAdmin'
ARG workpath='./build/libs/'
#'./MATHLEAGUE'

RUN mkdir -p $path/data

WORKDIR $path

COPY $workpath/data .
COPY $workpath/MathLeagueAdmin-0.0.1-SNAPSHOT.jar .
COPY $workpath/libkey_checker.so .
COPY $workpath/keystore.p12 .

EXPOSE 8080

CMD ["java", "-jar", "MathLeagueAdmin-0.0.1-SNAPSHOT.jar"]
#docker build -t mathleague-docker .
#docker run --rm --name web -p 443:443 mathleague-docker