Michael Saltzman
mjs2287

Data Structures: Programming 2


USAGE
--------------------
There are 5 buttons:

1) Open Directory - Choose a directory. If "virusdb.ser" exists in the 
directory, the previous save state will automatically be loaded. Otherwise,
a new database will be created at runtime.

2/3) Learn Benign Files/Viruses - Choose a directory containing the known
viruses/benign files in order to train the program.

4)Clear Database - Clears the current working database and chosen directory. 
No files will be deleted.

5) Scan File - Choose a file. The program will then scan the file and calculatethe ratio of virus/benign based on the PROBABILITY CALCULATION method below. 
Then, the program predicts whether the file is a virus or not based on the
ratio.


In order to use the program, you have to train it. Start by clicking "Learn Benign Files" and "Learn Viruses." These buttons will prompt you to choose a 
directory, in which the  known viruses/normal files are stored. Then, the 
program will scan the files and count the n-grams for each file (my program
uses 4 character sequences). When the program is learning, there will be no
output until the end. For some reason, it waits until the end of the learning
to print anything to the console. It may take up to 5 secs for the program to
finish and it will prompt you when it is done.

On exit, the program will ask you if you want to save. If you want to save, 
you must first choose a directory by clicking "Open Directory." The serialized
data will be saved as "virusdb.ser" in the chosen directory.

The top-right panel contains the current directory as well as the number of
files that have been used to train the program in the current session.


PROBABILITY CALCULATION
-----------------------
I calculated probabiilites using this method:

http://en.wikipedia.org/wiki/Naive_Bayes_classifier#Document_Classification

When a file is scanned, I compute the natural log of the ratios. The formula
is as follows:

ln[(p(virus|file)/p(not virus|file)] = sum[p(word|virus)/p(word|not virus)]

If the sum of the logs is greater than 0, then the file is a virus. If the 
sum is less than 0, then the file is benign.

N-grams that have not been seen in the training phase are skipped.

Overall, this method is okay at categorizing files. There are quite a few fals negatives, meaning that virus files are classified as benign.
I believe that this is caused by the unevenness of the two training
directories. Although there are more virus files, there are more n-grams in thebenign directory. Therefore, the counts are generally higher in the benign hashtable, skewing the results a bit towards the benign side in cases where viruses

OTHER INFO
----------------------
When the state is saved as "virusdb.ser", the VirusDB object is serialized.
VirusDB contains two hash tables, one virus and one benign files, a list of thefiles used for training, the number of files used for training, and the 
directory the file was saved in.

