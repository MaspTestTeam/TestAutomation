package BTA;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadAndParseVRNS {
    public static void main(String[] args) {
        //The filepath for unused VRNs
        String filePath = "accounts/unused_VRNS.txt";
        List<String> bpAndVrn = ReadAndParseVRNS.readAndParseVrn(filePath);
    }

    public static List<String> readAndParseVrn(String filepath){
        // Variable array to hold the bp+vrn
        List<String> BpAndVrn = new ArrayList<>();

        //Buffer for reading the file
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            // Skip the first line
            String line = br.readLine();

            //Loop through the txt file line by line
            while ((line = br.readLine()) != null) {
                // Split the line on whitespace
                String[] values = line.split(" ");
                // Always take the 5th value as the VRN
                BpAndVrn.add(values[0].trim() +","+ values[4].trim());
            }
        } catch (IOException e) {
            System.out.println();
        }
        return BpAndVrn;
    }
}

