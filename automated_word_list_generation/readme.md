# Word Frequency Generator

## Example
Say we have a file, "example.txt," containing the following text: 

    This is too easy. Way too easy.

To get a list of the unique words for this file, run the following command:

    % ./getWordLists.py example.txt
    
This will save the following output to the file "word-frequency-example.json":

    easy

## What does the script do?  
The script takes a text file ("filename.txt") as input, and gets a list of every unique word in it. Then, the script removes all the stop words, and saves the list to a file.  

## General Requirements
* Python 2.7
* [NLTK](http://nltk.org/)

### Known issues
You need to already have the text files. This can easily be done with the [pdf2txt.py](https://github.com/euske/pdfminer/) utility. 

### How can I replicate the wordlists? 

Install the fantastic tool "[Drake](https://github.com/Factual/drake/)" and run "Drake" in the wordLists directory. 

### Sources: 

* Crypto - "Springer - A Classical Introduction to Cryptography - Applications for Communications Security"

* Networking - "Unix Network Programming - Stevens"

* Effective Java - 


* Patterns - "Patterns of Enterprise Application Architecture, Fowler, Rice, Foemmel"

* Design Patterns - 


* Pragmatic - 
