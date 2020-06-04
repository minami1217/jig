package org.dddjava.jig.domain.model.jigsource.jigloader.analyzed;

import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.annotation.FieldAnnotation;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.annotation.TypeAnnotation;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.field.FieldDeclaration;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.field.FieldDeclarations;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.field.StaticFieldDeclaration;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.field.StaticFieldDeclarations;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.method.MethodDeclarations;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.type.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 型の実装から読み取れること
 */
public class TypeFact {

    final ParameterizedType type;

    final boolean canExtend;

    final ParameterizedType superType;
    final List<ParameterizedType> interfaceTypes;

    final List<TypeAnnotation> typeAnnotations = new ArrayList<>();
    final List<StaticFieldDeclaration> staticFieldDeclarations = new ArrayList<>();

    final List<FieldAnnotation> fieldAnnotations = new ArrayList<>();
    final List<FieldDeclaration> fieldDeclarations = new ArrayList<>();

    final List<MethodFact> instanceMethodFacts = new ArrayList<>();
    final List<MethodFact> staticMethodFacts = new ArrayList<>();
    final List<MethodFact> constructorFacts = new ArrayList<>();

    final Set<TypeIdentifier> useTypes = new HashSet<>();

    public TypeFact(ParameterizedType type,
                    ParameterizedType superType,
                    List<ParameterizedType> interfaceTypes,
                    boolean canExtend) {
        this.type = type;
        this.superType = superType;
        this.interfaceTypes = interfaceTypes;
        this.canExtend = canExtend;

        for (TypeParameter typeParameter : type.typeParameters().list()) {
            this.useTypes.add(typeParameter.typeIdentifier());
        }
        this.useTypes.add(superType.typeIdentifier());
        for (ParameterizedType interfaceType : interfaceTypes) {
            this.useTypes.add(interfaceType.typeIdentifier());
        }
    }

    public TypeIdentifier typeIdentifier() {
        return type.typeIdentifier();
    }

    public boolean canExtend() {
        return canExtend;
    }

    public boolean isEnum() {
        return superType.typeIdentifier().equals(new TypeIdentifier(Enum.class));
    }

    public boolean hasInstanceMethod() {
        return !instanceMethodFacts().isEmpty();
    }

    public boolean hasField() {
        return !fieldDeclarations.isEmpty();
    }

    public FieldDeclarations fieldDeclarations() {
        return new FieldDeclarations(fieldDeclarations);
    }

    public StaticFieldDeclarations staticFieldDeclarations() {
        return new StaticFieldDeclarations(staticFieldDeclarations);
    }

    public TypeIdentifiers useTypes() {
        for (MethodFact methodFact : allMethodFacts()) {
            useTypes.addAll(methodFact.methodDepend().collectUsingTypes());
        }

        return new TypeIdentifiers(new ArrayList<>(useTypes));
    }

    public List<MethodFact> instanceMethodFacts() {
        return instanceMethodFacts;
    }

    public List<TypeAnnotation> typeAnnotations() {
        return typeAnnotations;
    }

    public List<FieldAnnotation> annotatedFields() {
        return fieldAnnotations;
    }

    public void registerTypeAnnotation(TypeAnnotation typeAnnotation) {
        typeAnnotations.add(typeAnnotation);
        useTypes.add(typeAnnotation.type());
    }

    public void registerField(FieldDeclaration field) {
        fieldDeclarations.add(field);
        useTypes.add(field.typeIdentifier());
    }

    public void registerStaticField(StaticFieldDeclaration field) {
        staticFieldDeclarations.add(field);
        useTypes.add(field.typeIdentifier());
    }

    public void registerUseType(TypeIdentifier typeIdentifier) {
        useTypes.add(typeIdentifier);
    }

    public void registerFieldAnnotation(FieldAnnotation fieldAnnotation) {
        fieldAnnotations.add(fieldAnnotation);
    }

    public void registerInstanceMethodFacts(MethodFact methodFact) {
        instanceMethodFacts.add(methodFact);
    }

    public void registerStaticMethodFacts(MethodFact methodFact) {
        staticMethodFacts.add(methodFact);
    }

    public void registerConstructorFacts(MethodFact methodFact) {
        constructorFacts.add(methodFact);
    }

    public List<MethodFact> allMethodFacts() {
        ArrayList<MethodFact> list = new ArrayList<>();
        list.addAll(instanceMethodFacts);
        list.addAll(staticMethodFacts);
        list.addAll(constructorFacts);
        return list;
    }

    public ParameterizedType superType() {
        return superType;
    }

    public TypeDeclaration type() {
        return new TypeDeclaration(type, superType, new ParameterizedTypes(interfaceTypes));
    }

    public List<ParameterizedType> interfaceTypes() {
        return interfaceTypes;
    }

    public MethodDeclarations methodDeclarations() {
        return allMethodFacts().stream()
                .map(MethodFact::methodDeclaration)
                .collect(MethodDeclarations.collector());
    }
}
