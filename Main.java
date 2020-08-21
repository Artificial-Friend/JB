package encryptdecrypt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import static java.lang.String.valueOf;

class SelectionContext {

    private CryptographyAlgorithm algorithm;

    public void setAlgorithm(CryptographyAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public Text create(Text text) {
        return this.algorithm.operate(text);
    }
}

interface CryptographyAlgorithm {

    Text operate(Text text);
}

class EncodeUnicodeAlgorithm implements CryptographyAlgorithm {

    private final Text text;

    public EncodeUnicodeAlgorithm(Text text) {
        this.text = text;
    }

    @Override
    public Text operate(Text text) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.data.length(); i++) {
            if (" ".equals(text.data.charAt(i))) {
                result.append(text.data.charAt(i));
            } else {
                result.append((char) (text.data.charAt(i) + text.key));
            }
        }

        text.data = result.toString();
        return text;
    }
}

class EncodeShiftAlgorithm implements CryptographyAlgorithm {

    private final Text text;

    public EncodeShiftAlgorithm(Text text) {
        this.text = text;
    }

    @Override
    public Text operate(Text text) {
        StringBuilder result = new StringBuilder();

        for (char ch : text.data.toCharArray()) {
            if (ch <= 'z' && ch >= 'a') {
                int a = ((ch - 'a') + (text.key % 26)) % 26;
                result.append((char) ('a' + a));
            } else if (ch <= 'Z' && ch >= 'A') {
                int a = ((ch - 'A') + (text.key % 26)) % 26;
                result.append((char) ('A' + a));
            } else {
                result.append(ch);
            }
        }

        text.data = result.toString();
        return text;
    }
}

class DecodeUnicodeAlgorithm implements CryptographyAlgorithm {

    private final Text text;

    public DecodeUnicodeAlgorithm(Text text) {
        this.text = text;
    }

    @Override
    public Text operate(Text text) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.data.length(); i++) {
            if (" ".equals(text.data.charAt(i))) {
                result.append(text.data.charAt(i));
            } else {
                result.append((char) (text.data.charAt(i) - text.key));
            }
        }

        text.data = result.toString();
        return text;
    }
}

class DecodeShiftAlgorithm implements CryptographyAlgorithm {

    private final Text text;

    public DecodeShiftAlgorithm(Text text) {
        this.text = text;
    }

    @Override
    public Text operate(Text text) {
        StringBuilder result = new StringBuilder();


        for (char ch : text.data.toCharArray()) {
            if (ch <= 'z' && ch >= 'a') {
                int a = ((ch - 'a') + 26 - (text.key % 26)) % 26;
                result.append((char) ('a' + a));
            } else if (ch <= 'Z' && ch >= 'A') {
                int a = ((ch - 'A') + 26 - (text.key % 26)) % 26;
                result.append((char) ('A' + a));
            } else {
                result.append(ch);
            }
        }

        text.data = result.toString();
        return text;
    }
}

class Text {

    String data;
    int key;

    public Text(String data, int key) {
        this.data = data;
        this.key = key;
    }
}


public class Main {

    public static void main(String[] args) {
        StringBuilder path = new StringBuilder();
        StringBuilder resultPath = new StringBuilder();

        String algorithm = "shift";
        boolean in = false;
        boolean out = false;
        int key = 0;
        String mode = "enc";
        String data = "";

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-mode":
                    mode = args[++i];
                    break;
                case "-key":
                    key = Integer.parseInt(args[++i]);
                    break;
                case "-data":
                    data = args[++i];
                    break;
                case "-in":
                    path.append(args[++i]);
                    in = true;
                    break;
                case "-out":
                    resultPath.append(args[++i]);
                    out = true;
                    break;
                case "-alg":
                    algorithm = args[++i];
                    break;
            }
        }

        if (data.equals("") && in) {
            try {
                File userFile = new File(valueOf(path));
                Scanner scanFile = new Scanner(userFile);
                while (scanFile.hasNext()) {
                    data += scanFile.nextLine();
                }
                scanFile.close();
            } catch (IOException e) {
                System.out.println("Error " + e.getMessage());
            }
        }

        final Text text = new Text(data, key);
        final CryptographyAlgorithm alg = create(algorithm, mode, text);

        SelectionContext ctx = new SelectionContext();
        ctx.setAlgorithm(alg);

        final Text temp = ctx.create(text);
        final String result = temp.data;

        if (out) {
            try {
                FileWriter fileWriter = new FileWriter(new File(valueOf(resultPath)));
                fileWriter.write(result);
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error " + e.getMessage());
            }
        } else {
            System.out.println(result);
        }
    }

    public static CryptographyAlgorithm create(String algType, String mode, Text text) {
        if ("shift".equals(algType)) {
            if ("enc".equals(mode)) {
                return new EncodeShiftAlgorithm(text);
            } else if ("dec".equals(mode)) {
                return new DecodeShiftAlgorithm(text);
            } else {
                throw new IllegalArgumentException("Unknown mode type " + mode);
            }
        } else if ("unicode".equals(algType)) {
            if ("enc".equals(mode)) {
                return new EncodeUnicodeAlgorithm(text);
            } else if ("dec".equals(mode)) {
                return new DecodeUnicodeAlgorithm(text);
            } else {
                throw new IllegalArgumentException("Unknown mode type " + mode);
            }
        } else {
            throw new IllegalArgumentException("Unknown algorithm type " + algType);
        }
    }
}