package com.app;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Base64;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar Main.jar <240341220146> <C:\\Users\\HP\\Desktop\\pritesh.json>");
            System.exit(1);
        }

        String prnNumber = args[0].toLowerCase();
        String jsonFilePath = args[1];

        String destinationValue = getDestinationValue(jsonFilePath);
        if (destinationValue == null) {
            System.out.println("Key 'destination' not found in the JSON file.");
            System.exit(1);
        }

        String randomString = generateRandomString(8);
        String concatenatedString = prnNumber + destinationValue + randomString;

        String md5Hash = generateMD5Hash(concatenatedString);

        System.out.println(md5Hash + ";" + randomString);
    }

    private static String getDestinationValue(String jsonFilePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(new File(jsonFilePath));
            return findDestinationValue(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private static String findDestinationValue(JsonNode node) {
        if (node.isObject()) {
            for (JsonNode child : node) {
                if (child.has("destination")) {
                    return child.get("destination").asText();
                }
                String result = findDestinationValue(child);
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                String result = findDestinationValue(item);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }
        return result.toString();
    }

    private static String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(digest).replaceAll("\\W", "").toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
