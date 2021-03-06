package org.dddjava.jig.domain.model.jigmodel.lowmodel.relation.method;

import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.field.FieldDeclaration;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.field.FieldDeclarations;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.method.MethodDeclaration;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.method.MethodDeclarations;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.type.TypeIdentifier;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.type.TypeIdentifiers;

import java.util.*;

/**
 * メソッドが依存しているもの
 */
public class MethodDepend {

    Set<TypeIdentifier> usingTypes;
    List<FieldDeclaration> usingFields;
    List<MethodDeclaration> usingMethods;
    boolean hasNullReference;

    public MethodDepend(Set<TypeIdentifier> usingTypes, List<FieldDeclaration> usingFields, List<MethodDeclaration> usingMethods, boolean hasNullReference) {
        this.usingTypes = usingTypes;
        this.usingFields = usingFields;
        this.usingMethods = usingMethods;
        this.hasNullReference = hasNullReference;
    }

    public UsingFields usingFields() {
        FieldDeclarations fieldDeclarations = usingFields.stream().collect(FieldDeclarations.collector());
        return new UsingFields(fieldDeclarations);
    }

    public UsingMethods usingMethods() {
        return new UsingMethods(usingMethods.stream().collect(MethodDeclarations.collector()));
    }

    public boolean notUseMember() {
        return usingFields.isEmpty() && usingMethods.isEmpty();
    }

    public boolean hasNullReference() {
        return hasNullReference;
    }

    public Collection<TypeIdentifier> collectUsingTypes() {
        Set<TypeIdentifier> typeIdentifiers = new HashSet<>(usingTypes);

        for (FieldDeclaration usingField : usingFields) {
            typeIdentifiers.add(usingField.declaringType());
            typeIdentifiers.add(usingField.typeIdentifier());
        }

        for (MethodDeclaration usingMethod : usingMethods) {
            // メソッドやコンストラクタの持ち主
            // new演算子で呼び出されるコンストラクタの持ち主をここで捕まえる
            typeIdentifiers.add(usingMethod.declaringType());

            // 呼び出したメソッドの戻り値の型
            typeIdentifiers.add(usingMethod.methodReturn().typeIdentifier());
        }

        return typeIdentifiers;
    }

    public TypeIdentifiers usingTypes() {
        return new TypeIdentifiers(new ArrayList<>(collectUsingTypes()));
    }
}
