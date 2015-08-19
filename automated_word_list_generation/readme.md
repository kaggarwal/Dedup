# Word Frequency Generator

The current directory contains the code necessary to generate the lists of
unique words as described in the methodology section of our paper. 

## Example
Say we have a file, "example.txt," containing the following text: 

    This is too easy. Way too easy.

To get a list of the unique words for this file, run the following command:

    % ./generate_word_lists example.txt
    
This will save the following output to the file "word-frequency-example.json":

    easy

## What does the script do?

The script takes a text file ("filename.txt") as input, and gets a list of every
unique word in it. Then, the script removes all the stop words, and saves the
list to a file.  

## General Requirements
* Python 2.7
* [NLTK](http://nltk.org/)

### Known issues

None.

### How can I replicate the word lists? 

Run `make all` in the `word_lists` directory.

### Sources: 

* Crypto - "Springer - A Classical Introduction to Cryptography - Applications for Communications Security"

* Networking - "Unix Network Programming - Stevens"

* Effective Java - ???

* Patterns - "Patterns of Enterprise Application Architecture, Fowler, Rice, Foemmel"

* Design Patterns - ???

* Pragmatic - ???

* Stop words - ??? 
