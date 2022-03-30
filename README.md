### BACKBASE MOVIE AWARDS SERVICE

#### Requirements
   The application should indicate whether a movie won a “Best Picture” Oscar, given a
   movie’s title based on this API and the enclosed CSV file that contains winners from
   1927 until 2010. It should also allow users to give a rating to movies and provide a list
   of 10 top-rated movies ordered by box office value.

   1. Your solution should include an API
   2. Your solution is able to persist and retrieve data from a real database 
   3. Your API needs to expect an API token from the caller

#### Assumptions

   The below assumptions have been made in absence of requirements gap:
   1. API requires to provide a list of top ten rated movies ordered by box office value. but enclosed csv data does not have box office value. so, required data is filled using random generator.
   2. API expects an API token from caller but there has been no mention of what kind of API token and provider to be used, plus it adds complexity to the solution, so basic authorization have been used for securing rest endpoints.
   3. There is no information on user like how we store accounts, so default user account "__guest__" is created for rating movies. This is done because then ratings data would not be reliable and keep overiding. API is also provided to register new user accounts.

#### Solution design

   Solution is designed with API first approach, with the principle of keeping API endpoints simple and meaningful for clear intent to users. 
   API are designed with asynchronous in nature with reactive and event driven programing needs as asynchronous API are scalable and non blocking.
   The application is built using below tech stack with focus on containerization:
   
   * Java 11
   * Maven
   * Docker
   * Quarkus
   * PostgreSQL
   * Liquibase

   Quarkus is used as strategic service framework and is an ideal candidate for building cloud native java applications. Quarkus is fast, light-weight and uses CDI at its core.
   PostgreSQL is used as data store for storing structural data and persistence. There is no ORM solution as direct jdbc approach is faster in this case. 
   Liquibase is used for tracking anf managing database schema changes. 
   In addition to that, mutiny is used as asynchronous library for writing non-blocking and reactive APIs.

#### How to run the application

   postgres is being run on docker host, and it is being recomnended to do, and application can be run locally for testing APIs

   1. Run the docker compose service yml given in docker folder using below:
      ````
      docker-compose -f postgres.yml up -d
      ````
   
   2. If running the application in the same docker host where postgres is running, then run the application with below environment variables:
      ````
      BB_MOVIE_DB_HOST=localhost
      BB_MOVIE_DB_PORT=5432
      ````
      if running the application in a different host other than postgres host, then kindly set the port forwarding and run the application with below environment variables:
      ````
      BB_MOVIE_DB_HOST=localhost
      BB_MOVIE_DB_PORT=<POSTGRES_FORWARDED_PORT>
      ````
   3. If docker host is not available, then have to install postgres binaries locally.

#### How to test the application
    
The application expects authorization in order to access API endpoints. User must have role of "__backbase__" in order to access API endpoints.
In order to test APIs, please provide below details:

    userid - backbase
    passowrd - backbase

* Has movie won oscar
    ````
    http://localhost:8080/v1/movie/hasWonOscar?title=<movie title>
    ````

* Post rating for movie
    ````
    POST http://localhost:8080/v1/movie/rating
    Content-Type: application/json
    Authorization: Basic backbase backbase

     {
      "movieTitle": "Million Dollar Baby",
      "userLogin": "guest",
      "rating": 8
     }
    ````
  
* Get top ten rated ordered by box office value
    ````
    http://localhost:8080/v1/movie/topTenRated
    ````

* Register new user
    ````
    POST http://localhost:8080/v1/movie/registerUser
    Content-Type: application/json
    Authorization: Basic backbase backbase

    {
    "login": "guest",
    "name": "John Doe"
    }
    ````

#### Features could have been implemented, if more time

1. Rating aggregation, right now there is no algorithm for rating aggregation.
2. Movies data storing with year wise release dates to allow duplicate names.
3. JWT based bearer token for authorization and securing APIs.

#### Scalability

