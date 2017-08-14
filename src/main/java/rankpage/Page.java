package rankpage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 8/14/2017.
 */
public class Page {
    String url;
    private double PR = 1;
    public double PRcopy = 1;
    double referenceTo;

    public Page(String url) {
        this.url = url;

        switch (url){
            case "A":
                referenceTo = 2;
                break;
            case "B":
                referenceTo = 1;
                break;
            case "C":
                referenceTo = 1;
                break;
        }

    }

    void updatePR(){
        PR = PRcopy;
    }

    public double getPR() {
        return PR;
    }
}
