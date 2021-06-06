Telegram Speech-to-Text Java Bot (Google Cloud API)

To test the program, write in the private.config file:

- connection string to your MongoDB database
- telegram bot token and username (create in BotFatner)

To translate voice into text, the Google Speech API is used, in the test version of the bot, the connection is made via https://cloud.google.com/sdk/docs/authorizing

What the bot can do:

- when sending a voice message to the bot in the chat (there is a limit on the length of the message of 5 seconds), the bot transcribes the voice into text and returns it with a message to the chat
- each transcribed message has two buttons: "save" and "delete"
- the "save" button transfers the message to the MongoDB database
- the "delete" button removes the transcribed message from the chat
- the command "/notes" displays all saved user notes
- the command "/delX" delete the note from the database, "X" is the note number
