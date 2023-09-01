import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class TextAnalyzer {
    private static final int TEXT_LENGTH = 100000;
    private static final int NUM_TEXTS = 10000;
    private static final int QUEUE_CAPACITY = 100;

    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    public static void main(String[] args) {
        Thread textGeneratorText = new Thread(TextAnalyzer::generateAndEnqueueTexts);
        Thread analyzerAThread = new Thread(() -> analyzeCharacter('a', queueA));
        Thread analyzerBThread = new Thread(() -> analyzeCharacter('b', queueB));
        Thread analyzerCThread = new Thread(() -> analyzeCharacter('c', queueC));

        textGeneratorText.start();
        analyzerAThread.start();
        analyzerBThread.start();
        analyzerCThread.start();

        try {
            textGeneratorText.join();
            analyzerAThread.join();
            analyzerBThread.join();
            analyzerCThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int maxA = maxCountA.get();
        int maxB = maxCountB.get();
        int maxC = maxCountC.get();

        System.out.println("Максимальное количество символов 'a': " + maxA);
        System.out.println("Максимальное количество символов 'b': " + maxB);
        System.out.println("Максимальное количество символов 'c': " + maxC);

    }

    private static void generateAndEnqueueTexts() {
        String letters = "abc";
        Random random = new Random();
        for (int i = 0; i < NUM_TEXTS; i++) {
            StringBuilder text = new StringBuilder();
            for (int j = 0; j < TEXT_LENGTH; j++) {
                text.append(letters.charAt(random.nextInt(letters.length())));
            }
            try {
                queueA.put(text.toString());
                queueB.put(text.toString());
                queueC.put(text.toString());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private static final AtomicInteger maxCountA = new AtomicInteger(0);
    private static final AtomicInteger maxCountB = new AtomicInteger(0);
    private static final AtomicInteger maxCountC = new AtomicInteger(0);

    private static void analyzeCharacter(char character, BlockingQueue<String> queue) {
        int maxCount = 0;

        while (true) {
            try {
                String text = queue.take();
                int count = countCharacter(text, character);
                if (count > maxCount) {
                    maxCount = count;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        if (character == 'a') {
            maxCountA.set(maxCount);
        } else if (character == 'b') {
            maxCountB.set(maxCount);
        } else if (character == 'c') {
            maxCountC.set(maxCount);
        }
    }

    private static int countCharacter(String text, char character) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == character) {
                count++;
            }
        }
        return count;
    }
}
