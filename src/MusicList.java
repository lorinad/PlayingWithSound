import java.util.Iterator;

public interface MusicList {
  
  /**
   * The number of channels in the SoundList
   * @return The number f channels in the SoundList
   */
  public int getNumChannels();
  
  /**
   * Returns the sample rate, in samples per second
   * @return The sample rate, in samples per second
   */
  public float getSampleRate();
   
  /**
   * Returns the number of samples in the MusicList
   * @return The number of samples in the MusicList.
   */
  public int getNumSamples();
  
  /**
   * Returns The duration of the sound, in seconds.
   * @return  the duration of the sound, in seconds.
   */
  public float getDuration();
  
  /**
   * Add an echo effect to the SoundList.  
   * @param delay The time (in seconds) before the echo starts
   * @param percent The percent falloff of the echo (0.5 is 50 percent volume, 0.25 is
   * 25 percent volume, and so on.  All samples should be clipped to the range -1 .. 1
   */
  public void addEcho(float delay, float percent);
  
  /**
   * Reverse the SoundList.  
   */
  public void reverse();
  
  /**
   * Change the speed of the sound.   
   * @param percentChange  How much to change the speed.  1.0 is no change, 2.0 doubles the speed
   * (and the pitch), 0.5 cuts the speed in half (and lowers the pitch)
   */
  public void changeSpeed(float percentChange);
  
  /**
   * Change the sample rate of the SoundList.  This will increase (or decrease) the number of
   * samples in the list, based on the new rate.  The total time (and pitch) of the sound should
   * remain the same. (Though of course you will lose information
   * if the new sample rate is lower than the old sample rate)
   * @param newRate the new sampling rate
   */
  public void changeSampleRate(float newRate);
  
  /**
   * Add a single sample to the end of the SoundList. Throws an exception if the soundlist has more
   * than 1 channel 
   * @param sample The sample to add
   */
  public void addSample(float sample);
  
  /**
   * Adds a single sample for each channel to the end of the SoundList. Throws an exception if 
   * the size of the sample 
   * array is not the same as the number of channels in the sound list
   * @param sample Array of samples (one for each channel) to add to the end of the SoundList
   */
  public void addSample(float sample[]);
  
  /**
   * Return an iterator that traverses the entire sample, returning an array floats (one for each
   * channel)
   * @return iterator
   */
  public Iterator<float[]> iterator();
  
  /**
   * Return an iterator that traverses a single channel of the list
   * @param channel The channel to traverse
   * @return the iterator to traverse the list
   */
  public Iterator<Float> iterator(int channel);
  
  /**
   * Trim the Soundlist, by removing all samples before the startTime, and all samples past the 
   * duration.Note that if a SoundList represents an 8 second sound, and we call clip(4,2), 
   * the new SoundList will be a 2-second sound (from seconds 4-6 in the old SoundList)
   * @param startTime Time to start (in seconds)
   * @param duration Duration (in seconds)
   */
  public void clip(float startTime, float duration);
  
  /**
   * Splice a new SoundList into this soundList.  Both SoundLists will be modified.  
   * If the sampleRate of the clipToSplice is not the same as this sampleList, it will be modified 
   * to match the current soundList.
   * @param startSpliceTime Time to start the splice
   * @param clipToSplice The other SoundClip to splice in.  
   */
  public void spliceIn(float startSpliceTime, MusicList clipToSplice);
  
  /**
   * Combine all channels into a single channel, by adding together all channels into a single 
   * channel.
   * @param allowClipping If allowClipping is true, then values greater than 1.0 or less than -1.0
   * after the addition are clipped to fit in the range. If allowClipping is false, then if any 
   * values are greater than 1.0 or less than -1.0, the entire sample is rescaled  
   * to fit in the range.
   */
  public void makeMono(boolean allowClipping);

  /**
   * Combines this SoundList with a new soundlist, by adding the samples together. This SoundList
   * is modified. 
   * @param clipToCombine  The clip to combine with this clip
   * @param allowClipping  If allowClipping is true, then values greater than 1.0 or less than
   * -1.0 after the 
   * addition are clipped to fit in the range.  If allowClipping is false, then the entire sample 
   * is rescaled  
   */
  public void combine(MusicList clipToCombine, boolean allowClipping);
  
  /**
   * Returns a clone of this SoundList
   * @return The cloned SoundList
   */
  public MusicList clone();
}