# Crossover Java Trial Project - Weather - Results

## Code problems

* mixed code fragments in Endpoint controllers
* no persistance
* all classes in one package
* concurrency problems, linked with data and metrics structures

## Inital activity

At first I decided to write it all as a usual web application, running services and initialization logics from servlets and listeners
As database-communication layer I chose Hibernate ORM

All was fine, until I remember that I must write stand-alone application

To avoid rewriting previous code, I decided to use Spring-Boot for standalone application.

## First serious problems

I fail to startup Jersey on Spring-Boot
I fail to create EntityManagerFActory on Spring-Boot

So, taking in mind, that I was almost out of time, I asked for trial extend for 2 days

Minor solved problems - delivering EntityManagerFactory through Context was problematic under Spring-Boot. As known now, 
by default embed tomcat's context is turned off

## Next activity

I performed additional investigations about these to problems, discover than first could be solved by running application via maven,
decision of second problem remains undefined.

So, I decided to use remained time for rewriting this application to Camel.

## Results 

Application is ready, unit tests is ready, all tested, and I think, that all objectives are completed.

## Build & Run

mvn clean package
java -jar target/weather-camel.jar 
