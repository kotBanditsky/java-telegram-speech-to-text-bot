import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static java.lang.Math.toIntExact;

public class SpeechToTextBot extends TelegramLongPollingBot {

    public String originalFileName; // Variable for storing a random name of the audio file saved from tg

    public static void main(String[] args) {
        try {
            ApiContextInitializer.init(); // Initializing api
            new TelegramBotsApi().registerBot(new SpeechToTextBot()); // Create a new bot
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return Config.TELEGRAM_BOT_USERNAME;
    } // Take the name of the bot in telegram
    public String getBotToken() {
        return Config.TELEGRAM_BOT_TOKEN;
    } // Take the token of the bot in telegram

    public void onUpdateReceived(Update update) { // Instructions when receiving a message

        Message incomingMessage = update.getMessage(); // Write the incoming message to the variable

        if (update.hasCallbackQuery()) { // Check if the inline message button is pressed

            String message_text = update.getCallbackQuery().getMessage().getText();
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();

            if (call_data.equals(Config.SAVE_MSG)) {
                sendSaveMessage(incomingMessage, chat_id, message_id, message_text, "Note saved");
            }

            if (call_data.equals(Config.DELETE_MSG)) {
                sendDeleteMessage(incomingMessage, chat_id, message_id);
            }

        } else if (incomingMessage == null || !incomingMessage.hasVoice()) { // Check the message is empty or does not contain a voice

            if (incomingMessage.getText().equals("/notes")) {

                sendKeyMessage(incomingMessage);

            } else if (incomingMessage.getText().equals("/help")) {

                sendTechMessage(incomingMessage, "Enter /delX, where Х is the note to delete" + "\n" + "Enter /notes, to display saved notes");

            } else if (incomingMessage.getText().matches("^/del[0-9]+$")) {

                int value = Integer.parseInt(incomingMessage.getText().replaceAll("[^0-9]", ""));
                sendDelMessage(incomingMessage, value - 1);
                sendTechMessage(incomingMessage, "Note deleted");

            } else {

                sendTechMessage(incomingMessage, "Record a voice message");

            }

        } else {

            if (incomingMessage.getVoice().getDuration() > 5) {

                sendTechMessage(incomingMessage, "The note must be less than 5 seconds");

            } else {

                String responseMessageText = "Something went wrong"; // Create a variable with a message - write a basic error message

                try {

                    URL fileURL = getFileURL(incomingMessage); // Write the result of the getFileURL method to the fileURL variable - a link to the audio file
                    safeDownloadAndConvert(fileURL); // Download from the link and convert the audio message file using the safeDownloadAndConvert method
                    File file = new File(originalFileName);
                    file.delete(); // Delete the original downloaded audio file
                    QuickstartSample sc = new QuickstartSample();
                    String outputFileName2 = originalFileName.substring(0, originalFileName.lastIndexOf(".")) + ".wav"; // Create a variable outputFileName2 in which we substitute the wav extension for the file name
                    responseMessageText = sc.sampleRecognize(outputFileName2); // Process the recording file (already converted to wav) via google speach api and save the result (text) to the responseMessageText variable
                    File file2 = new File(outputFileName2);
                    file2.delete(); // delete the converted audio file

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    sendReplyMessage(incomingMessage, responseMessageText); // we pass the incoming message and the result of audio processing (text) to the sendReplyMessage method
                }
            }
        }
    }

    // method for extracting a link to audio from a telegram message
    private URL getFileURL(Message containerMessage) throws TelegramApiException, MalformedURLException {
        GetFile getFile = new GetFile().setFileId(containerMessage.getVoice().getFileId());
        return new URL(execute(getFile).getFileUrl(getBotToken()));
    }

    // method for downloading from telegram and converting audio file to wav
    private synchronized byte[] safeDownloadAndConvert(URL fileURL) throws IOException {
        Random generate = new Random(); // Run the randomizer
        originalFileName = generate.nextInt(5000) + "voiceMessage.oga"; // Generate a prefix to the file name with the randomizer and store the name in the originalFileName variable
        FileUtils.copyURLToFile(fileURL, new File(originalFileName)); // Save the file by url from tg to the directory with the name generated above
        return FFmpegWrapper.convertToWAV(originalFileName); // Return the file converted to wav via the FFmpegWrapper method (save it in the directory)
    }

    private void sendDelMessage(Message replyToMessage, int value) {
        ArrayList<String> queries = SingletonMongo.main("answer", "deleter", replyToMessage.getChatId(), replyToMessage.getMessageId());
        String fordel = queries.get(value);
        SingletonMongo.main("answer", fordel, replyToMessage.getChatId(), replyToMessage.getMessageId());
    }

    private void sendKeyMessage(Message replyToMessage) {
        try {
            ArrayList<String> queries = SingletonMongo.main("answer", "deleter", replyToMessage.getChatId(), replyToMessage.getMessageId());
            Iterator<String> iterator = queries.iterator(); // Getting an iterator for a list
            int count = 1;
            StringBuffer sBuffer = new StringBuffer("Your saved notes:" + System.lineSeparator() + System.lineSeparator());

            while (iterator.hasNext()) {   // Checking if there are more items
                String text = iterator.next(); // Get the current item and go to the next
                sBuffer.append(count + ". " + text + System.lineSeparator());
                count++;
            }

            SendMessage sendMessage = new SendMessage()
                    .enableMarkdown(true)
                    .setChatId(replyToMessage.getChatId().toString())
                    .setText(String.valueOf(sBuffer));
            execute(sendMessage);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendReplyMessage(Message replyToMessage, String text) {
        try {
            SendMessage sendMessage = new SendMessage()
                    .enableMarkdown(true)
                    .setChatId(replyToMessage.getChatId().toString())
                    .setText(text);
            execute(sendMessage.setReplyMarkup(KeyboardFactory.setupInlineKeyboard()));

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendSaveMessage(Message replyToMessage, long chat_id, long message_id, String message_text, String text) {
        String answer = "Note saved";
        EditMessageText new_message = new EditMessageText()
                .setChatId(chat_id)
                .setMessageId(toIntExact(message_id))
                .setText(answer);
        try {
            SingletonMongo.main(message_text, "deleter", chat_id, message_id);
            execute(new_message);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendTechMessage(Message replyToMessage, String text) {
        try {
            SendMessage sendMessage = new SendMessage()
                    .enableMarkdown(true)
                    .setChatId(replyToMessage.getChatId().toString())
                    .setText(text);
            execute(sendMessage.setReplyMarkup(KeyboardFactory.setupBaseKeyboard()));

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendDeleteMessage(Message replyToMessage, long chat_id, long message_id) {
        try {
            DeleteMessage deleteMessage = new DeleteMessage(chat_id, (int) message_id);
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
