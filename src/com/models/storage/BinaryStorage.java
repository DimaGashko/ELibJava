package com.models.storage;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Класс для хранения данных на основании текстового файла
 * @param <T> класс хранимых данных
 */
public class BinaryStorage<T> implements IStorage<T> {

    //Текстовый файл для хранения данных
    private File dataFile;

    // Класс хранимих данных
    private Class<T> dataClass;

    // Массив хранимых данных
    private ArrayList<T> data = new ArrayList<>();

    public BinaryStorage(String url, Class<T> dataClass) throws IOException {
        setDataClass(dataClass);
        initFile(url);

        load();
    }

    @Override
    public void add(T item) throws IOException {
        data.add(item);
        save();
    }

    @Override
    public void addAll(T[] items) throws IOException {
        data.addAll(Arrays.asList(items));
        save();
    }

    @Override
    public ArrayList<T> getData() {
        return data;
    }

    @Override
    public ArrayList<T> getData(Predicate<T> filter) {
        return data.stream().filter(filter)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public T[] getArrOfData() {
        return data.toArray(_getTArray(data.size()));
    }

    @Override
    public T[] getArrOfData(Predicate<T> filter) {
        return data.stream().filter(filter).toArray(this::_getTArray);
    }

    @Override
    public void remove(T item) throws IOException {
        remove(el -> el.equals(item));
    }

    @Override
    public void remove(Predicate<T> isRemoved) throws IOException {
        data = data.stream().filter(item -> !isRemoved.test(item))
                .collect(Collectors.toCollection(ArrayList::new));

        save();
    }

    @Override
    public void replace(T prevItem, T newItem) throws IOException {
        Collections.replaceAll(data, prevItem, newItem);
        save();
    }

    /**
     * Сохраняет переданные элементы в бинарном файле
     */
    private void save() throws IOException {

        try (FileOutputStream fos = new FileOutputStream(dataFile);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {

            out.writeObject(data);
        }
    }

    /**
     * Загружает данные из текстового файла в массив данных
     */
    private void load() throws IOException {
        dataFile.createNewFile();

        try(FileInputStream fis = new FileInputStream(dataFile);
            ObjectInputStream ois = new ObjectInputStream(fis)) {

            data = (ArrayList<T>)ois.readObject();

        } catch (EOFException err) {
            data = new ArrayList<>();

        } catch (ClassNotFoundException err) {
            err.printStackTrace();

        }
    }

    private void initFile(String url) {
        dataFile = new File(url);

        // Создает родительские каталоги
        dataFile.getParentFile().mkdirs();
    }

    /**
     * Создает массив new T[len]
     *
     * @param len длина массива
     * @return массив данных
     * <p>
     */
    private T[] _getTArray(int len) {
        @SuppressWarnings("unchecked")
        var arr = (T[]) Array.newInstance(dataClass, len);

        return arr;
    }

    private void setDataClass(Class<T> dataClass) {
        this.dataClass = dataClass;
    }

}