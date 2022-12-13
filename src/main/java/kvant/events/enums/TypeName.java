package kvant.events.enums;

public enum TypeName {
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

    TypeName(String classTypeName, String alt) {
        this.classTypeName = classTypeName;
        this.alt = alt;
    }

    public String getClassTypeName() {
        return classTypeName;
    }

    public String getAlt() {
        return alt;
    }

    public static String getTypeName(Class<?> clazz) {
        var values = values();
        var classTypeName = clazz.getTypeName();

        for (TypeName name : values) {
            if (name.classTypeName.equals(classTypeName))
                return name.alt;
        }

        return classTypeName;
    }
}
