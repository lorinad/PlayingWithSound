# Playing with Sound

Manipulates sound by changing sound waves. 

## Sound
Sound waves are compressions of air.
![alt text](https://www.cs.usfca.edu/~galles/cs245/project1/soundWave1.jpg)
Sound waves are an inherently analog phenomena, to represent them in a digital domain, we need to sample them. We choose a sampling rate (say, 10000 samples a second) and then every 1/1000th of a second, we record the value of the wave at that point. We then save all of these samples, and that is our internal representaion of the sound.
![alt text](https://www.cs.usfca.edu/~galles/cs245/project1/soundWave2.jpg)
Sometimes we want to record multiple channels of sound. For instance, stereo headphones have 2 separate channels of sound (one for the left ear, and one for the right). Surround sound systems can have even more channels -- 5.1 has 6 channels: Front-Left, center, Front-Right, back-left, back-right, subwoofer.

## Built with

Implemented with Linked List data structure in JAVA

## Created for
ALgorithms and Data Structures course CS 245 
University Of San Francisco 

## License
This project is licensed under the MIT License
