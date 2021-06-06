import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FFmpegWrapper {

    public static byte[] convertToWAV(String inputFileName) throws IOException {
        String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf(".")) + ".wav"; // Change only the extension
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(inputFileName)
                .overrideOutputFiles(true)
                .addOutput(outputFileName)
                .setAudioCodec("pcm_s16le")
                .setAudioSampleRate(16000)
                .done();
        new FFmpegExecutor(new FFmpeg("ffmpeg.exe")).createJob(builder).run();
        return Files.readAllBytes(Paths.get(outputFileName));
    }
}