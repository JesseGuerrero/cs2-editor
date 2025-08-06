package com.example.myapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class App {
    public static void saveStringWithFileWriter(String content, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
            System.out.println("String saved to " + filename + " using FileWriter");
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            File compiledDir = new File("C:\\Users\\jesse\\IdeaProjects\\Darkan-Tools\\cs2-editor\\binary");
            File[] files = compiledDir.listFiles();
            if (files == null) {
                System.err.println("Error: Could not read directory binary");
                return;
            }
            for (File file : files) {
                try {
                    String fileName = file.getName();
                    CS2Script script = new CS2Script();
                    int id = Integer.parseInt(fileName.replace(".cs2", ""));
                    byte[] data = Files.readAllBytes(file.toPath());
                    String scriptText = script.decode(id, data);
                    saveStringWithFileWriter(scriptText, id + ".ts");
                    System.out.println("Processed script ID: " + id);

                } catch (Exception e) {
                    System.err.println("Error processing file " + file.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}