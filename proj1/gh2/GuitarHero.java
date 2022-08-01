package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    private static final int NUMS = 37;

    public static void main(String[] args) {

        String keyboard =  "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        GuitarString []guitarStrings = new GuitarString[NUMS];

        for(int i = 0; i < NUMS; i += 1) {
            Double frequency = 440 * Math.pow(2, (i - 24) / 12.0);
            guitarStrings[i] = new GuitarString(frequency);
        }

        while(true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                for(int i = 0; i < NUMS; i += 1) {
                    if(keyboard.charAt(i) == key){
                        guitarStrings[i].pluck();
                    }
                }
            }

            double sample = 0.0;
            for(GuitarString gs : guitarStrings){
                sample += gs.sample();
                gs.tic();
            }

            StdAudio.play(sample);

        }
    }
}
