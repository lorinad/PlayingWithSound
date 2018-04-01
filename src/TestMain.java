import java.io.*;
import java.util.Iterator;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat.Encoding;

public class TestMain {
  //
  protected static final int SAMPLE_RATE = 16 * 1024;



  public static boolean approxEqual(float n1, float n2)
  {
    return Math.abs(n1 - n2) < 0.01;
  }



  public static boolean testMakeMono(int numChannels, boolean play)
  {
    boolean success = true;
    try
    {
      float frequency[] = new float[numChannels];
      float sampleRate = 10000;
      float time = 1;
      MusicLinkedList[] listOfLists = new MusicLinkedList[numChannels];
      boolean[]  channelsToUse = new boolean[numChannels];
      float[] angleFrequency = new float[numChannels];
      for (int i = 0; i < numChannels; i++)
      {

        channelsToUse[i] = true;
        frequency[i] = 100 + 50*i;
        angleFrequency[i] = (float) ( frequency[i] * Math.PI * 2);
        listOfLists[i] = SoundUtil.createSineWave(time, frequency[i], sampleRate, numChannels, channelsToUse);
        channelsToUse[i] = false;
      }
      for (int i = 1; i < numChannels; i++)
      {
        listOfLists[0].combine(listOfLists[i], true);
      }
      listOfLists[0].makeMono(true);

      Iterator<float[]> it1= listOfLists[0].iterator();
      double currentTime = 0;

      while (it1.hasNext())
      {
        float nextSample = it1.next()[0];

        float nextCorrectSample = 0;
        for (int i = 0; i < numChannels; i++)
        {
          nextCorrectSample += (float) Math.sin(currentTime *angleFrequency[i]);
        }
        nextCorrectSample = Math.max(-1, Math.min(1, nextCorrectSample));

        if (!approxEqual(nextSample, nextCorrectSample))
        {
          if (success)
          {
            System.out.println("Error:  Samples don't match");
          }
          success = false;
        }
        currentTime +=  1.0 / (double) sampleRate;
      }


      if (play)
      {
        SoundUtil.play(listOfLists[0]);
      }

    }
    catch (Exception e)
    {
      System.out.println(e.toString());
      return false;
    }

    return success;
  }

  public static boolean testMultiChannelSineWave(int numChannels, boolean play)
  {
    boolean success = true;
    try
    {
      float frequency[] = new float[numChannels];
      float sampleRate = 10000;
      float time = 1;
      MusicLinkedList[] listOfLists = new MusicLinkedList[numChannels];
      boolean[]  channelsToUse = new boolean[numChannels];
      float[] angleFrequency = new float[numChannels];
      for (int i = 0; i < numChannels; i++)
      {

        channelsToUse[i] = true;
        frequency[i] = 100 + 50*i;
        angleFrequency[i] = (float) ( frequency[i] * Math.PI * 2);
        listOfLists[i] = SoundUtil.createSineWave(time, frequency[i], sampleRate, numChannels, channelsToUse);
        channelsToUse[i] = false;
      }
      for (int i = 1; i < numChannels; i++)
      {
        listOfLists[0].combine(listOfLists[i], true);
      }

      Iterator<float[]> it1= listOfLists[0].iterator();
      double currentTime = 0;
      while (it1.hasNext())
      {
        float[] nextSample = it1.next();

        for (int i = 0; i < numChannels; i++)
        {
          float nextCorrectSample = (float) Math.sin(currentTime *angleFrequency[i]);
          if (!approxEqual(nextSample[i], nextCorrectSample))
          {
            if (success)
            {
              System.out.println("Error:  Samples don't match:  Multiple Iterator");
            }
            success = false;
          }
        }
        currentTime +=  1.0 / (double) sampleRate;

      }
      
      boolean singleSuceess = true;
      for (int chan = 0; chan < numChannels; chan++)
      {
         currentTime = 0;
        Iterator<Float> it = listOfLists[0].iterator(chan);
        while (it.hasNext())
        {
          Float nextSample = it.next();
        
            float nextCorrectSample = (float) Math.sin(currentTime *angleFrequency[chan]);
            if (!approxEqual(nextSample, nextCorrectSample))
            {
              if (singleSuceess)
              {
                System.out.println("Error:  Samples don't match:  Single iterator");
              }
              singleSuceess = false;
              success = false;
            }
            currentTime +=  1.0 / (double) sampleRate;
        }
  
      }     


      if (play)
      {
        SoundUtil.play(listOfLists[0]);
      }
    } 
    catch(Exception e)
    {
      System.out.println(e.toString());
      return false;
    }
    return success;
  }



  public static boolean testRescale(boolean play)
  {
    boolean success = true;
    try
    {
      float frequency = 100;
      float sampleRate = 10000;
      float time = 1;
      MusicLinkedList mList1 = SoundUtil.createSineWave(time, frequency, sampleRate);
      MusicLinkedList mList2 = SoundUtil.createSineWave(time, frequency, sampleRate);
      MusicLinkedList mList3 = SoundUtil.createSineWave(time, frequency, sampleRate);
      MusicLinkedList mList4 = SoundUtil.createSineWave(time, frequency * 2, sampleRate);

      mList1.combine(mList2, false);
      
      Iterator<Float> it1 = mList1.iterator(0);
      Iterator<Float> it2 = mList3.iterator(0);
      
      while (it1.hasNext() && it2.hasNext())
      {
        if (!approxEqual(it1.next(), it2.next()))
        {
          if (success)
          {
            System.out.println("Error:  Samples don't match");
          }
          success = false;
        }
      }
      if (it1.hasNext() || it2.hasNext())
      {
        System.out.println("Error:  iterator problem in combine");
      }
      if (play)
      {
        SoundUtil.play(mList1);
      }
      
      mList1.combine(mList4, false);
      
      boolean difference = false;
      it1 = mList1.iterator(0);
      it2 = mList3.iterator(0);
      
      while (it1.hasNext() && it2.hasNext())
      {
        if (!approxEqual(it1.next(), it2.next()))
        {
          difference = true;
        }
      }
      if (!difference)
      {
        System.out.println("Something odd in combine ...");
        success = false;
      }
      if (it1.hasNext() || it2.hasNext())
      {
        System.out.println("Error:  iterator problem in combine");
      }

      if (play)
      {
        SoundUtil.play(mList1);
      }
      
    }
    catch (Exception e)
    {
      System.out.println("Failed: " + e.toString());
      return false;
    }
    
    return success;
  }

  public static boolean testReverse(int numChannels)
  {
    boolean success = true;

    try
    {

      MusicLinkedList mL = new MusicLinkedList(10000, numChannels);
      float[] samples = new float[numChannels];
      for (int i = 0; i < 1000; i++)
      {
        for (int j = 0; j < numChannels; j++)
        {
          samples[j] = 1/(float)(i+j+1);
        }
        mL.addSample(samples);      
      }

      mL.reverse();
      mL.reverse();
      mL.reverse();  // reversing 3 times should be same as reversing once ...
      Iterator<float[]> it = mL.iterator();
      int i = 999;
      while (it.hasNext())
      {
        samples = it.next();
        for (int j = 0; j < numChannels; j++)
        {
          if (!approxEqual(samples[j],  1/(float) (i+j+1)))
          {
            if (success)
            {
              System.out.println("Error:  Samples don't match");
            }
            success = false;
          }
        }
        i--;
      }
      if (mL.getNumSamples() != 1000)
      {
    
          System.out.println("Error:  NumSamples don't match");
        
        success = false;

      }


    }
    catch(Exception e)
    {
      System.out.println("Failed: " + e.toString());
      return false;
    }

    return success;
  }

  

  public static boolean testSingleChanelSineWave(boolean play)
  {
    boolean success = true;
    float frequency = 100;
    float sampleRate = 10000;
    float angleFrequency =  (float) ( frequency * Math.PI * 2);
    float time = 1;

    try 
    {
      MusicLinkedList mList = SoundUtil.createSineWave(time, frequency, sampleRate);
      double currentTime = 0.0f;
      Iterator<float[]> it1= mList.iterator();
      while (it1.hasNext())
      {
        float nextSample = it1.next()[0];
        float nextCorrectSample = (float) Math.sin(currentTime *angleFrequency);
        currentTime +=  1.0 / (double) sampleRate;

        if (!approxEqual(nextSample, nextCorrectSample))
        {
          if (success)
          {
            System.out.println("Error:  Samples don't match");
          }
          success = false;
        }
      }
      if (!approxEqual((float) currentTime, time))
      {
        success = false;
        System.out.println("Error:  Times don't match");
      }
      if (!approxEqual(time, mList.getDuration()))
      {
        success = false;
        System.out.println("Error:  Duration don't match");
      }


      if (play)
      {
        SoundUtil.play(mList);
      }

    }
    catch (Exception e)
    {
      System.out.println("Failed: " + e.toString());
      return false;
    }
    return success;
  }


  public static boolean testResample(int numChannel, float oldSampleRate, float newSampleRate, boolean play)
  {
    boolean success = true;
    try
    {
      float frequency = 100;
      float angleFrequency =  (float) ( frequency * Math.PI * 2);
      float time = 1;

      boolean[] useChannel = new boolean[numChannel];
      for (int i = 0; i < numChannel; i++)
      {
        useChannel[i] = true;
      }
      MusicLinkedList ml = SoundUtil.createSineWave(time, frequency, oldSampleRate,numChannel, useChannel);
      if (play)
      {
        SoundUtil.play(ml);
      }
      ml.changeSampleRate(newSampleRate);
      double currentTime = 0;
      Iterator<float[]> it= ml.iterator();
  
      while (it.hasNext())
      {
        float[] nextSample = it.next();
        
        for (int i = 0; i < numChannel; i++)
        {
        
          float nextCorrectSample = (float) Math.sin(currentTime *angleFrequency);
          if (!approxEqual(nextSample[i], nextCorrectSample))
          {
            if (success)
            {
              System.out.println("Error:  Samples don't match");
            }
            success = false;
          }

          
        }
        currentTime +=  1.0 / (double) newSampleRate;
      }
      if (ml.getSampleRate() != newSampleRate)
      {
        success = false;
        System.out.println("Error:  Sampe rate not changed");       
      }
      if (!approxEqual((float) currentTime, time))
      {
        success = false;
        System.out.println("Error:  Times don't match");
      }
      if (play)
      {
        SoundUtil.play(ml);
      }
      
    }
    catch (Exception e)
    {
      System.out.println("Failed: " + e.toString());
      return false;
    }
    return success;
  }

  public static void printResult(boolean success)
  {
    if (success)
    {
      System.out.println("Success!");
    }
    else
    {
      System.out.println("Falure!");
    }
  }



  public static void main(String[] args) throws LineUnavailableException, UnsupportedAudioFileException, IOException {


    boolean play = false;

    System.out.println("Testing one channel simple sine wave");
    boolean result = testSingleChanelSineWave(play);
    printResult(result);

    System.out.println("Testing 2 channel simple sine wave");
    result = testMultiChannelSineWave(2, play);
    printResult(result);

    System.out.println("Testing 10 channel simple sine wave");
    result = testMultiChannelSineWave(2, false);  // Can't play 10 channel sounds anyway!
    printResult(result);

    System.out.println("Testing make mono (2 channel)");
    result = testMakeMono(2, play);
    printResult(result);

    System.out.println("Testing make mono (10 channel)");
    result = testMakeMono(10, play);
    printResult(result);

    System.out.println("Testing reverse (1 channel)");
    result = testReverse(1);
    printResult(result);

    System.out.println("Testing reverse (10 channels)");
    result = testReverse(10);
    printResult(result);
    
    System.out.println("Testing rescale");
    result = testRescale(play);
    printResult(result);

    

    System.out.println("Testing resampling (1  channel)");
    result = testResample(1, 20000, 5000, play);
    printResult(result);
    System.out.println("Testing resampling (2  channels)");
    result = testResample(2, 20000, 13000, play);
    printResult(result);
    System.out.println("Testing resampling (10 channels)");
    result = testResample(10, 20000, 5000, false);
    printResult(result);


    MusicLinkedList m = SoundUtil.readWAVFile("test2.wav");
    MusicLinkedList echo = (MusicLinkedList) m.clone();
    MusicLinkedList resample = (MusicLinkedList) m.clone();
    MusicLinkedList mono = (MusicLinkedList) m.clone();
    MusicLinkedList rev = (MusicLinkedList) m.clone();

    echo.addEcho(0.3f, 0.4f);
    mono.makeMono(true);
    rev.reverse();
    resample.changeSampleRate(resample.getSampleRate() / 3.0f);
    System.out.println("Playing normal version");
    SoundUtil.play(m);
    System.out.println("Playing resampled version (should sound the same)");
    SoundUtil.play(resample);
    System.out.println("Playing mono version");
    SoundUtil.play(mono);
    System.out.println("Playing echo version");
    SoundUtil.play(echo);
    System.out.println("Playing reversed version");
    SoundUtil.play(rev);
    
    MusicLinkedList one_four = SoundUtil.readWAVFile("one_four.wav");
    MusicLinkedList two_three = SoundUtil.readWAVFile("two_three.wav");
    one_four.spliceIn(1, two_three);
    System.out.println("Playing one two three four");   
    SoundUtil.play(one_four);
    one_four.clip(1.0f,1.0f);
    System.out.println("Playing two");    
    SoundUtil.play(one_four);
    
  }

}
