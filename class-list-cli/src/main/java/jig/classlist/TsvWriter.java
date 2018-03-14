package jig.classlist;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import static java.util.stream.Collectors.joining;

public class TsvWriter {

    private static final Logger logger = Logger.getLogger(TsvWriter.class.getName());

    public <T> void writeTo(ReportFactory<T> factory, Path output) {
        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            writeTsvRow(writer, factory.headerLabel());

            for (List<String> row : factory.rowList()) {
                writeTsvRow(writer, row);
            }

            logger.info(output.toAbsolutePath() + "を出力しました。");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeTsvRow(BufferedWriter writer, List<String> list) throws IOException {
        writer.write(list.stream().collect(joining("\t")));
        writer.newLine();
    }
}
