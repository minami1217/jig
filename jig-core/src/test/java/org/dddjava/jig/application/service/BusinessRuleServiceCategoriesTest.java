package org.dddjava.jig.application.service;

import org.dddjava.jig.domain.model.jigdocument.specification.Categories;
import org.dddjava.jig.domain.model.jigmodel.categories.CategoryAngle;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.type.TypeIdentifier;
import org.dddjava.jig.domain.model.jigsource.file.Sources;
import org.junit.jupiter.api.Test;
import testing.JigServiceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@JigServiceTest
public class BusinessRuleServiceCategoriesTest {

    @Test
    void readProjectData(BusinessRuleService businessRuleService, Sources sources, JigSourceReadService jigSourceReadService) {
        jigSourceReadService.readProjectData(sources);
        Categories categories = businessRuleService.categories();

        assertThat(categories.list())
                .extracting(
                        CategoryAngle::typeIdentifier,
                        categoryAngle -> categoryAngle.constantsDeclarationsName(),
                        categoryAngle -> categoryAngle.fieldDeclarations(),
                        categoryAngle -> categoryAngle.userTypeIdentifiers().asSimpleText(),
                        categoryAngle -> categoryAngle.hasParameter(),
                        categoryAngle -> categoryAngle.hasBehaviour(),
                        categoryAngle -> categoryAngle.isPolymorphism()
                ).contains(
                tuple(
                        new TypeIdentifier("stub.domain.model.category.SimpleEnum"),
                        "[A, B, C, D]", "[]", "[AsmFactReaderTest, RelationEnum, ValueAngleTest]",
                        false, false, false
                ),
                tuple(
                        new TypeIdentifier("stub.domain.model.category.HasStaticFieldEnum"),
                        "[A, B]", "[]", "[]",
                        false, false, false
                ),
                tuple(
                        new TypeIdentifier("stub.domain.model.category.ParameterizedEnum"),
                        "[A, B]", "[String param]", "[AsmFactReaderTest, RelationEnum, ValueAngleTest]",
                        true, false, false
                ),
                tuple(
                        new TypeIdentifier("stub.domain.model.category.BehaviourEnum"),
                        "[A, B]", "[]", "[AsmFactReaderTest, RelationEnum]",
                        false, true, false
                ),
                tuple(
                        new TypeIdentifier("stub.domain.model.category.PolymorphismEnum"),
                        "[A, B]", "[]", "[AsmFactReaderTest, RelationEnum]",
                        false, false, true
                ),
                tuple(
                        new TypeIdentifier("stub.domain.model.category.RelationEnum"),
                        "[A, B, C]", "[RichEnum field]", "[]",
                        true, false, false
                ),
                tuple(
                        new TypeIdentifier("stub.domain.model.category.RichEnum"),
                        "[A, B]", "[String param]", "[AsmFactReaderTest, RelationEnum]",
                        true, true, true
                )
        );
    }
}