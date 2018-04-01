import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFormat.Encoding;

public class SoundUtil
{

  /**
   * Play a MusicList using the default audio out
   * @param L The MusicList to play
   * @throws LineUnavailableException
   */
  public static void play(MusicList L) throws LineUnavailableException
  {
    Iterator<float[]> it = L.iterator();
    byte buff[] = new byte[150];
    final AudioFormat af = new AudioFormat(L.getSampleRate(), 16, L.getNumChannels(), true, true);
    SourceDataLine line = AudioSystem.getSourceDataLine(af);
    line.open(af, L.getNumSamples() * L.getNumChannels() * 2);
    line.start();

    while (it.hasNext())
    {
      int buffSize = 0;
      while (it.hasNext() && buffSize < 100)
      {
        float nextSample[] = it.next();
        for (int channel = 0; channel < nextSample.length; channel++)
        {
          short nextNum = (short) (nextSample[channel] * 0x7FFF);
          for (int i = 0; i < 2; i++)
          {
            buff[buffSize + 1 - i] = (byte) (nextNum & 0xFF);
            nextNum >>= 8;
          }
          buffSize += 2;  
        }
      }
      line.write(buff, 0, buffSize);

    }

    line.drain();
    line.close();
  }
  
  /**
   * Create a single channel MusicList representing a pure sine wave
   * @param time Duration of created sine wave in seconds
   * @param frequency Frequency of created sine wave in cycles / second
   * @param sampleRate Sample Rate of created sine wave, in samples / second
   * @return Created MusicList for that sine wave
   */
  public static MusicLinkedList createSineWave(float time, float frequency, float sampleRate)
  {
    boolean [] channelsToUse = new boolean[1];
    channelsToUse[0] = true;    
    return createSineWave(time, frequency, sampleRate, 1, channelsToUse);
    
  }
  /**
   * Create a multi-channel MusicList representing a pure sine wave.  Any subset of the channels may contain 
   * the sine wave, the channels that do not contain the sine wave will be empty (that is, have zero values
   * for all samples)
   * @param time Duration of created sine wave in seconds
   * @param frequency Frequency of created sine wave in cycles / second
   * @param sampleRate  Sample Rate of created sine wave, in samples / second
   * @param numChannels Number of channels in created MusicList
   * @param channelsToUse Which channels to use -- if channelsToUse[index] has the value true, then the 
   *                      channel for index will contain the sound wave.  if  channelsToUse[index] is false,
   *                      then the channel for index will be silent
   * @return Created MusicList for that sine wave
   */
  public static MusicLinkedList createSineWave(float time, float frequency, float sampleRate, int numChannels, boolean[] channelsToUse)
  {
    double totalTime = 0;
    MusicLinkedList returnVal = new MusicLinkedList(sampleRate, numChannels);
    float samples[] = new float[numChannels];
    double angleFrequency =  frequency * Math.PI * 2;
    while (totalTime < time)
    {
      float nextVal = (float) Math.sin(totalTime *angleFrequency);
      for (int i = 0; i < numChannels; i++)
      {
        if (channelsToUse[i])
        {
          samples[i] = nextVal;
        }
        else
        {
          samples[i] = 0;
        }
      }
      returnVal.addSample(samples);
      totalTime += 1.0 / (double) sampleRate;
    }
    
    return returnVal;
    
  }
  
  /**
   * Read a .wav file into a MusicLinkedList.  Note that not all .wav formats are supported
   * @param filename Filename of the .wav file
   * @return Created MusicLinkedList
   */
  public static MusicLinkedList readWAVFile(String filename)
  {
    MusicLinkedList outputMusicList = null;
    File soundFile = new File(filename);
    AudioInputStream audioInputStream;
    try {
      audioInputStream = AudioSystem.getAudioInputStream(soundFile);
      AudioFormat audioFormat = audioInputStream.getFormat();
      int frameSize = audioFormat.getFrameSize();

      boolean bigEndian = audioFormat.isBigEndian();
      boolean encodingUnsigned = audioFormat.getEncoding() == Encoding.PCM_UNSIGNED;
      int bitsize = audioFormat.getSampleSizeInBits(); 
      byte buff[] = new byte[frameSize];
      int numChannels = audioFormat.getChannels();
      outputMusicList = new MusicLinkedList(audioFormat.getSampleRate(),numChannels);

      if (encodingUnsigned && bitsize == 8)
      {
        while (audioInputStream.available() > 0)
        {
          int nextInput = audioInputStream.read();
          nextInput -= 128;
          outputMusicList.addSample(((float) nextInput) / 128f);
        }


      } else {

        while (audioInputStream.available() >=frameSize)
        {
          int bytesRead = audioInputStream.read(buff, 0, frameSize);
          if (bytesRead != frameSize)
            System.out.println("Not Enough Bytes!");
          float values[] = new float[numChannels];
          for (int currentChannel = 0; currentChannel < numChannels; currentChannel++) 
          {
            byte highByte = buff[currentChannel*2];
            byte lowByte = buff[currentChannel*2+1];

            if (!bigEndian)
            {
              byte tmp = lowByte;
              lowByte = highByte;
              highByte = tmp;
            }
            int highInt = highByte;
            int lowInt = ((int) lowByte) &0xFF;
            highInt <<= 24;
            highInt >>= 16;
            int result = lowInt + highInt;
            values[currentChannel] = ((float) result) / (float) 0x7FFF;
          }
          outputMusicList.addSample(values);
        }
      }
    } catch (UnsupportedAudioFileException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return outputMusicList;

  }
}
