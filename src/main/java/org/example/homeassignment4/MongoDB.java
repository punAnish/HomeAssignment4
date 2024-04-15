package org.example.homeassignment4;

import javax.swing.*;
import java.awt.*;

import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

public class MongoDB extends JFrame {

    private JTextField idField, ageField, nameField, cityField;
    private JButton addButton, readButton, updateButton, deleteButton;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDB() {
        // Connect to MongoDB server
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        // Access database
        database = mongoClient.getDatabase("homeAssignment");
        // Access collection
        collection = database.getCollection("documents");

        setTitle("MongoDB CRUD Operations");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));

        JLabel idLabel = new JLabel("ID:");
        idField = new JTextField();
        panel.add(idLabel);
        panel.add(idField);

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        panel.add(nameLabel);
        panel.add(nameField);

        JLabel ageLabel = new JLabel("Age:");
        ageField = new JTextField();
        panel.add(ageLabel);
        panel.add(ageField);

        JLabel cityLabel = new JLabel("City:");
        cityField = new JTextField();
        panel.add(cityLabel);
        panel.add(cityField);

        addButton = new JButton("Add ");
        addButton.addActionListener(e -> addDocument());
        panel.add(addButton);

        readButton = new JButton("Read ");
        readButton.addActionListener(e -> readDocument());
        panel.add(readButton);

        updateButton = new JButton("Update ");
        updateButton.addActionListener(e -> updateDocument());
        panel.add(updateButton);

        deleteButton = new JButton("Delete ");
        deleteButton.addActionListener(e -> deleteDocument());
        panel.add(deleteButton);

        add(panel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addDocument() {
        int id = Integer.parseInt(idField.getText());
        int age = Integer.parseInt(ageField.getText());
        String name = nameField.getText();
        String city = cityField.getText();

        // convert numeric id to object id
        ObjectId objectId = new ObjectId();

        Document document = new Document()
                .append("_id", objectId)
                .append("name", name)
                .append("age", age)
                .append("city", city);
        collection.insertOne(document);
        JOptionPane.showMessageDialog(this, "Document added successfully!");
        clearFields();
    }

    private void readDocument() {
        StringBuilder documents = new StringBuilder("Found document:\n");
        FindIterable<Document> iterable = collection.find();
        for (Document document : iterable) {
            // Retrieve ObjectId and other fields
            ObjectId id = document.getObjectId("_id");
            String name = document.getString("name");
            int age = document.getInteger("age");
            String city = document.getString("city");

            // Append to the StringBuilder
            documents.append("ID: ").append(id)
                    .append(", Name: ").append(name)
                    .append(", Age: ").append(age)
                    .append(", City: ").append(city)
                    .append("\n");
        }
        JOptionPane.showMessageDialog(this, documents.toString());
    }

    private void updateDocument() {
        String idString = idField.getText();
        Document filter;
        try {
            ObjectId id = new ObjectId(idString);
            filter  = new Document("_id", id);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid ObjectID. Please enter a valid ObjectID.");
            return;
        }

        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText());
        String city = cityField.getText();

        Document update = new Document("$set", new Document("name", name).append("age", age).append("city", city));
        UpdateResult result = collection.updateOne(filter, update);
        if (result.getModifiedCount() > 0) {
            JOptionPane.showMessageDialog(this, "Document updated successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update document.");
        }
        clearFields();
    }

    private void deleteDocument() {
        String idString = idField.getText();
        Document filter;
        try {
            ObjectId id = new ObjectId(idString);
            filter = new Document("_id", id);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid ObjectID. Please enter a valid ObjectID.");
            return;
        }

        DeleteResult result = collection.deleteOne(filter);
        if (result.getDeletedCount() > 0) {
            JOptionPane.showMessageDialog(this, "Document deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete document.");
        }
        clearFields();
    }


    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        ageField.setText("");
        cityField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MongoDB::new);
    }
}




