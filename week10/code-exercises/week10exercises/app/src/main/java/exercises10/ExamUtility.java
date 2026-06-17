package exercises10;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ExamUtility {

    /**
     * Dynamically resolves the path to a resource file.
     * @param resourceName The name of the resource file.
     * @return The absolute path to the resource file, or null if not found.
     */
    public static String getResourcePath(String resourceName) {
        try {
            Path path = Paths.get(
                    ExamUtility.class.getClassLoader().getResource(resourceName).toURI()
            );
            System.out.println("✅ Debug: File path resolved to -> " + path.toString());
            return path.toString();
        } catch (URISyntaxException | NullPointerException e) {
            System.out.println("❌ Error: Could not resolve the file path for " + resourceName);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads the file using a single sequential stream.
     * @param filename The file path.
     * @return A sequential stream of lines from the file.
     */
    public static Stream<String> getLinesAsStream(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            return reader.lines()
                    .onClose(() -> {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException exn) {
            System.out.println("❌ Error: Unable to read file " + filename);
            exn.printStackTrace();
            return Stream.empty();
        }
    }

    /**
     * Reads the file using a parallel stream.
     * @param filename The file path.
     * @return A parallel stream of lines from the file.
     */
    public static Stream<String> getLinesAsParallelStream(String filename) {
        return getLinesAsStream(filename).parallel();
    }
}
