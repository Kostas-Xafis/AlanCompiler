package gr.hua.dit.compiler.types;

public class DataType extends Type {

    public enum DataTypeEnum {
        BOOL,
        BYTE,
        CHAR,
        STRING,
        INT,
        PROC,
    };

    static public DataType IntType = Int();
    static public DataType Int() {
        return new DataType(DataTypeEnum.INT);
    }
    static public DataType BoolType = Bool();
    static public DataType Bool() {
        return new DataType(DataTypeEnum.BOOL);
    }
    static public DataType ByteType = Byte();
    static public DataType Byte() {
        return new DataType(DataTypeEnum.BYTE);
    }
    static public DataType CharType = Char();
    static public DataType Char() {
        return new DataType(DataTypeEnum.BYTE);
    }
    static public DataType StringType = String();
    static public DataType String() {
        return new DataType(DataTypeEnum.BYTE, 0);
    }
    static public DataType ProcType = Proc();
    static public DataType Proc() {
        return new DataType(DataTypeEnum.PROC);
    }

    private final DataTypeEnum type;

    private Boolean isRef = false;

    // Array properties
    private Boolean isArray = false;
    private Boolean isAccessed = false;
    private int arraySize = -1;

    // Char property
    private Boolean isHex = false;

    public DataType(DataTypeEnum e, Integer s) {
        type = e;
        isArray = true;
        arraySize = s;
    }

    public DataType(DataTypeEnum e) {
        type = e;
    }

    public void setArray(Integer size) {
        isArray = true;
        arraySize = size;
    }
    public Boolean isArray() {
        return isArray;
    }

    public Boolean isAccessed() {
        return isAccessed;
    }

    public void setAccessed(Boolean accessed) {
        isAccessed = accessed;
    }

    public void setRef(Boolean ref) {
        isRef = ref;
    }

    public Boolean isRef() {
        return isRef;
    }

    public Integer getArraySize() {
        return arraySize;
    }

    public Type getBaseType() {
        return this;
    }

    public String toString() {
        return (isRef ? "&" : "") + type.toString() + (isArray ? "[" + arraySize + "]" : "") + (isAccessed ? " accessed" : "");
    }

    public boolean equals(Type t) {
        if (t instanceof DataType) {
            DataType that = (DataType)t;
            if (this.isAccessed == that.isAccessed) {
                // If both base types are exposed or not, then do full type check
                return this.type == that.type && this.isArray == that.isArray;
            } else if (this.isArray == that.isArray) {
                // Either one is accessed but both are arrays, the there is a type mismatch
                return false;
            }

            return this.type == that.type;
        }
        return false;
    }

    public DataType copy() {
        if (isArray) {
            DataType ndt = new DataType(type, arraySize);
            ndt.isAccessed = isAccessed;
            ndt.isRef = isRef;
            return ndt;
        }
        return new DataType(type);
    }
}
