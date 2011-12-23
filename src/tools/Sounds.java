package tools;

import Main.Main;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 24.08.11
 * Time: 11:44
 * To change this template use File | Settings | File Templates.
 */
public class Sounds {
    public static void bell() {
        playSound("sound-bell");
    }

    public static void warning() {
        playSound("sound-warning");
    }

    public static void error() {
        playSound("sound-error");
    }

    public static void toggleon() {
        playSound("sound-toggle-on");
    }

    public static void toggleoff() {
        playSound("sound-toggle-off");
    }

    public static void playSound(String prop) {
        if (Main.getProps().getProperty("sound").equalsIgnoreCase("on")) {
            try {
                URL url = new URL("file://"+Main.getProps().get("workdir")+System.getProperty("file.separator")+"sounds"+System.getProperty("file.separator")+Main.getProps().getProperty(prop));
                AudioClip clip = Applet.newAudioClip(url);
                clip.play();
            } catch (Exception e2) {
                Main.getLogger().error(e2);
            }
        }
    }
}
