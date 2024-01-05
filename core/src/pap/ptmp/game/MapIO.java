package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.*;
import java.util.ArrayList;


public class MapIO {

    public class MapData {
        int id = -1;
        int seed = -1;
        float high_score = -1;
        float gravity = 0;
        String name = "";

        public MapData(int id, int seed, float high_score, float gravity, String name)
        {
            this.id = id;
            this.seed = seed;
            this.high_score = high_score;
            this.gravity = gravity;
            this.name = name;
        }
    }

    String filename = "";
    ArrayList<MapData> loadedMaps;

    public MapIO(String set_filename) {
        filename = set_filename;
        loadedMaps = new ArrayList<MapData>();
        loadMapData();
    }

    public ArrayList<MapData> getMaps()
    {
        return loadedMaps;
    }

    private void loadMapData() {
        try {
            FileHandle fileHandle = Gdx.files.internal(filename);
            BufferedReader bufferedReader = new BufferedReader(fileHandle.reader());

            // Reading file content (this part is just an example and may not work if the file doesn't exist)
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(",");
                int id = Integer.parseInt(values[0]);
                int seed = Integer.parseInt(values[1]);
                float high_score = Float.parseFloat((values[2]));
                float gravity = Float.parseFloat(values[3]);
                String map_name = values[4];

                loadedMaps.add(new MapData(id, seed, high_score, gravity, map_name));
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
        //loadMapData((id));
    }
}
