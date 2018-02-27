package jig.cli;

import jig.application.service.DiagramService;
import jig.application.service.ThingService;
import jig.infrastructure.javaparser.PackageInfoLibrary;
import jig.model.diagram.Diagram;
import jig.model.diagram.DiagramIdentifier;
import jig.model.diagram.DiagramSource;
import jig.model.jdeps.*;
import jig.model.tag.JapaneseNameDictionaryLibrary;
import jig.model.thing.ThingFormatter;
import jig.model.thing.Things;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

@SpringBootApplication(scanBasePackages = "jig")
public class PackageDiagramApplication implements CommandLineRunner {

    public static void main(String[] args) {
        System.setProperty("PLANTUML_LIMIT_SIZE", "65536");
        SpringApplication.run(PackageDiagramApplication.class, args);
    }

    @Value("${target.class}")
    String targetClass;

    @Value("${package.pattern}")
    String packagePattern;

    @Value("${output.diagram.name}")
    String outputDiagramName;

    @Autowired
    ThingService thingService;
    @Autowired
    DiagramService diagramService;
    @Autowired
    JapaneseNameDictionaryLibrary library;

    @Override
    public void run(String... args) throws IOException {
        Path output = Paths.get(outputDiagramName);

        Things things = thingService.toModels(
                new AnalysisCriteria(
                        new SearchPaths(Collections.singletonList(Paths.get(targetClass))),
                        new AnalysisClassesPattern(packagePattern + "\\..+"),
                        new DependenciesPattern(packagePattern + "\\..+"),
                        AnalysisTarget.PACKAGE));
        ThingFormatter thingFormatter = thingService.modelFormatter(library.borrow());
        DiagramSource diagramSource = diagramService.toDiagramSource(things, thingFormatter);
        DiagramIdentifier identifier = diagramService.request(diagramSource);
        diagramService.generate(identifier);
        Diagram diagram = diagramService.get(identifier);

        try (BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(output))) {
            outputStream.write(diagram.getBytes());
        }
    }

    @Bean
    public JapaneseNameDictionaryLibrary library(@Value("${target.source}") String targetSource) {
        Path sourceRoot = Paths.get(targetSource);
        return new PackageInfoLibrary(sourceRoot);
    }
}
