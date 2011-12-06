package bot2;

import java.io.IOException;
import java.io.InputStream;

/**
 * Handles system input stream reading.
 */
public abstract class AbstractSystemInputReader {
    /**
     * Reads system input stream line by line. All characters are converted to lower case and each
     * line is passed for processing to {@link #processLine(String)} method.
     * 
     * @throws java.io.IOException if an I/O error occurs
     */
    public void readInput(InputStream stream) throws IOException {
        StringBuilder line = new StringBuilder();
        int c;
        while ((c = stream.read()) >= 0) {
            if (c == '\r' || c == '\n') {
                processLine(line.toString().toLowerCase().trim());
                line.setLength(0);
            } else {
                line = line.append((char)c);
            }
        }
    }
    
    /**
     * Process a line read out by {@link #readSystemInput()} method in a way defined by subclass
     * implementation.
     * 
     * @param line single, trimmed line of system input
     */
    public abstract void processLine(String line);
}
