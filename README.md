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

#### Solution design

   Solution is designed with API first approach, with the principle of keeping API endpoints simple and meaningful for clear intent to users. 
   API are designed with asynchronous in nature with reactive and event driven programing needs as asynchronous API are scalable and non blocking.
   The application is built using below tech stack with focus on containerization:
   
   * Java 11
   * Maven
   * Docker
   * Quarkus
   * PostgreSQL

   Quarkus is used as strategic service framework and is an ideal candidate for building cloud native java applications. Quarkus is fast, light-weight and uses CDI at its core.
   PostgreSQL is used as data store for storing structural data needs and persistence. There is no ORM solution as direct jdbc approach is faster in this case. 
   in addition to that, mutiny is used as asynchronous library for writing non-blocking and reactive APIs
   

