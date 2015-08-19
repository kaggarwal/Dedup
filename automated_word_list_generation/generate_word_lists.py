#!/usr/bin/env python

import sys
import fileinput
import json
import string

from nltk        import word_tokenize
from collections import Counter


def getWordLists(fileName):
    # Given a sequence of words from stdin, return a dictionary
    # w/ words as keys and word frequencies as values
    with open(fileName, 'r') as f:
        text = f.read()
        # Remove all punctuation from textx
        text = text.translate(None, string.punctuation)
        words = word_tokenize(text)
        # Set comprehension ignores duplicates: 
        wordList = {word.lower() for word in words}
    return wordList


def removeStopWords(wordList, stopWordsFile = "../stop-words.txt"):
    with open(stopWordsFile, 'r') as f:
        stopWords = word_tokenize(f.read())
        for stopWord in stopWords:
            try:
                wordList.remove(stopWord)
            except KeyError:
                pass
        return wordList

if __name__ == "__main__":
    textFiles = sys.argv[1:]
    for textFile in textFiles: 
        print "Generating word frequency list..."
        wordList = getWordLists(textFile)
        print "Removing stop words..."
        wordList = removeStopWords(wordList)
        wordListFileName = 'word-list-' + textFile[:-4] + '.txt'
        with open(wordListFileName, 'wb') as wordListFile:
            wordListString = ""
            for word in wordList:
                wordListString += word + "\n"
            wordListFile.write(wordListString)
