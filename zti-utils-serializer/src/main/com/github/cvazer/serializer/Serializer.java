package com.github.cvazer.serializer;

import java.io.*;

/**
 * This utility class works from an instance and allows you to easily serialize and deserialize objects from your program.
 * @param <T> Class type for instance to work with.
 *
 * @author Yan Frankovski
 * @since ZTIU 1.0.2
 */
@SuppressWarnings({"ResultOfMethodCallIgnored", "WeakerAccess"})
public class Serializer<T> {
    /** This value stores reference to file to work with */
    private File file;

    /**
     * Empty constructor
     */
    public Serializer() {}

    /**
     * Constructor that creates an instance of {@code Serializer} class and associates it with file by given path.
     * @param path  {@code String} path to the file.
     */
    public Serializer(String path) {
        file = new File(path);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Constructor that creates an instance of {@code Serializer} class and associates it with given {@code File}.
     * @param file A file to work with.
     */
    public Serializer(File file) {
        if (!file.exists()) try {file.createNewFile();} catch (IOException e) { e.printStackTrace();}
        this.file = file;
    }

    /**
     * This method should be called to serialize object to stored {@code File} location.
     * @param obj {@code Object} to be serialized.
     */
    public void serialize(T obj){
        try {
            FileOutputStream fis = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fis);
            out.writeObject(obj);
            out.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method should be called to deserialize object from stored {@code File} location.
     * @return {@code Object} deserialized from stored {@code File} location.
     */
    @SuppressWarnings("unchecked")
    public T deserialize() {
        FileInputStream fis;
        ObjectInputStream in;
        try {
            fis = new FileInputStream(file);
            in = new ObjectInputStream(fis);
            Object object = in.readObject();
            in.close();
            fis.close();
            return (T) object;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * @return returns stored {@code File} instance.
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file New {@code File} instance to be stored.
     */
    public void setFile(File file) {
        this.file = file;
    }

}
