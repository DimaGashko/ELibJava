package com.models.storage;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Класс для хранения данных на основании текстового файла
 * @param <T> класс хранимых данных
 * @version 0.0.0.1
 *
 * TODO: заменить Array на ArrayList
 */
public class TextStorage<T> implements IStorage<T> {
    static final private int DEF_BUFFER_SIZE = 1000;

    //Размер массива для хранения данных
    private int bufferSize;

    //Текстовый файл для хранения данных
    private File dataFile;

    // Класс хранимих данных
    // Используется для создания new T[]
    private Class<T> dataClass;

    // Массив хранимых данных
    private T[] data;

    // Количество данных
    private int len;

    private StringifyFunction<T> stringify;
    private ParseFunction<T> parse;

    public TextStorage(String url, StringifyFunction<T> stringify, ParseFunction<T> parse, Class<T> dataClass) throws IOException {
        setBufferSize(DEF_BUFFER_SIZE);

        setDataClass(dataClass);
        setStringify(stringify);
        setParse(parse);

        setCleanDataArr();
        initFile(url);

        load();
    }

    @Override
    public void add(T item) throws IOException {
        save(item);
        addToArr(item);
    }

    @Override
    public void addAll(T[] items) throws IOException {
        for (T item : items) {
            add(item);
        }
    }

    @Override
    public ArrayList<T> getData() {
        return new ArrayList<>(Arrays.asList(data));
    }

    @Override
    public ArrayList<T> getData(Predicate<T> filter) {
        T[] res = getArrOfData(filter);
        return new ArrayList<>(Arrays.asList(res));
    }

    @Override
    public T[] getArrOfData() {
        return Arrays.stream(Arrays.copyOf(data, len))
                .toArray(this::_getTArray);
    }

    @Override
    public T[] getArrOfData(Predicate<T> filter) {
        return Arrays.stream(getArrOfData())
                .filter(filter)
                .toArray(this::_getTArray);
    }

    @Override
    public void remove(T item) throws IOException {
        remove(el -> el.equals(item));
    }

    @Override
    public void remove(Predicate<T> isRemoved) throws IOException {
        T[] newData = Arrays.stream(getArrOfData())
                // Не через методы типа indexOf
                // Так как может быть несколько одинковых элементов
                .filter(el -> !isRemoved.test(el))
                .toArray(this::_getTArray);

        setData(newData);
    }

    @Override
    public void replace(T prevItem, T newItem) throws IOException {
        for (int i = 0; i < data.length; i++) {
            if (!data[i].equals(prevItem)) continue;

            data[i] = newItem;
            break;
        }

        resaveAll();
    }

    /**
     * Добавл8яет элемент данных в массив данных
     * @param item добавляемый массив
     * TODO: убрать после внедрения ArrayList
     */
    private void addToArr(T item) {
        if (len == data.length) return;
        data[len++] = item;
    }

    /**
     * Сохраняет переданный элемент в текстовом файле
     * @param item сохраняемый элемент
     */
    private void save(T item) throws IOException {
        _writeToFile(stringify.call(item), false);
    }

    /**
     * Устанавливает новые данные (старые удаляются)
     * @param items новые данные
     */
    private void setData(T[] items) throws IOException{
        T[] prevData = data;
        int prevLen = len;

        setCleanDataArr();

        len = items.length;
        System.arraycopy(items, 0, data, 0, len);

        try {
            resaveAll();

        } catch(IOException err) {
            data = prevData;
            len = prevLen;

            throw err;
        }
    }

    /**
     * Сохраняет все данные в файл (перезаписывая его)
     */
    private void resaveAll() throws IOException {
        var strItems = Arrays.stream(getArrOfData())
                .map(T::toString)
                .toArray(String[]::new);

        _writeToFile(strItems, true);
    }

    /**
     * Удаляет все данные из массивов (не влияет на данные в файле)
     * TODO: удалить после внедрения ArrayList
     */
    private void setCleanDataArr() {
        data = _getTArray(bufferSize);
        len = 0;
    }

    /**
     * Загружает данные из текстового файла в массив данных
     */
    private void load() throws IOException {
        dataFile.createNewFile();

        String itemStr;

        try (var fr = new FileReader(dataFile);
             var reader = new BufferedReader(fr)
        ) {

            while ((itemStr = reader.readLine()) != null) {
                addToArr(parse.call(itemStr));
            }

        }

    }

    /**
     * Создает массив new T[len]
     *
     * @param len длина массива
     * @return массив данных
     */
    private T[] _getTArray(int len) {
        @SuppressWarnings("unchecked")
        var arr = (T[]) Array.newInstance(dataClass, len);

        return arr;
    }

    /**
     * Записывает в файл строку
     *
     * @param str   строка для записи
     * @param clean очистить перед записью (либо записывать в конец)
     */
    private void _writeToFile(String str, boolean clean) throws IOException {
        String[] strs = {str};
        _writeToFile(strs, clean);
    }

    /**
     * Записывает в файл строки
     *
     * @param strs  массив строк для записи
     * @param clean запись в конец файла
     */
    private void _writeToFile(String[] strs, boolean clean) throws IOException {
        dataFile.createNewFile();

        try(var fr = new FileWriter(dataFile, !clean);
            var writer = new PrintWriter(fr)
        ) {

            if (clean) {
                writer.print("");
            }

            for (String str : strs) {
                writer.println(str);
            }

        }
    }

    private void initFile(String url) {
        dataFile = new File(url);

        // Создает родительские каталоги
        dataFile.getParentFile().mkdirs();
    }

    private void setDataClass(Class<T> dataClass) {
        this.dataClass = dataClass;
    }

    private void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setStringify(StringifyFunction<T> stringify) {
        this.stringify = stringify;
    }

    public void setParse(ParseFunction<T> parse) {
        this.parse = parse;
    }

    /**
     * Функция преобразования объекта в строку
     * @param <T> тип объекта
     */
    @FunctionalInterface
    public interface StringifyFunction<T> {
        String call(T item);
    }

    /**
     * Функция преобразования строки в объект
     * @param <T> тип объекта
     */
    @FunctionalInterface
    public interface ParseFunction<T> {
        T call(String strT);
    }
}