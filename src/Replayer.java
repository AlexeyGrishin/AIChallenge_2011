import bot2.Bot;
import bot2.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Replayer {


    public static final File REPLAY_ARCHIVE = new File("archive");
    public static final File JS_FOLDER = new File("js");

    public static void main(String args[]) throws IOException {
        REPLAY_ARCHIVE.mkdirs();

        File replayObj = new File(args.length > 0 ? args[0] : "replays");
        List<File> files = new ArrayList<File>();
        if (replayObj.isFile()) {
            files.add(replayObj);
        }
        else if (replayObj.isDirectory()) {
            files.addAll(Arrays.asList(replayObj.listFiles()));
        }
        for (File replayFile: files) {
            System.out.println("Read: " + replayFile);
            try {
                FileInputStream fis = new FileInputStream(replayFile);
                new Bot().readInput(fis);
                fis.close();
                copyFile(replayFile, REPLAY_ARCHIVE, replayFile.getName());
                copyFile(Logger.getLog(), REPLAY_ARCHIVE, replayFile.getName() + ".log.txt");
                composeReplay(Logger.getField(), JS_FOLDER, replayFile.getName());
                //replayFile.delete();

            }
            catch (Throwable e) {
                e.printStackTrace();
            }

        }
    }

    private static void composeReplay(File field, File jsFolder, String name) throws IOException {
        BufferedReader rdr = new BufferedReader(new FileReader(new File(jsFolder, "review.html")));
        BufferedReader fld = new BufferedReader(new FileReader(field));
        BufferedWriter wrt = new BufferedWriter(new FileWriter(new File(jsFolder, name + ".review.html")));
        String str;
        while ((str = rdr.readLine()) != null) {
            if (str.contains("../logs/field.js")) {
                wrt.write("<script>");
                wrt.newLine();
                while ((str = fld.readLine())!= null) {
                    wrt.write(str);
                    wrt.newLine();
                }
                wrt.write("</script>");
                wrt.newLine();
            }
            else {
                wrt.write(str);
                wrt.newLine();
            }
        }
        rdr.close();
        fld.close();
        wrt.close();
    }

    private static void copyFile(File from, File to, String name) throws IOException {
        FileInputStream input = new FileInputStream(from);
        FileOutputStream output = new FileOutputStream(new File(to, name));
        byte[] buffer = new byte[100*1024];
        int len;
        while ((len = input.read(buffer)) != -1) {
            output.write(buffer, 0, len);
        }
        output.close();
        input.close();
    }
}
