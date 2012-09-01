LSWM - Linus Simple Watermark

Questp programma fa uso di un programma "framework" che ha il compito di fornire ai programmi utente una matrice che rappresenta i pixel dell'immagine. I programmi utente implementano l'interfaccia ImgIntfc02, ricevendo la matrice suddetta e restituendo una matrice nella stessa forma al programma framework.
Si noti che questo programma framework (chiamato ImgMod02a) è stato modificato, in particolare perché in origine non permetteva di salvare il file modificato su disco. Le modifiche sono segnalate dalla stringa "LSWM Mod".
Il pacchetto LSWM è formato dai programmi utente dowatermark e checkwatermark.
Il programma framework originale è disponibile all'indirizzo: http://www.developer.com/java/other/article.php/3403921
Mentre alcuni snippet usati per implementare LSWM sono tratti da: http://www.developer.com/java/ent/article.php/3530866

Uso programma:
Compilare file .java: lanciare watermark.bat
Scrivere watermark: java ImgMod02a dowatermark <immagineOriginale> <immaginaneFirmata>
Checkare watermark: java ImgMod02a checkwatermark <immagine>
Se non vengono specificati parametri il programma ImgMod02a assumerà i parametri di default "dowatermark wild.png modded.png"

All'interno del programma viene poi fatto scegliere il keypair da utilizzare per la firma.
E' possibile generare altri keypair con il programma signatureTest.java (ad ogni avvio ne viene generato uno nuovo, chiamato sempre keypair)

Attenzione: il programma è stato pensato e testato per funzionare con file PNG 24bit di dimensione ragionevole. Non è garantito il funzionamento con altri formati (in particolare tutti quelli lossy e non a 24bit).