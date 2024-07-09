package gr.hua.dit.compiler.errors;

public class BashColors {
    static final String Red(String wrappedStr) {
        return "\033[0;31m" + wrappedStr + "\033[0m";
    }

    static final String Green(String wrappedStr) {
        return "\033[0;32m" + wrappedStr + "\033[0m";
    }

    static final String Yellow(String wrappedStr) {
        return "\033[0;33m" + wrappedStr + "\033[0m";
    }

    static final String Blue(String wrappedStr) {
        return "\033[0;34m" + wrappedStr + "\033[0m";
    }

    static final String Purple(String wrappedStr) {
        return "\033[0;35m" + wrappedStr + "\033[0m";
    }

    static final String Cyan(String wrappedStr) {
        return "\033[0;36m" + wrappedStr + "\033[0m";
    }

    static final String White(String wrappedStr) {
        return "\033[0;37m" + wrappedStr + "\033[0m";
    }
}
