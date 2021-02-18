// TODO: avoid mentioning ArangoDB verisons
// TODO: remove all images, leave code snippets only
// TODO: remove mentions to Eclipse
// TODO: add gradle configuration (as alternative to maven)

// TODO: update imports as the folowing ones
import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.mapping.ArangoJack;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collections;
import java.util.Map;

public class FirstProject {
    public static void main(String[] args) {
        ArangoDB arangoDB = new ArangoDB.Builder()
                // TODO: add
                .serializer(new ArangoJack())
                .build();
        String dbName = "mydb";
        try {
            arangoDB.createDatabase(dbName);
            System.out.println("Database created: " + dbName);
        } catch (ArangoDBException e) {
            System.err.println("Failed to create database: " + dbName + "; " + e.getMessage());
        }

        String collectionName = "firstCollection";
        try {
            CollectionEntity myArangoCollection = arangoDB.db(dbName).createCollection(collectionName);
            System.out.println("Collection created: " + myArangoCollection.getName());
        } catch (ArangoDBException e) {
            System.err.println("Failed to create collection: " + collectionName + "; " + e.getMessage());
        }

        BaseDocument myObject = new BaseDocument();
        myObject.setKey("myKey");
        myObject.addAttribute("a", "Foo");
        myObject.addAttribute("b", 42);
        try {
            arangoDB.db(dbName).collection(collectionName).insertDocument(myObject);
            System.out.println("Document created");
        } catch (ArangoDBException e) {
            System.err.println("Failed to create document. " + e.getMessage());
        }

        try {
            BaseDocument myDocument = arangoDB.db(dbName).collection(collectionName).getDocument("myKey",
                    BaseDocument.class);
            System.out.println("Key: " + myDocument.getKey());
            System.out.println("Attribute a: " + myDocument.getAttribute("a"));
            System.out.println("Attribute b: " + myDocument.getAttribute("b"));
        } catch (ArangoDBException e) {
            System.err.println("Failed to get document: myKey; " + e.getMessage());
        }

        // FIXME: remove section "Read a document as VelocyPack"
        System.out.println("-----------");
        try {
            VPackSlice myDocument = arangoDB.db(dbName).collection(collectionName).getDocument("myKey",
                    VPackSlice.class);
            System.out.println("Key: " + myDocument.get("_key").getAsString());
            System.out.println("Attribute a: " + myDocument.get("a").getAsString());
            System.out.println("Attribute b: " + myDocument.get("b").getAsInt());
        } catch (ArangoDBException | VPackException e) {
            System.err.println("Failed to get document: myKey; " + e.getMessage());
        }

        // TODO: add section "Read a document as Jackson JsonNode"
        System.out.println("-----------");
        try {
            ObjectNode myDocument = arangoDB.db(dbName).collection(collectionName).getDocument("myKey",
                    ObjectNode.class);
            System.out.println("Key: " + myDocument.get("_key").textValue());
            System.out.println("Attribute a: " + myDocument.get("a").textValue());
            System.out.println("Attribute b: " + myDocument.get("b").intValue());
        } catch (ArangoDBException e) {
            System.err.println("Failed to get document: myKey; " + e.getMessage());
        }
        System.out.println("-----------");

        // ---


        myObject.addAttribute("c", "Bar");
        try {
            arangoDB.db(dbName).collection(collectionName).updateDocument("myKey", myObject);
        } catch (ArangoDBException e) {
            System.err.println("Failed to update document. " + e.getMessage());
        }

        try {
            BaseDocument myUpdatedDocument = arangoDB.db(dbName).collection(collectionName).getDocument("myKey",
                    BaseDocument.class);
            System.out.println("Key: " + myUpdatedDocument.getKey());
            System.out.println("Attribute a: " + myUpdatedDocument.getAttribute("a"));
            System.out.println("Attribute b: " + myUpdatedDocument.getAttribute("b"));
            System.out.println("Attribute c: " + myUpdatedDocument.getAttribute("c"));
        } catch (ArangoDBException e) {
            System.err.println("Failed to get document: myKey; " + e.getMessage());
        }


        try {
            arangoDB.db(dbName).collection(collectionName).deleteDocument("myKey");
        } catch (ArangoDBException e) {
            System.err.println("Failed to delete document. " + e.getMessage());
        }


        ArangoCollection collection = arangoDB.db(dbName).collection(collectionName);
        for (int i = 0; i < 10; i++) {
            BaseDocument value = new BaseDocument();
            value.setKey(String.valueOf(i));
            value.addAttribute("name", "Homer");
            collection.insertDocument(value);
        }


        try {
            String query = "FOR t IN firstCollection FILTER t.name == @name RETURN t";
            // FIXME: replace MapBuilder with Collections.singletonMap
            Map<String, Object> bindVars = Collections.singletonMap("name", "Homer");
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                System.out.println("Key: " + aDocument.getKey());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
        }


        try {
            String query = "FOR t IN firstCollection FILTER t.name == @name "
                    + "REMOVE t IN firstCollection LET removed = OLD RETURN removed";
            // FIXME: replace MapBuilder with Collections.singletonMap
            Map<String, Object> bindVars = Collections.singletonMap("name", "Homer");
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                System.out.println("Removed document " + aDocument.getKey());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
        }

        // TODO: add to tutorial
        arangoDB.shutdown();
    }
}
