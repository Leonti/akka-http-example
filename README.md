# Scala Rest

- This project is based on the akka-http sample found here: https://github.com/akka/akka-http-quickstart-scala.g8. Refer to Lightbend's Akka-HTTP documentation to complete this exercise: https://developer.lightbend.com/guides/akka-http-quickstart-scala/http-server.html

- With the JSON files provided, load collections of users, tickets, and organizations into memory so that they are all available on request (both individually and as a list).
- Write basic CRUD for the collections (Users is already written but you are welcome to replace it with a different approach).
    - The user CRUD implementation as written has a simple flaw to do with IDs. Can you identify and fix it?
- Provide endpoints that filter the collections of users, tickets and organizations individually. For instance, add a route and parameters so that Users can be filtered by their name, returning all users with a given name. It's not necessary to provide partial matches (a search for Al need not return Alice).
- All of the resources include tags. Write a general tag endpoint that returns a response with a list of users, a list of organizations, and a list of tickets that have a given tag. Bonus points: allow search by a given set of tags, not just a single tag.
    - Try to maintain an internal separation of concerns even when writing the endpoint that returns a combined result.
- Add tests sufficient to demonstrate that your code works

# Running

How to run:  
```
sbt run
```
Available andpoints:   

Users:  
``http://localhost:8080/users``   
``http://localhost:8080/users?active=false&role=admin``  
 
Organizations:  
``http://localhost:8080/organizations``  
``http://localhost:8080/organizations?shared_tickets=true&domain_names=artiq,mazuda``  

Tickets:  
``http://localhost:8080/tickets``  
``http://localhost:8080/tickets?has_incidents=false&via=web&tags=Virginia`` 
 
All:  
``http://localhost:8080/all`` 

Tag search:  
``http://localhost:8080/all?tags=first,second`` 

Repo contains ``postman_collection.json``  for Postman to make testing easier.

Each endpoint can be filtered by any field or combination of fields. 
Partial matches are supported. Combination of fields is an implicit `AND`, 
for example ``http://localhost:8080/tickets?has_incidents=false&via=web`` will return
tickets which doesn't have incidends AND submitted via web.  


To run tests:  
```
sbt tests
```

Notes:  

- Implementation uses Akka typed actors which gives type safety and formalizes
communication protocol between actors  
- Api needs more design/refinement. For example get/delete would fail with 404 when entity doesn't exist.  
At the moment id has to be supplied by the user and POST is idempotent.  
- Scalafmt is used for code formatting
- Models use only strings, booleans and numbers for fields - 
some fields could be more strongly typed - for example ``via`` or ``role`` fields 
- Currently the only mandatory field is id and there is no validation.  
- Errors are thrown as is - it would be nice to have an error handler and return them as json  
- Testing could be more granular - for example UserRoutesSpec tests UserRegistry + UserOperations wrapper,
they could have their own tests and UserRouteSpec could use ad-hoc created implementation of `EntityOperations`
- There is some duplication in `registry` package - users/organizations/tickets are doing the same thing with different types.
This could be simplified by making types more generic