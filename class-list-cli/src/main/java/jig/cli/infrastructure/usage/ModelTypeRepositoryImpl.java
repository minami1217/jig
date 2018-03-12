package jig.cli.infrastructure.usage;

import jig.domain.model.list.*;
import jig.domain.model.relation.Relation;
import jig.domain.model.relation.RelationRepository;
import jig.domain.model.thing.Name;
import jig.domain.model.thing.Thing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Repository
public class ModelTypeRepositoryImpl implements ModelTypeRepository {

    private List<ModelType> classes;

    RelationRepository relationRepository;

    @Override
    public ModelTypes find(ModelKind modelKind) {
        return new ModelTypes(classes.stream().filter(modelKind::correct).collect(toList()));
    }

    public ModelTypeRepositoryImpl(@Value("${target.class}") String targetClasspath, RelationRepository relationRepository) {
        this.relationRepository = relationRepository;
        URL[] urls = Arrays.stream(targetClasspath.split(":"))
                .map(Paths::get)
                .map(Path::toUri)
                .map(uri -> {
                    try {
                        return uri.toURL();
                    } catch (MalformedURLException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .toArray(URL[]::new);
        Path path = Paths.get(targetClasspath.split(":")[0]);

        try (URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());
             Stream<Path> walk = Files.walk(path)) {

            classes = walk
                    .filter(p -> p.toString().endsWith(".class"))
                    .map(path::relativize)
                    .map(Path::toString)
                    .map(str -> str.replace(".class", "").replace(File.separator, "."))
                    .map(className -> {
                        try {
                            return loader.loadClass(className);
                        } catch (ClassNotFoundException e) {
                            throw new IllegalStateException(e);
                        }
                    })
                    .peek(this::registerRelation)
                    .map(clz -> new ModelType(new Name(clz), ModelMethods.from(clz)))
                    .collect(toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void registerRelation(Class<?> clz) {
        for (Field field : clz.getDeclaredFields()) {
            Relation relation = new Relation(
                    new Thing(new Name(clz)),
                    new Thing(new Name(field.getType()))
            );
            relationRepository.persist(relation);
        }
    }
}
