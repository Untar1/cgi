# CGI summer internship 2019

## Notes

For solving task 1 I used materials from [Spring official sources](https://spring.io/guides/gs/uploading-files/).
For URL uploading, I referenced the materials I found on [Baeldung](https://www.baeldung.com/java-download-file).

For task 2 - I implemented "session" by checking when file was last modified - if the "last modified" is before session started,
the file is not shown. While this is not the "ideal" solution, but it still works.

For task 3 - I used [Dreamix guide](https://dreamix.eu/blog/java/how-to-use-jython-with-spring-boot-2) for [Jython](https://www.jython.org/)
to integrate Python script directly into Spring. I did not implement any machine learning or anything for "prediction". Instead,
I simply get the first 10 bytes of the file and use them as a "seed" for a random integer generator.
