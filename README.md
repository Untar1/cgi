# CGI summer internship 2019

## Notes

For solving task 1 I used materials from [Spring official sources](https://spring.io/guides/gs/uploading-files/).
Managed to finish only part 1, somewhat.

For task 2 - logging history should be relatively easy to implement. I could either implement a database-style system.
Current system holds only the current "session" as the system makes a wipe of the storage, so it displays all files
uploaded in the current session. However, to make it proper, I could just add either a "display by session ID"
or find something else.

For task 3 - I could use a Decision Tree or Random Forest for decision making task. I was planning on running several
models to test out options. RMSE and ROC/AUC would be sufficient to decide. I could get a training dataset from the
internet (for example, some famous cartoon characters). 
