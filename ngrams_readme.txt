These sample ngrams contain the top 1,000,000 2-grams (two word strings), 3-grams, 4-grams, and 5-grams from the iWeb corpus.

There are two formats, and when you purchase the n-grams you have access to both formats.

--------------------------------

Perhaps the easier of the two forms are the "words" format, e.g. ngrams_words_3.txt

These n-grams are fairly self-explanatory.

freq	word1	word2	word3

For example, line 8 of ngrams_words_3.txt is:

1934603	the	end	of	AT                  	NN1                 	IO                  

This means that the phrase "the end of" occurs 1934603 times in the corpus. "AT", "NN1" and "IO" are the PoS for these three words.
Note that the words are case sensitive. If a word has a different PoS in the lexicon, it will appear in two different n-grams.

--------------------------------

The wordID format (e.g. ngrams_wID_3.txt) is a bit more difficult to interpret, but it is also more powerful.

freq	wordID1	wordID2	wordID3

The wordID1-wordID3 numbers need to be matched up with the wordID fields in the lexicon that you 
can download from https://www.corpusdata.org/iweb_samples/iweb_lexicon.zip

For example, line 31 in ngrams_wID_3.txt is:

1934603	1	255	5

https://www.corpusdata.org/iweb_samples/iweb_lexicon.zip shows that:

1 = "the"
255 = "end"
5 = "of"

So this shows that the three word string "the end of" occurs 1934603 times in the corpus. Note that "the" and "The" would be two different numbers, 
as would "end" as a noun and "end" as a verb.

