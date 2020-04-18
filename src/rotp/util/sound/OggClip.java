/*
 * Copyright 2015-2020 Ray Fowler
 * 
 * Licensed under the GNU General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://www.gnu.org/licenses/gpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rotp.util.sound;

import rotp.Rotp;
import rotp.util.Base;

import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.FloatControl.Type.MASTER_GAIN;

public class OggClip implements SoundClip, Base {
    static HashMap<String, OggClip> loadedClips = new HashMap<>();
    Clip clip;
    boolean loaded = false;
    String filename;
    float volume;
    int position = 0;
    boolean continuous = false;
    String style = "";

    public static OggClip play(String fn, float vol) {
        if (!loadedClips.containsKey(fn))
            loadedClips.put(fn, new OggClip(fn, vol));

        OggClip wc = loadedClips.get(fn);
        wc.play();
        return wc;
    }
    public static OggClip playContinuously(String fn, float vol, String s) {
         if (!loadedClips.containsKey(fn))
            loadedClips.put(fn, new OggClip(fn, vol));

        OggClip wc = loadedClips.get(fn);
        wc.style = s;
        wc.playContinuously();
        return wc;
    }
    public OggClip(String fn, float vol) {
        filename = fn;
        volume = vol;
        loaded = false;

        AudioInputStream ais = null;
        AudioInputStream decoded = null;
        try {
            if (!loaded) {
                BufferedInputStream is = new BufferedInputStream(WavClip.wavFileStream(fn));
                ais = AudioSystem.getAudioInputStream(is);
                final int ch = ais.getFormat().getChannels();
                final float rate = ais.getFormat().getSampleRate();
                AudioFormat format = new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
                decoded = getAudioInputStream(format, ais);

                clip = AudioSystem.getClip();
                clip.open(decoded);
                if (vol < 1 && clip.isControlSupported(MASTER_GAIN)) {
                    System.out.println("Setting Volume ");
                    log("setting volume for sound: "+fn+"  to "+(int)(vol*100));
                    FloatControl gain = (FloatControl) clip.getControl(MASTER_GAIN);
                    gain.setValue(20f * (float) Math.log10(vol));
                }
                loaded = true;
            }
        }
        catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            e.printStackTrace();
            System.err.println(e.toString());
            System.err.println(e.getStackTrace());
        }
        finally {
            if (ais != null)
                try { ais.close(); } catch (IOException e) {            e.printStackTrace();
                }
            if (decoded != null)
                try { decoded.close(); } catch (IOException e) {            e.printStackTrace();
                }
        }
    }
    public void play() {
        clip.setFramePosition(position);
        clip.start();
    }
    public void playContinuously() {
        continuous = true;
        if (style.equals("L"))
            clip.setFramePosition(0);
        else
            clip.setFramePosition(position);
        clip.start();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    @Override
    public void pausePlaying() {
        position = clip.getFramePosition();
        clip.stop();
    }
    @Override
    public void resumePlaying() {
        clip.setFramePosition(position);
        clip.start();
        if (continuous)
            clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    @Override
    public void endPlaying() {
        position = clip.getFramePosition();
        clip.stop();
    }
}
