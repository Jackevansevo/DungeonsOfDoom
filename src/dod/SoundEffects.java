package dod;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Class to play obnoxious sound effects because I couldn't work out how to make a chat client
 * Note: code taken from http://stackoverflow.com/questions/2416935/how-to-play-wav-files-with-java
 * All sounds taken with permission from http://soundbible.com/
 */
public class SoundEffects {

    /**
     *
     * @param sound the sound file to be played
     * @param repeatNumber the number of times the track is to be repeated
     */
    public static void playSound(File sound, int repeatNumber) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(sound);
            AudioFormat format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.loop(repeatNumber);
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }
}


