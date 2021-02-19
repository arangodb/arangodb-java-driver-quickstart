# Tutorial: Java in 10 Minutes

This is a short tutorial with the [Java Driver](https://github.com/arangodb/arangodb-java-driver) and ArangoDB. In less
than 10 minutes you can learn how to use ArangoDB with Java.

## Install the Java driver

This tutorial will explain the usage of the java driver in Eclipse. First of all add the Java driver via maven to your
project:

```xml

<dependencies>
    <dependency>
        <groupId>com.arangodb</groupId>
        <artifactId>arangodb-java-driver</artifactId>
        <version>4.2.2</version>
    </dependency>
    ....
</dependencies>
```

## Connection

Let's configure and open a connection to start ArangoDB.

```java
ArangoDB arangoDB=new ArangoDB.Builder().build();
```

> **Hint:** The default connection is to http://127.0.0.1:8529.

## Creating a database

Let’s create a new database:

```java
String dbName = "mydb";
try {
    arangoDB.createDatabase(dbName);
    System.out.println("Database created: " + dbName);
} catch(ArangoDBException e) {
    System.err.println("Failed to create database: " + dbName + "; " + e.getMessage());
}
```

After executing this program the console output should be:

```text
Database created: mydb
```

## Creating a collection

Now let’s create our first collection:

```java
String collectionName = "firstCollection";
try {
    CollectionEntity myArangoCollection=arangoDB.db(dbName).createCollection(collectionName);
    System.out.println("Collection created: " + myArangoCollection.getName());
} catch(ArangoDBException e) {
    System.err.println("Failed to create collection: " + collectionName + "; " + e.getMessage());
}
```

After executing this program the console output should be:

```text
Collection created: firstCollection
```

## Creating a document

Now we create a document in the collection. Any object can be added as a document to the database and be retrieved from
the database as an object.

For this example we use the class BaseDocument, provided with the driver. The attributes of the document are stored in a
map as key<String>/value<Object> pair:

```java
BaseDocument myObject = new BaseDocument();
myObject.setKey("myKey");
myObject.addAttribute("a", "Foo");
myObject.addAttribute("b", 42);
try {
    arangoDB.db(dbName).collection(collectionName).insertDocument(myObject);
    System.out.println("Document created");
} catch(ArangoDBException e) {
    System.err.println("Failed to create document. " + e.getMessage());
}
```

After executing this program the console output should be:

```text
Document created
```

Some details you should know about the code:

- `setKey()` sets the key value of the new object
- `addAttribute()` puts a new key/value pair into the object
- each attribute is stored as a single key value pair in the document root

## Read a document

To read the created document:

```java
try {
        BaseDocument myDocument = arangoDB.db(dbName).collection(collectionName).getDocument("myKey", BaseDocument.class);
        System.out.println("Key: " + myDocument.getKey());
        System.out.println("Attribute a: " + myDocument.getAttribute("a"));
        System.out.println("Attribute b: " + myDocument.getAttribute("b"));
} catch(ArangoDBException e) {
        System.err.println("Failed to get document: myKey; " + e.getMessage());
}
```

After executing this program the console output should be:

```text
Key: myKey
Attribute a: Foo
Attribute b: 42
```

Some details you should know about the code:

- `getDocument()` returns the stored document data in the given JavaBean (BaseDocument)

