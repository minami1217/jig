package jig.domain.model.specification;

import jig.domain.model.thing.Name;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodDescriptor {

    // TODO 名前の混乱をなんとかする
    public final String methodName;
    public final Name name;
    public final String descriptor;

    public MethodDescriptor(String className, String name, String descriptor) {
        this.methodName = name;
        this.descriptor = descriptor;

        Type[] argumentTypes = Type.getArgumentTypes(descriptor);
        String argumentsString = Arrays.stream(argumentTypes).map(Type::getClassName).collect(Collectors.joining(",", "(", ")"));
        this.name = new Name(className + "." + name + argumentsString);
    }

    public final List<Name> usingFieldTypeNames = new ArrayList<>();
    public final List<Name> usingMethodNames = new ArrayList<>();

    public void addFieldInstruction(String owner, String name, String descriptor) {
        // 使っているフィールドの型がわかればOK
        Type type = Type.getType(descriptor);
        usingFieldTypeNames.add(new Name(type.getClassName()));
    }

    public void addMethodInstruction(String owner, String name, String descriptor) {
        // 使ってるメソッドがわかりたい
        Name ownerTypeName = new Name(owner);
        String methodName = ownerTypeName.value() + "." + name;
        usingMethodNames.add(new Name(methodName));
    }
}