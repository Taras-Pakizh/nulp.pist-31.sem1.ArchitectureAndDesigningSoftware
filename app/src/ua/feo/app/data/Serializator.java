package ua.feo.app.data;

import java.io.*;

public class Serializator {

    private final static String FILE = "program_data.data";

    public static ProgramData restore() {
        try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(FILE))) {
            return (ProgramData) stream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return ProgramData.NULL_PROGRAM_DATA;
        }
    }

    public static void save(ProgramData programData) {
        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(FILE))) {
            stream.writeObject(programData);
        } catch (IOException ignored) { }
    }

}
