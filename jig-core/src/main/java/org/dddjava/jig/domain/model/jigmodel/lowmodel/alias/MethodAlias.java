package org.dddjava.jig.domain.model.jigmodel.lowmodel.alias;

import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.method.MethodIdentifier;

/**
 * メソッド別名
 */
public class MethodAlias {
    MethodIdentifier methodIdentifier;
    Alias alias;

    public MethodAlias(MethodIdentifier methodIdentifier, Alias alias) {
        this.methodIdentifier = methodIdentifier;
        this.alias = alias;
    }

    public static MethodAlias empty(MethodIdentifier methodIdentifier) {
        return new MethodAlias(methodIdentifier, Alias.empty());
    }

    public MethodIdentifier methodIdentifier() {
        return methodIdentifier;
    }

    public String asText() {
        return alias.toString();
    }

    public String asTextOrDefault(String defaultText) {
        if (alias.exists()) {
            return asText();
        }
        return defaultText;
    }
}
