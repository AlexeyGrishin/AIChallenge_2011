package common;

public class Compare {

    private int result = 0;

    public boolean upIfSmaller(int ourValue, int anotherValue) {
        result = ourValue - anotherValue;
        return result != 0;
    }

    public boolean upIfLarger(int ourValue, int anotherValue) {
        result = -(ourValue - anotherValue);
        return result != 0;
    }

    public boolean downIfSmaller(int ourValue, int anotherValue) {
        result = -(ourValue - anotherValue);
        return result != 0;
    }

    public boolean downIfLarger(int ourValue, int anotherValue) {
        result = ourValue - anotherValue;
        return result != 0;
    }

    public boolean use(int comparatorResult) {
        result = comparatorResult;
        return result != 0;
    }

    public int result() {
        return result;
    }

    public Compare values(boolean result) {
        //ignore
        return this;
    }

    private static Compare instance;

    public static Compare compare() {
        instance = new Compare();
        return instance;
    }

}
