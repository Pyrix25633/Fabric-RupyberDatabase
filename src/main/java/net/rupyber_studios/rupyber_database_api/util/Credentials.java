package net.rupyber_studios.rupyber_database_api.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Credentials {
    private static final Random RANDOM = new Random();
    public static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    public static final char[] LETTERS = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static final char[] SYMBOLS = new char[]{'-', '_', '.', '*', '/', '@', '#'};

    public static @NotNull String generatePassword() {
        List<Character> chars = new ArrayList<>();
        chars.add(randomDigit());
        chars.add(randomSymbol());
        for(int i = 0; i < 6; i++)
            chars.add(randomChar());
        return buildString(chars);
    }

    public static @NotNull String generateToken() {
        List<Character> chars = new ArrayList<>();
        for(int i = 0; i < 16; i++)
            chars.add(randomChar());
        return buildString(chars);
    }

    private static char randomChar() {
        return switch(RANDOM.nextInt(3)) {
            case 0 -> randomDigit();
            case 1 -> randomLetter();
            default -> randomSymbol();
        };
    }

    private static char randomDigit() {
        return DIGITS[RANDOM.nextInt(DIGITS.length)];
    }

    private static char randomLetter() {
        return LETTERS[RANDOM.nextInt(LETTERS.length)];
    }

    private static char randomSymbol() {
        return SYMBOLS[RANDOM.nextInt(SYMBOLS.length)];
    }

    private static @NotNull String buildString(@NotNull List<Character> chars) {
        StringBuilder builder = new StringBuilder();
        while(!chars.isEmpty())
            builder.append(chars.remove(RANDOM.nextInt(chars.size())));
        return builder.toString();
    }
}