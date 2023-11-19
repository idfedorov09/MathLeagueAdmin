FROM ubuntu:latest


ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    apt-get install texlive-full -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*


ARG path='/usr/MathLeagueAdmin'
ARG workpath='./build/libs/'
#'./MATHLEAGUE'

RUN mkdir -p $path/data
WORKDIR $path

COPY $workpath/data ./data
COPY $workpath/MathLeagueAdmin-0.0.1-SNAPSHOT.jar .
COPY $workpath/libkey_checker.so .
COPY $workpath/keystore.p12 .


#for faster first load.
#Recommended also add pdflatex, lualatex etc for feautures.
RUN cd data/weekly-problem/template && \
    xelatex main.tex

EXPOSE 8080
#443

ENV TZ Europe/Moscow

CMD ["java", "-jar", "MathLeagueAdmin-0.0.1-SNAPSHOT.jar"]
#docker build -t mathleague .
#docker run --rm --name web -p 443:443 mathleague
#docker run --rm --name web -p 80:8080 -p 443:8080 -p 8080:8080 mathleague

#remove <none> rmi
#
#docker rmi $(docker images | grep '<none>' | awk '{print $3}')

#remove all stopped (succesfully) process
#
#docker rm $(docker ps -a | grep 'Exited (0)' | awk '{print $1}')


#Start REDIS server
#
#docker run -d -p 6379:6379 --name redis_container redis --requirepass "0SVO-Z002-4022-2Z0V"
#
#using docker-compose.yml:
#
#cd ./MATHLEAGUE/redis && docker-compose up -d