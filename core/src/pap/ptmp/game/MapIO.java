package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.*;



public class MapIO {
    int id = -1;
    int seed = -1;
    float high_score = -1;
    float gravity = 0;
    String filename = "";
    String map_name = "";

    public MapIO(String set_filename) {
         filename = set_filename;
    }

    public void loadMapData(int target_id) {
        try {
            FileHandle fileHandle = Gdx.files.internal(filename);
            BufferedReader bufferedReader = new BufferedReader(fileHandle.reader());

            // Reading file content (this part is just an example and may not work if the file doesn't exist)
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(",");
                int file_id = Integer.parseInt(values[0]);
                if(file_id == target_id) {
                    id = file_id;
                    seed = Integer.parseInt(values[1]);
                    high_score = Float.parseFloat((values[2]));
                    gravity = Float.parseFloat(values[3]);
                    map_name = values[4];
                }
            }
            if (id == -1) {
                System.err.println("Invalid map id provided");
            }

            // Close the resources
            bufferedReader.close();
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
            FileHandle fileHandle = Gdx.files.internal(filename);
            BufferedReader bufferedReader = new BufferedReader(fileHandle.reader());
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

            //Save to file
            FileHandle outputFileHandle = Gdx.files.local(filename);
            outputFileHandle.writeString(fileContent.toString(), false);
            bufferedReader.close();

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO exception: " + e.getMessage());
        }
        loadMapData((id));
    }
}
