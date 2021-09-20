#ONBOARDING README

##Project Details
1. **Name**: 
   Onboarding Project
2. **Description:** 
   This project consists of a simple REPL, with some math and star commands.
3. **Team Members and Contributions:** 
   This was a solo project by movo5hmkwxub4roi. I worked on this for about 15 hours...a lot of time was spent trying 
   to work through multiple set-up issues when going through the lab. 


##Design Choices
**Classes, and their Relationship**
My project has two classes: Main and MathBot. Their relationship is that Main contains two instances of MathBot. 
This is used to support the "add" and "subtract" commands of my REPL. 

**Unusual algorithms**
Instead of using a sort algorithm, I ended up using a very inefficient, brute force method to come up with star neighbors.
I looped through the entire list of stars the same amount of times as requested neighbors. Everytime I loop through the 
star list, I look for the star that has the smallest possible distance from the given position, that has still not been 
added to the neighbor list. 


##Bugs
First, all commands seem to work except for the two "naive_neighbors" command. I tried using print statements to see 
whether my REPL was even detecting the command, and it did not react. I've tried editing if statements and making 
changes to the logic of that command but "naive_neighbors" still does not even seem to be processed. Because of this, 
I do not know if my "naive_neighbors" finds the right neighbors.
Another bug is that the exceptions instead throw the exception on line 209 in Main. I suspect that this is because I 
am trying to throw the exceptions while inside a `<try{}>` indent. Instead of printing out my exception, it goes to the
corresponding `<catch{}>` statement.

##Tests
When I run `<./cs32-test src/test/system/*.test>` in my command line, only 3/4 tests pass. The one that fails is the 
"Running error". I am unsure about what this means exactly; I am guessing that it could be related to why my 
"naive_neighbor" commands don't work. 
I did not have time to write my own tests. I did look at the tests in the src/test/system/stars, and simulated them by 
running my REPL with the input in the files from that folder, to test for basic functionality. 


##Links
*Here are some links that helped me write some of the code in my project:*
https://stackoverflow.com/questions/11990211/reading-csv-file-using-bufferedreader-resulting-in-reading-alternative-lines
https://stackoverflow.com/questions/8065532/how-to-randomly-pick-an-element-from-an-array
https://javatutoring.com/distance-between-two-points-java-program/
