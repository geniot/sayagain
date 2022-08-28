**Demo**
https://sayagain.app/swagger-ui/index.html

**Installation**

You only need MySQL with database 'sayagain', user 'sayagain' and password 'sayagain'.

*SayAgain.app is a domain I acquired for another project (hobby). I use it for this assessment because I have an
existing CI/CD pipeline that deploys to this domain.

1. Create a jar package with 'mvn clean package'.
2. Run it with 'java -jar sayagain.jar' (port 8002 should be free)
3. Open http://localhost:8002/sayagain/swagger-ui/index.html in your browser



**Architectural Choices**

1. OAS3 yaml file is loaded by Swagger. I chose not to use annotations scanning.
2. It's a three-tier architecture https://en.wikipedia.org/wiki/Multitier_architecture
3. There is no user management, authentication, validation is rudimentary (I'm out of time)
4. RecipeService has EntityManager and search implementation. Ideally this functionality should be part of the data
   tier.
5. DTOs are generated from the specification using Maven plugin.
6. Specification is synced manually in this project and https://github.com/geniot/sayagain-api-regression-test
7. There are no JUnit tests for several reasons:

- there is not much business logic,
- regression tests cover all use cases,
- I have already exceeded the time limit of 8 hours.

8. Exclusive search can probably be improved. Please let me know how do it, I'm always interested in learning new
   things.
9. Search criteria use AND to concatenate predicates.