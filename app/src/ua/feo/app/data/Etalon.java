package ua.feo.app.data;

public class Etalon {

    public static int compareWithEtalon(Boolean[] etalon, Boolean[] data) {
        int count = 0;
        for (int i = 0; i< etalon.length; i++) {
            if (etalon[i] == data[i]) {
                count++;
            }
        }
        return (int) (100. * count / etalon.length);
    }

}
