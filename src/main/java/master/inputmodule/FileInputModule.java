package master.inputmodule;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by semaj on 17. 10. 20.
 */

public class FileInputModule implements Runnable {

    private boolean alive = true;

    private LineListener<String> listener;

    private long delay = 1000L;

    private File file;

    public FileInputModule(String filename, LineListener<String> listener) {
        file = new File(filename);
        if (!file.canRead()) {
            System.out.println("can't read file " + file.getPath());
        }
        this.listener = listener;
    }

    @Override
    public void run() {

        long filePointer = 0;
        RandomAccessFile reader = null;

        try {
            while (alive) {
                long fileLength = this.file.length();

                try {
                    if (fileLength == 0) {
                        System.out.println("E log : filelength " + fileLength);
                        // file 다시 읽기
                        alive = false;
                        continue;
                    }
                    if (fileLength < filePointer) {
                        System.out.println("log : filelength " + fileLength);
                        reader = new RandomAccessFile(this.file, "r");
                        filePointer = 0;
                        continue;
                    } else if (fileLength > filePointer) {

                        reader = new RandomAccessFile(this.file, "r");
                        reader.seek(filePointer);

                        String line = reader.readLine();
                        while (line != null) {
                            listener.handle(line);
                            line = reader.readLine();
                        }
                        filePointer = reader.getFilePointer();
                        System.out.println("log : file Pointer " + filePointer);

                        reader.close();
                    }
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ignored) {
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    alive = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}