package rankpage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 8/14/2017.
 */
public class Page {
    String url;
    private double PR = 1;
    public double PRcopy = 0.15;
    double referenceTo;
    ArrayList<String> referenceUrls;

    public Page(String url, ArrayList<String> arrayList) {
        this.url = url;
        this.PRcopy = 0.15;
        referenceUrls = arrayList;

//        switch (url){
//            case "A":
//                referenceUrls.add("B");
//                referenceUrls.add("C");
//                referenceTo = 2;
//                break;
//            case "B":
//                referenceUrls.add("C");
//                referenceTo = 1;
//                break;
//            case "C":
//                referenceUrls.add("A");
//                referenceTo = 1;
//                break;
//        }

    }

    public Page(String url) {
        this.url = url;
        referenceUrls = new ArrayList<>();
    }

    void updatePR(){
        PR = PRcopy;
    }

    public double getPR() {
        return PR;
    }
}
