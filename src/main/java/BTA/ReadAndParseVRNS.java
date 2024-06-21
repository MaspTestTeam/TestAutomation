package BTA;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadAndParseVRNS {
    public static void main(String[] args) {
    }

    public static List<String> readAndParseVrn(String filepath){
        // Variable array to hold the VRNS
        List<String> VRNS = new ArrayList<>();


        //Buffer for reading the file
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            // Skip the first line
            String line = br.readLine();

            //Loop through the txt file line by line
            while ((line = br.readLine()) != null) {
                // Split the line on whitespace
                String[] values = line.split(" ");
                // Always take the 5th value as the VRN
                VRNS.add(values[4].trim());
            }
        } catch (IOException e) {
            System.out.println();
        }

        return VRNS;
    }
}

