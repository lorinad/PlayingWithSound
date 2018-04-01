import java.util.Iterator;
import java.util.NoSuchElementException;

public class MusicLinkedList implements MusicList {
  private float sampleRate;
  private int numChannels;
  private int numSamples;
  private SoundLink head;

  // Class of single object in linked list.
  private class SoundLink {
    private float sample;
    private SoundLink nextSample;
    private SoundLink nextChannel;

    SoundLink(float sample) {
      this.sample = sample;
    }
  }

  public MusicLinkedList(float sampleRate, int numChannels) {
    this.sampleRate = sampleRate;
    this.numChannels = numChannels;
  }

  @Override
  public int getNumChannels() {
    return numChannels;
  }

  @Override
  public float getSampleRate() {
    return sampleRate;
  }

  @Override
  public int getNumSamples() {
    return numSamples;
  }

  @Override
  public float getDuration() {
    return numSamples / sampleRate;
  }

  @Override
  public void addEcho(float delay, float percent) {
    // Not implemented
  }

  @Override
  public void reverse() {
    if (head == null) {
      return;
    }
    SoundLink prev = head;
    SoundLink current = head.nextSample;
    while (current != null) {
      if (prev == head) {
        // New last sample
        SoundLink prevInChannel = prev;
        while (prevInChannel != null) {
          prevInChannel.nextSample = null;
          prevInChannel = prevInChannel.nextChannel;
        }
      }
      SoundLink next = current.nextSample;
      SoundLink currentInChannel = current;
      SoundLink prevInChannel = prev;
      // Reverse link
      while (currentInChannel != null) {
        currentInChannel.nextSample = prevInChannel;
        currentInChannel = currentInChannel.nextChannel;
        prevInChannel = prevInChannel.nextChannel;
      }
      prev = current;
      if (next == null) {
        head = current;
      }
      current = next;
    }
  }

  @Override
  public void changeSpeed(float percentChange) {
     sampleRate = sampleRate * percentChange;
  }

  @Override
  public void changeSampleRate(float newRate) {
    // Not implemented
  }

  @Override
  public void addSample(float sample) {
    if (numChannels != 1) {
      throw new IllegalArgumentException("SoundLink is not single channel");
    }

    // Create new sample
    SoundLink newSoundLink = new SoundLink(sample);
    if (head == null) {
      head = newSoundLink;
    }
    else {
      SoundLink currentSample = head;
      while (currentSample.nextSample != null)  {
        currentSample = currentSample.nextSample;
      }
      currentSample.nextSample = newSoundLink;
    }
    numSamples++;
  }

  @Override
  public void addSample(float[] sample) {
    if (sample.length != numChannels) {
      throw new IllegalArgumentException("The length of the sample array is not the same as the "
          + "number of channels.");
    }

    // Create a linked list of samples from sample array and connect them.
    SoundLink newSample = new SoundLink(sample[0]);
    SoundLink prevChannel = newSample;
    for (int i = 1; i < numChannels; i++) {
      SoundLink newChannel = new SoundLink(sample[i]);
      prevChannel.nextChannel = newChannel;
      prevChannel = newChannel;
    }

    if (head == null) {
      head = newSample;
    } else {
      SoundLink lastSample = head;
      // Find the last sample from head
      while (lastSample.nextSample != null) {
        lastSample = lastSample.nextSample;
      }
      // Add new elements to the last sample and connect them
      do {
        lastSample.nextSample = newSample;
        lastSample = lastSample.nextChannel;
        newSample = newSample.nextChannel;
      } while (lastSample != null);
    }
    // Increase number of samples.
    numSamples++;
  }

  @Override
  public Iterator<float[]> iterator() {
    return new Iterator<float[]>() {
      SoundLink current = head;

      @Override
      public boolean hasNext() {
        if (current.nextSample == null) {
          return false;
        } else {
          return true;
        }
      }

      @Override
      public float[] next() {
        if (current == null) {
          throw new NoSuchElementException("No next elements.");
        }

        float[] arrayChannels = new float[numChannels];
        SoundLink currentChannel = current;
        for (int i = 0; i < numChannels; i++) {
          arrayChannels[i] = currentChannel.sample;
          currentChannel = currentChannel.nextChannel;
        }
        current = current.nextSample;
        return arrayChannels;
      }
    };
  }

  @Override
  public Iterator<Float> iterator(int channel) {
    // Find the channel
    SoundLink beg = head;
    for (int i = 0; i < channel; i++) {
      beg = beg.nextChannel;
    } 
    final SoundLink finalBeg = beg;
    return new Iterator<Float>() {
      private SoundLink current = finalBeg;
 
      @Override
      public boolean hasNext() {
        if (current.nextSample == null) {
          return false;
        } else {
          return true;
        }
      }

      @Override
      public Float next() {
        if (current == null) {
          throw new NoSuchElementException("No next elements.");
        }
        float sample = current.sample;
        current = current.nextSample;
        return sample;
      }
    };
  }

  @Override
  public void clip(float startTime, float duration) {
    if (head == null) {
      return;
    }
    // Remove before start time
    SoundLink currentSample = head;
    int currentNum = 0;
    while (currentSample != null) {
      float sampleStartTime = currentNum / sampleRate;
      if (sampleStartTime >= startTime) {
        head = currentSample;
        break;
      }
      currentSample = currentSample.nextSample;
      currentNum++;
    }

    // Remove after duration
    currentNum = 0;
    while (currentSample != null) {
      float sampleStartTime = (currentNum + 1) / sampleRate;
      if (sampleStartTime >= duration) {
        currentSample.nextSample = null;
        break;
      }
      currentSample = currentSample.nextSample;
      currentNum++;
    }
    numSamples = currentNum;
  }

  @Override
  public void spliceIn(float startSpliceTime, MusicList clipToSplice) {
    if (head == null) {
      return;
    }
    // If the sampleRate of the clipToSplice is not the same as this sampleList, it will be modified 
    // to match the current soundList.
    if (sampleRate != clipToSplice.getSampleRate()) {
      clipToSplice.changeSampleRate(sampleRate);
    }
    int oldNumSamples = numSamples;
    // Find start time
    SoundLink currentSample = head;
    int currentNum = 0;
    while (currentSample != null) {
      float sampleStartTime = currentNum / sampleRate;
      if (sampleStartTime >= startSpliceTime) {
        break;
      }
      currentSample = currentSample.nextSample;
      currentNum++;
    }
    SoundLink oldNextSample = currentSample.nextSample;
    // Add samples from clipToSplice
    currentSample.nextSample = null;
    Iterator<float[]> it = clipToSplice.iterator();
    while(it.hasNext()) {
      float[] otherChannels = it.next();
      addSample(otherChannels);
      currentSample = currentSample.nextSample;
    }

    // Add third part
    while(currentSample != null && oldNextSample != null) {
      currentSample.nextSample = oldNextSample;
      currentSample = currentSample.nextChannel;
      oldNextSample =  oldNextSample.nextChannel;
    }
    numSamples = oldNumSamples + clipToSplice.getNumSamples();
  }

  @Override
  public void makeMono(boolean allowClipping) {
    if (numChannels == 1 || head == null) {
      return;
    }
    SoundLink currentSample = head;
    boolean rescale = false;
    float max = 1;
    float min = -1;
    do {
      SoundLink currentChannel = currentSample.nextChannel;
      for (int i = 1; i < numChannels; i++) {
        currentSample.sample += currentChannel.sample;
        currentChannel = currentChannel.nextChannel;
      }
      if (allowClipping) {
        if (currentSample.sample > 1) {
          currentSample.sample = 1;
        } else if (currentSample.sample < -1) {
          currentSample.sample = -1;
        }
      } else {
        if (currentSample.sample > 1 || currentSample.sample < -1) {
          rescale = true;
        }
        if (currentSample.sample > max) {
          max = currentSample.sample;
        }
        if (currentSample.sample < min) {
          min = currentSample.sample;
        }
      }
      // Remove other channels
      currentSample.nextChannel = null;
      currentSample = currentSample.nextSample;
    } while (currentSample != null);

    if (!allowClipping && rescale) {
      // if any values are greater than 1.0 or less than -1.0, the entire sample is rescaled  
      // to fit in the range
      currentSample = head;
      while (currentSample != null) {
        if (currentSample.sample > 0) {
          currentSample.sample = currentSample.sample / max;
        } else if (currentSample.sample < 0) {
          currentSample.sample = currentSample.sample / -min;
        }
        currentSample = currentSample.nextSample;
      }
    }
    numChannels = 1;
  }

  @Override
  public void combine(MusicList clipToCombine, boolean allowClipping) {
    if (head == null) {
      return;
    }
    SoundLink currentSample = head;
    Iterator<float[]> it = clipToCombine.iterator();
    boolean rescale = false;
    float max = 1;
    float min = -1;
    while (currentSample != null && it.hasNext()) {
      SoundLink currentChannel = currentSample;
      float[] channels = it.next();
      for (int i = 0; i < channels.length; i++) {
        currentChannel.sample += channels[i];
        if (allowClipping) {
          if (currentChannel.sample > 1) {
            currentChannel.sample = 1;
          }
          if (currentChannel.sample < -1) {
            currentChannel.sample = -1;
          }
        } else {
          if (currentChannel.sample > 1 || currentChannel.sample < -1) {
            rescale = true;
          }
          if (currentChannel.sample > max) {
            max = currentChannel.sample;
          }
          if (currentChannel.sample < min) {
            min = currentChannel.sample;
          }
        }
        currentChannel = currentChannel.nextChannel;
      }
      currentSample = currentSample.nextSample;
    }
    if (!allowClipping && rescale) {
      currentSample = head;
      while (currentSample != null) {
        SoundLink currentChannel = currentSample;
        for (int i = 0; i < numChannels; i++) {
          if (currentChannel.sample > 0) {
            currentChannel.sample /= max;
          } else if (currentChannel.sample < 0) {
            currentChannel.sample /= -min;
          }
          currentChannel = currentChannel.nextChannel;
        }
        currentSample = currentSample.nextSample;
      }
    }
  }

  @Override
  public MusicList clone() {
    // Clone linked list.
    MusicLinkedList cloneMusicList = new MusicLinkedList(sampleRate, numChannels);
    cloneMusicList.numSamples = numSamples;
    // first sample in the first channel of list to copy.
    SoundLink currentSample = head;
    SoundLink cloneHead = null;
    SoundLink clonePrevSample = null;
    
    // Copies objects horizontally.
    while (currentSample != null) {
      SoundLink newSoundLink = new SoundLink(currentSample.sample);
      if (cloneHead == null) {
        cloneHead = newSoundLink;
      } else if (clonePrevSample != null) {
        clonePrevSample.nextSample = newSoundLink;
      }
      // first sample in the second channel.
      SoundLink currentChannel = currentSample.nextChannel;
      SoundLink clonePrevChannel = clonePrevSample != null ? clonePrevSample.nextChannel : null;
      SoundLink cloneCurrentChannel = newSoundLink;
      
      // Copies objects vertically and adds links from previous channels.
      while (currentChannel != null) {
        SoundLink newSoundLinkChannel = new SoundLink(currentChannel.sample);
        cloneCurrentChannel.nextChannel = newSoundLinkChannel;
        // If not the first sample.
        if (clonePrevChannel != null) {
          clonePrevChannel.nextSample = newSoundLink;
          clonePrevChannel = clonePrevChannel.nextChannel;
        }
        cloneCurrentChannel = cloneCurrentChannel.nextChannel;
        currentChannel = currentChannel.nextChannel;
      }
      currentSample = currentSample.nextSample;
      clonePrevSample = newSoundLink;
    }
    cloneMusicList.head = cloneHead;
    return cloneMusicList;
  }
}
