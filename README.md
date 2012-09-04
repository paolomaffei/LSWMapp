This program encodes a watermark in the least significant bits of a into a 24-bit PNG image.
The watermark is a generated RSA key therefore the image is signed with a combination of the most 7 significant bits of the whole image and the key itself.

Usage:

java ImgMod02a dowatermark <originalFileName> <signedFileName>
java ImgMod02a checkwatermark <fileToCheck>

Once started the program you'll be asked to select a key file. You can generate new keys with signatureTest.java

Note: Only works on 24-bit PNGs - one such image (flower.png) is provided.