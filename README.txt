Welcome! This is a collection of Java Classes that implement the following
ciphers: Affine Transformation, Caesar Cipher, Exponentiation Cipher,
Hill Cipher, RSA Cipher*, and Vigenere Cipher. There is also a toy
frequency analyzer and decipher program in FrequencyAnalysis.java.

This project was inspired by my Intro Number Theory class. During the class
I made several small methods to test my calculations, and wanted to take 
the project further (although only one of these original functions is 
included in the project)

All methods are written personally unless noted.

For testing convenience, excerpts of Mary Shelley's Frankenstein (in the public
domain) are also included in varying lengths.

Please contact me with any questions after reading my implementation notes.

Implementation notes:

*RSA limitations: as said in file comments, a proper RSA cipher would need some
  BigInt equivalent. Such a conversion wouldn't add any fun to the project, so
  it was excluded. Hence, please only use relatively small primes.

Exclusion of block ciphering in exponentation cipher: Severe parsing issues.
  Difficult to distinguish if final string block is, for example, "aab" or "ab"
  or "b" due to zero rounding.
  Future work could resolve this issue with sentries/ extra char notifications

Exclusion of Cipher Interface: The use of an Interface was rejected because
  (1) the only shared methods are encipher, decipher, toString, and get/setMessage.
  (2) encipher has different parameters between the classes, which could be
    be removed without total loss of functionality, but would be disappointing.
  (3) HillCipher raises errors with en/de-cipher, whereas all other classes do not.

Lack of inheritance: Same general reasons as "Interface". Only Affine
  Transformation and Ceasar Cipher are similar enough to merit inheritance.

Print Warnings over Exceptions: I am of the personal preference to avoid
  exceptions when it would not be catastrophic. None of the "warnings"
  (generally bad en/de-cipher or bad scalars) are severe enough to merit
  anything more than the use of a dummy variable.

Why Mary Shelley's Frankenstein? The novel is in the public domain, and I'm
  fond of it. The file lengths are:
  frankenstein.txt (2 lines, 55 words, a short excerpt),
  frankensteinCh1.txt (23 lines including blanks, 1779 words, the first chapter
    of "Frankenstein"), and
  frankensteinLONG.txt (195 lines including blanks, 11,580 words, the first
    five chapters of "Frankenstein")

Total time: Almost exactly one month, over the span of May & June 2020.
  Unknow hours.
