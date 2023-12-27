package pap.ptmp.game;

import java.io.*;



public class MapIO {
    int id = -1;
    int seed = -1;
    float high_score = -1;
    String filename = "";

    public MapIO(String set_filename) {
         filename = set_filename;
    }

    public void loadMapData(int target_id) {
        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Reading file content (this part is just an example and may not work if the file doesn't exist)
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(",");
                int file_id = Integer.parseInt(values[0]);
                if(file_id == target_id) {
                    id = file_id;
                    seed = Integer.parseInt(values[1]);
                    high_score = Float.parseFloat((values[2]));
                }
            }
            if (id == -1) {
                System.err.println("Invalid map id provided");
            }

            // Close the resources
            bufferedReader.close();
            fileReader.close();
        } catch (FileNotFoundException e) {
            // Handle the exception when the file is not found
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            // Handle other IOExceptions if they occur during file reading
            System.err.println("IO exception: " + e.getMessage());
        }
    }

    public void updateHighScore(int targetId, float newHighScore) {
        try {
            // Read the existing content
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder fileContent = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(",");
                int fileId = Integer.parseInt(values[0]);

                // Update the high score for the target ID
                if (fileId == targetId) {
                    values[2] = Float.toString(newHighScore);
                }

                // Reconstruct the line
                String updatedLine = String.join(",", values);
                fileContent.append(updatedLine).append("\n");
            }

            // Close the resources
            bufferedReader.close();
            fileReader.close();

            // Write the updated content back to the file
            FileWriter fileWriter = new FileWriter(filename);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(fileContent.toString());

            // Close the resources
            bufferedWriter.close();
            fileWriter.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO exception: " + e.getMessage());
        }
        loadMapData((id));
    }
}
