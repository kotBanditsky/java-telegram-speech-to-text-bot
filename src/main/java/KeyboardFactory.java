import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardFactory {

    public static ReplyKeyboard setupInlineKeyboard() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText(Config.SAVE_MSG).setCallbackData(Config.SAVE_MSG));
        rowInline.add(new InlineKeyboardButton().setText(Config.DELETE_MSG).setCallbackData(Config.DELETE_MSG));
        rowsInline.add(rowInline);
        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }

    public static ReplyKeyboardMarkup setupBaseKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(); // Create ReplyKeyboardMarkup object
        List<KeyboardRow> keyboard = new ArrayList<>(); // Create the keyboard (list of keyboard rows)
        KeyboardRow row = new KeyboardRow(); // Create a keyboard row
        row.add("/notes"); // Set each button, you can also use KeyboardButton objects if you need something else than text
        row.add("/help");
        keyboard.add(row); // Add the first row to the keyboard
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

}
