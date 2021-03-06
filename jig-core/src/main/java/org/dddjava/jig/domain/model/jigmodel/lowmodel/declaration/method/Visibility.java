package org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.method;

/**
 * メソッドの可視性
 */
public enum Visibility {
    PUBLIC,
    NOT_PUBLIC;

    public boolean isPublic() {
        return this == PUBLIC;
    }
}
