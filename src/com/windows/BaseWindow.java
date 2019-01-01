package com.windows;

import javafx.stage.Stage;
import java.io.IOException;

/**
 * Базовый класс окна
 * @param <T> Тип конструктора
 */
abstract public class BaseWindow<T> {
    protected Stage window;
    protected T controller;

    public BaseWindow() throws IOException {
        this(new Stage());
    }

    public BaseWindow(Stage window) throws IOException {
        setWindow(window);

        init();
    }

    private void setWindow(Stage window) {
        this.window = window;
    }

    protected void init() throws IOException {
        load();
    }

    abstract protected void load() throws IOException;

    public Stage getWindow() {
        return window;
    }

    public T getController() {
        return controller;
    }
}
