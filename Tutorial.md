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

- `getDocument()` returns the stored document data in the given JavaBean (`BaseDocument`)


## Read a document as VelocyPack

You can also read a document as a VelocyPack:

```java
try {
    VPackSlice myDocument = arangoDB.db(dbName).collection(collectionName).getDocument("myKey", VPackSlice.class);
    System.out.println("Key: " + myDocument.get("_key").getAsString());
    System.out.println("Attribute a: " + myDocument.get("a").getAsString());
    System.out.println("Attribute b: " + myDocument.get("b").getAsInt());
} catch (ArangoDBException | VPackException e) {
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

- `getDocument()` returns the stored document data in the VelocyPack format (VPackSlice)


## Update a document

```java
myObject.addAttribute("c", "Bar");
try {
    arangoDB.db(dbName).collection(collectionName).updateDocument("myKey", myObject);
} catch (ArangoDBException e) {
    System.err.println("Failed to update document. " + e.getMessage());
}
```


## Read the document again

Let’s read the document again:

```java
try {
    BaseDocument myUpdatedDocument = arangoDB.db(dbName).collection(collectionName).getDocument("myKey", BaseDocument.class);
    System.out.println("Key: " + myUpdatedDocument.getKey());
    System.out.println("Attribute a: " + myUpdatedDocument.getAttribute("a"));
    System.out.println("Attribute b: " + myUpdatedDocument.getAttribute("b"));
    System.out.println("Attribute c: " + myUpdatedDocument.getAttribute("c"));
} catch (ArangoDBException e) {
    System.err.println("Failed to get document: myKey; " + e.getMessage());
}
```

After executing this program the console output should look like this:

```text
Key: myKey
Attribute a: Foo
Attribute b: 42
Attribute c: Bar
```


## Delete a document

Let’s delete a document:

```java
try {
    arangoDB.db(dbName).collection(collectionName).deleteDocument("myKey");
} catch (ArangoDBException e) {
    System.err.println("Failed to delete document. " + e.getMessage());
}
```


## Execute AQL queries

First we need to create some documents with the name Homer in collection firstCollection:

```java
ArangoCollection collection = arangoDB.db(dbName).collection(collectionName);
for (int i = 0; i < 10; i++) {
    BaseDocument value = new BaseDocument();
    value.setKey(String.valueOf(i));
    value.addAttribute("name", "Homer");
    collection.insertDocument(value);
}
```

Get all documents with the name Homer from collection firstCollection and iterate over the result:

```java
try {
    String query = "FOR t IN firstCollection FILTER t.name == @name RETURN t";
    Map<String, Object> bindVars = new MapBuilder().put("name", "Homer").get();
    ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
    cursor.forEachRemaining(aDocument -> {
        System.out.println("Key: " + aDocument.getKey());
    });
} catch (ArangoDBException e) {
    System.err.println("Failed to execute query. " + e.getMessage());
}
```

After executing this program the console output should look something like this:

```text
Key: 1
Key: 0
Key: 5
Key: 3
Key: 4
Key: 9
Key: 2
Key: 7
Key: 8
Key: 6
```

Some details you should know about the code:

- the AQL query uses the placeholder `@name` which has to be bind to a value
- `query()` executes the defined query and returns a `ArangoCursor` with the given class (here: `BaseDocument`)
- the order is not guaranteed


## Delete a document with AQL

Now we will delete the document created before:

```java
try {
    String query = "FOR t IN firstCollection FILTER t.name == @name "
        + "REMOVE t IN firstCollection LET removed = OLD RETURN removed";
    Map<String, Object> bindVars = new MapBuilder().put("name", "Homer").get();
    ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
    cursor.forEachRemaining(aDocument -> {
        System.out.println("Removed document " + aDocument.getKey());
    });
} catch (ArangoDBException e) {
    System.err.println("Failed to execute query. " + e.getMessage());
}
```

After executing this program the console output should look something like this:

```text
Removed document: 1
Removed document: 0
Removed document: 5
Removed document: 3
Removed document: 4
Removed document: 9
Removed document: 2
Removed document: 7
Removed document: 8
Removed document: 6
```

## Learn more

    Have a look at the [AQL documentation](https://docs.arangodb.com/latest/AQL/index.html) to learn more about our query language.
    Do you want to know more about Databases? [Click here!](https://docs.arangodb.com/latest/Manual/DataModeling/Databases/index.html)
    Read more about [Collections](https://docs.arangodb.com/latest/Manual/DataModeling/Collections/index.html).
    Explore [Documents](https://docs.arangodb.com/latest/Manual/DataModeling/Documents/index.html) in our documentation.
    For more examples you can explore the [ArangoDB cookbook](https://www.arangodb.com/docs/stable/).
