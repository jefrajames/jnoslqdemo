# First steps with JNoSQL

This project is derived from Eclipse JNoSQL [Hands-on lab at Oracle Code One 2018](https://github.com/JNOSQL/oc1-hands-on-2018/tree/master/document)

It aims at providing some clarifications for the [JNoSQL](http://www.jnosql.org/) newbies explaining 
how to use a template, a repository in a Java EE and Java SE contexts.

I've found myself in difficulties when running it and I've decided to share my modest experience throw this demo project.

For the sake of simplicity, it is based on a minimalistic example using MongoDB. 
It can easily be extended to other uses cases and databases.


## Why testing JNoSQL?

My first interest in testing JNoSQL comes from the fact that it will be
the [first new standardization project to be adopted by Jakarta EE](https://projects.eclipse.org/proposals/jakarta-ee-nosql). 
In particular, it will be the first project following the new [Eclipse Foundation Specification Process](https://www.eclipse.org/projects/efsp/) 
replacing the Java Community Process. 
Hence understanding JNoSQL is a modest contribution to the future of the Jakarta EE platform.

## A few words about JNoSQL

JNoSQL is an Eclipse project. According to its definition:
"It is a Java framework that streamlines the integration of Java applications with NoSQL databases. 
It defines a set of APIs to interact with NoSQL databases and provides a standard implementation for most NoSQL databases."

JNoSQL is based on two layers:

1. **Artemis**: a mapping layer (similar to JPA),
2. **Diana**: a communication layer which sits on top of database drivers.

The ambition of JNoSQL is to provide a consistent developer experience across the different NoSQL technologies while enabling to use some specificities. 

As illustrated in the above mentioned hands-on , JNoSQL offers two ways to interact with a database:

1. using a Template,
2. using a Repositoty

This small tutorial is mostly about the how and the why of using them.

##  A few words on the demo project

The demo project is based on a Person entity and an Address value object.

The project illustrates some basic operations in different contexts:

- **TestTemplateSE**: using a Template in Java SE, starting the CDI container programmatically,
- **TestTempalteEE**: using a Template in Java EE context with Arquillian and a remote Payara server,
- **TestRepositorySE**: using a Repository in Java SE, starting the CDI container programmatically,
- **TestRepsoitoryEE**: using a Repository in Java EE context with Arquillian and a remote Payara server

There is no beans.xml file, the discovery mode is implicitly set to annotated, hence all CDI classes must be annotated.

The following products have been used:

- Java SE 8,
- JNoSQL 0.0.7: as of this date (14 Jan. 2019) 0.0.8 (released in Dec; 2018) is not yet published on Maven central,
- MongoDB server 3.4.18,
- [Payara 5.184](https://www.payara.fish/software/downloads/all-downloads/) for Java EE testing,
- [Weld 3.0.5.Final](http://weld.cdi-spec.org/) for Java SE testing,
- [Lombok 1.84.4](https://projectlombok.org/) is used to avoid the boiler plate code: getter, setter, constructor...

To run the project "as is":

- a running MongoDB server is needed in all test cases described below,
- a running Payara 5 server is needed in Java EE test cases.

## Using a template

A Template is an injectable CDI object enabling to run:

- CRUD operations on a single or a list of entities: insert, update and delete,
- Queries in different flavors: select, count, singleResult, prepare, query, including skip and limit directives for pagination.

A template provides great flexibility with the database enabling all kinds of precise queries. 

## Using a repository

A Repository is a CDI injectable Java interface that extends JNoSQL Repository.
By default, a Repository implements:

- CRUD operations on a single or a list of entities: findById, save (both for insert and update), delete,
- Basic queries: count, existById (for a single or a list of entities), count.

Additional queries can be defined in the interface, returning Stream or List, based on attribute names of entities or value objects:

- findAll,
- findByPhones ...

The implementation is auto-magically generated at runtime by JNoSQL.
Is seems very similar to [DeltaSpike Data](https://deltaspike.apache.org/documentation/data.html).

 
## Template vs repository?

A Template provides great flexibility to query the database.
A Repository has the main benefit of being strongly typed, enabling a consistent developer experience, thanks to its Java interface definition. 
However it is less powerful in terms of query. For instance, it doesn't enable to paginate on result sets (with skip and limit directives on select queries).
At least, I've not been able to find it ...


## Specific feedbacks

Here are some specific feedbacks coming from this first experience:

- a CDI producer returning a DocumentCollectionManager is needed to inject a Template or a Repository (@ApplicationScoped),
- Diana mongodb-driver has a weird dependency on de.flapdoodle.embed which is a testing tool,
- the @Database annotation is required when using a Repository in Java EE. 
Warning: if you forget it or if you put a bad value, a weird CDI error happens: *WELD-000167: Class PersonRepository is annotated with @ApplicationScoped but it does not declare an appropriate constructor therefore is not registered as a bean!*
I was ready to open an issue when I realized that I put *@Database(DatabaseType.COLUMN)* in my code instead of *@Database(DatabaseType.DOCUMENT)*. 
Curiously, this error does not happen in Java SE and the @Database annotation is not required. Even more, it seems to be ignored.


## Conclusion

JNoSQL seems to be a promising solution and a strong basis for a new Jakarta EE specification. 
It would deserve a better documentation and more examples to facilitate the first experience.

I hope that this small example will enable you to discover JNoSQL and to make the proper decision when choosing between a Repository and a Template.

To make a long story short:

- both can be used in Java SE and Java EE with the same level of functionalities,
- in terms of CRUD operations, they are very similar,
- a Template provides more flexibility when querying the database,
- a Repository has the benefit of being strongly typed, but it is less limited than a template.

Hope this helps!

## References:

- [A guide to Eclipse JNoSQL (Oct. 2018)](https://www.baeldung.com/eclipse-jnosql)
- [DZone article About JNoSQL 0.0.4 (Jan. 2018)](https://dzone.com/articles/eclipse-jnosql-a-solution-to-java-in-nosql-databas)
- [DZone article About JNoSQL 0.0.6 (Jul. 2018)](https://dzone.com/articles/eclipse-jnosql-006-what-is-new-in-this-version?fromrel=true)
- [Eclipse JNoSQL 0.0.6 Release Note (June 20, 2018)](https://projects.eclipse.org/projects/technology.jnosql/releases/0.0.6)
- [Eclipse JNoSQL 0.0.7 Release Note (October 3, 2018)](https://projects.eclipse.org/projects/technology.jnosql/releases/0.0.7)
- [Eclipse JNoSQL 0.0.8 Release Note (December 5, 2018)](https://projects.eclipse.org/projects/technology.jnosql/releases/0.0.8)
- [JaxCenter JNoSQL and Jakarta EE (December 14, 2018)](https://jaxenter.com/jnosql-jakarta-ee-152815.html?utm_campaign=18days)
- [Eclipse Jakarta EE NoSQL](https://projects.eclipse.org/proposals/jakarta-ee-nosql)


