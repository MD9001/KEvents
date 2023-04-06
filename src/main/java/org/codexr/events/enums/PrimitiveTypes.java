package org.codexr.events.enums;

public enum PrimitiveTypes {
    VOID(Void.class.getTypeName(), "void"),
    INTEGER(Integer.class.getTypeName(), "int"),
    LONG(Long.class.getTypeName(), "long"),
    BYTE(Byte.class.getTypeName(), "byte"),
    SHORT(Short.class.getTypeName(), "short"),
    FLOAT(Float.class.getTypeName(), "float"),
    DOUBLE(Double.class.getTypeName(), "double"),
    CHAR(Character.class.getTypeName(), "char"),
    BOOLEAN(Boolean.class.getTypeName(), "boolean");

    private final String classTypeName, alt;

    PrimitiveTypes(String classTypeName, String alt) {
        this.classTypeName = classTypeName;
        this.alt = alt;
    }

    public static String getTypeName(Class<?> clazz) {
        var values = values();
        var classTypeName = clazz.getTypeName();

        for (PrimitiveTypes name : values) {
            if (name.classTypeName.equals(classTypeName))
                return name.alt;
        }

        return classTypeName;
    }

    public String getClassTypeName() {
        return classTypeName;
    }

    public String getAlt() {
        return alt;
    }
}
