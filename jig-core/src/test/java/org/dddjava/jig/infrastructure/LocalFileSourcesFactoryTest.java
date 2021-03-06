package org.dddjava.jig.infrastructure;

import org.dddjava.jig.domain.model.jigsource.file.SourcePaths;
import org.dddjava.jig.domain.model.jigsource.file.Sources;
import org.dddjava.jig.domain.model.jigsource.file.binary.BinarySourcePaths;
import org.dddjava.jig.domain.model.jigsource.file.text.CodeSourcePaths;
import org.dddjava.jig.infrastructure.filesystem.LocalFileSourceReader;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalFileSourcesFactoryTest {

    @Test
    void 読み取れないパスが指定されていてもエラーにならない() {
        SourcePaths sourcePaths = new SourcePaths(
                new BinarySourcePaths(Collections.singletonList(Paths.get("invalid-binary-path"))),
                new CodeSourcePaths(Collections.singletonList(Paths.get("invalid-text-path")))
        );

        LocalFileSourceReader sut = new LocalFileSourceReader();
        Sources source = sut.readSources(sourcePaths);

        assertTrue(source.classSources().list().isEmpty());
        assertTrue(source.aliasSource().javaSources().list().isEmpty());
        assertTrue(source.aliasSource().packageInfoSources().list().isEmpty());
    }
}