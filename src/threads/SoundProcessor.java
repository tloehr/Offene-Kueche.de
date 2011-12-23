package threads;

import Main.Main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 25.08.11
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class SoundProcessor extends Thread {
    private String nextSound = "";
    private boolean pause = false;
    //private Clip currentClip, nextClip = null;


    public void bell() {
        nextSound = "sound-bell";
    }

    public void warning() {
        nextSound = "sound-warning";
    }

    public void error() {
        nextSound = "sound-error";
    }

    public void toggleon() {
        nextSound = "sound-toggle-on";
    }

    public void toggleoff() {
        nextSound = "sound-toggle-off";
    }

    public SoundProcessor() {
        super();
    }

    public void pause() {
        synchronized (this) {
            Main.debug("Sound Processor paused");
            this.pause = true;
            this.notify();
        }
    }

    public void unpause() {
        synchronized (this) {
            Main.debug("Sound Processor restarted");
            this.pause = false;
            this.notify();
        }
    }

    private Clip getClip(String name) {
        Clip myClip = null;
        try {
            File soundFile = new File(getFilename(name));
            AudioInputStream sound = AudioSystem.getAudioInputStream(soundFile);
            DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
            myClip = (Clip) AudioSystem.getLine(info);
            myClip.open(sound);
        } catch (Exception e) {
            myClip = null;
        }
        return myClip;
    }

    private String getFilename(String name) {
        return Main.getProps().get("workdir") + System.getProperty("file.separator") + "sounds" + System.getProperty("file.separator") + Main.getProps().getProperty(name);
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!nextSound.isEmpty()) {

                    Main.debug("Playing: " + nextSound);
                    if (Main.getProps().containsKey("sound-external-player")) {
                        runExternal(nextSound);
                    } else { // Java Sound API
                        getClip(nextSound).start();
                    }

                    nextSound = "";
                }
                Thread.sleep(200); // Millisekunden
                if (pause) {
                    synchronized (this) {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
                pause = true;
                Main.debug("Sound Processor interrupted");
            } catch (Exception mfe) {
                mfe.printStackTrace();
            }
        }
    }

    // http://www.devdaily.com/java/java-exec-processbuilder-process-1
    private void runExternal(String name) {

        File player = new File(Main.getProps().getProperty("sound-external-player"));

        if (player.exists()) {
            ProcessBuilder pb = new ProcessBuilder(Main.getProps().getProperty("sound-external-player"), getFilename(name));
            pb.directory(new File(Main.getProps().getProperty("workdir")));
            try {
                pb.start();
            } catch (IOException e) {
                Main.error(e);
            }
        } else {
            Main.error("Player " + player + " nicht gefunden.");
        }
    }
}
