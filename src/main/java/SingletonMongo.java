import com.mongodb.*;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import static com.mongodb.client.model.Filters.eq;

public class SingletonMongo {

    private static MongoClient mClient;

    private MongoClient getMongoClient() {
        if (mClient == null) {
            mClient = new MongoClient(new MongoClientURI(Config.MONGO_CONNECT));
        }
        return mClient;
    }

    private MongoDatabase getDB() {
        return getMongoClient().getDatabase("kotDatabase");
    }

    // Utility method to get user collection
    private MongoCollection<Document> getUserCollection(long chat_id) {
        return getDB().getCollection(String.valueOf(chat_id));
    }

    public static ArrayList<String> main(String answer, String deleter, long chat_id, long message_id) {
        System.out.println("Connecting to DB...");
        SingletonMongo demo = new SingletonMongo();
        demo.getUserCollection(chat_id);
        ArrayList<String> base = null;

        if (answer == "answer") {
            base = new ArrayList<>();
            base = demo.readMsg(chat_id);
        } else {
            demo.saveMsg(answer, chat_id, message_id);
        }

        if (!(deleter == "deleter")) {
            demo.deleteMsg(deleter, chat_id);
        }

        return base;
    }

    // Read all documents from Mongo user collection and add to java collection
    private ArrayList<String> readMsg(long chat_id) {
        ArrayList<String> list = new ArrayList<>();
        try (MongoCursor<Document> cur = getUserCollection(chat_id).find().iterator()) {
            while (cur.hasNext()) {
                Document doc = cur.next();
                ArrayList questions = new ArrayList<>(doc.values());
                list.add(questions.get(1).toString());
            }
        }
        return list;
    }

    // Save the transmitted message, chat ID and message ID to the database
    private void saveMsg(String answer, long chat_id, long message_id) {
        Document document = new Document("question", answer)
                .append("chat_id", chat_id)
                .append("message_id", message_id);
        getUserCollection(chat_id).insertOne(document);
    }

    // Removing a record from the database
    private void deleteMsg(String answer, long chat_id) {
        getUserCollection(chat_id).deleteOne(eq("question", answer));;
    }
}
