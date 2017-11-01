package master.inputmodule;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import collog.Collog;

/**
 * Created by semaj on 17. 10. 20.
 */

public class FileInputModule implements Runnable{

    private boolean alive = true;

    private LineListener<String> listener;

    private long delay = 1000L;

    private File file;

    public FileInputModule(String filename, LineListener<String> listener) {
        file = new File(filename);
        this.listener = listener;
    }

    private String inputFile() {
//        System.out.print("Input file name : ");
//        Scanner scan = new Scanner(System.in);
//        String fileNameTemp = scan.nextLine();
        String fileNameTemp = Collog.getInstance().getFile_name();

        if(validFileName(fileNameTemp))
            return fileNameTemp;
        else
            return null;
    }

    private boolean validFileName(String fileName){
        return true;
    }

    @Override
    public void run() {

        long filePointer = 0;
        RandomAccessFile reader = null;

        try {
            while(alive) {
                long fileLength = this.file.length();

                try {
                    if(fileLength == 0) {
                        System.out.println("E log : filelength " + fileLength);
                        continue;
                    }
                    if (fileLength < filePointer) {
                        System.out.println("log : filelength " + fileLength);
                        reader = new RandomAccessFile(this.file, "r");
                        filePointer = 0;
                        continue;
                    } else if(fileLength > filePointer){

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
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
