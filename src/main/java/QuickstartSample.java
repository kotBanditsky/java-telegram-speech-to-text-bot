import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import com.google.cloud.speech.v1.RecognizeRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class QuickstartSample {
    public static String sampleRecognize(String localFilePath) {

        String answer = null;

        try (SpeechClient speechClient = SpeechClient.create()) {
            String languageCode = "ru-RU";
            int sampleRateHertz = 16000;
            RecognitionConfig.AudioEncoding encoding = RecognitionConfig.AudioEncoding.LINEAR16;
            RecognitionConfig config =
                    RecognitionConfig.newBuilder()
                            .setLanguageCode(languageCode)
                            .setSampleRateHertz(sampleRateHertz)
                            .setEncoding(encoding)
                            .build();
            Path path = Paths.get(localFilePath);
            byte[] data = Files.readAllBytes(path);
            ByteString content = ByteString.copyFrom(data);
            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(content).build();
            RecognizeRequest request =
                    RecognizeRequest.newBuilder().setConfig(config).setAudio(audio).build();
            RecognizeResponse response = speechClient.recognize(request);

            for (SpeechRecognitionResult result : response.getResultsList()) {
                // First alternative is the most probable result
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                answer = alternative.getTranscript();
                System.out.printf("Transcript: %s\n", alternative.getTranscript());
            }

        } catch (Exception exception) {
            System.err.println("Failed to create the client due to: " + exception);
        }
        return answer;
    }

    public static void main(String[] args) throws Exception {}

}