package com.controllers;

import com.models.Book;
import com.models.BookFilters;
import com.models.HandlerFunction;
import com.models.storage.IStorage;
import com.windows.Alerts;
import com.windows.WindowCreateBook;

import java.net.URL;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import com.jfoenix.controls.*;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class Index implements Initializable {
    @FXML private JFXTextField fxFilterName;
    @FXML private JFXTextField fxFilterAuthor;
    @FXML private JFXTextField fxFilterPublisher;
    @FXML private JFXSlider fxFilterPriceFrom;
    @FXML private JFXSlider fxFilterPriceTo;
    @FXML private JFXSlider fxFilterPagesFrom;
    @FXML private JFXSlider fxFilterPagesTo;
    @FXML private JFXDatePicker fxFilterDateFrom;
    @FXML private JFXDatePicker fxFilterDateTo;
    @FXML private TableView<Book> fxBooksTable;
    @FXML private TableColumn<Book, String> fxBooksTableColumnName;
    @FXML private TableColumn<Book, String> fxBooksTableColumnAuthor;
    @FXML private TableColumn<Book, String> fxBooksTableColumnPublisher;
    @FXML private TableColumn<Book, Double> fxBooksTableColumnPrice;
    @FXML private TableColumn<Book, Package> fxBooksTableColumnPages;
    @FXML private TableColumn<Book, LocalDate> fxBooksTableColumnDate;

    // Menu Events
    @FXML private void fxOnMenuAbout() { onAbout(); }
    @FXML private void fxOnMenuAddBook() { onAddBook(); }
    @FXML private void fxOnMenuAppExit() { onExit(); }
    @FXML private void fxOnMenuEditBook() { onEditBook(); }
    @FXML private void fxOnMenuHelp() { onHelp(); }
    @FXML private void fxOnMenuRemoveBooks() { onRemoveBook(); }
    @FXML private void fxOnMenuResetFilters() { onResetFilters(); }

    // Tools Events
    @FXML private void fxOnToolAdd() { onAddBook(); }
    @FXML private void fxOnToolEdit() { onEditBook(); }
    @FXML private void fxOnToolRemove() { onRemoveBook(); }

    // Filters Events
    @FXML private void fxOnRunFilter() { onRunFilter(); }
    @FXML private void fxOnResetFilters() { onResetFilters(); }

    // Путь к файлу в котором хранятся книги
    static final private String DB_URL = "src/com/labs/lab5/ELib/configs/books-db.txt";

    // Хранилице книг - содежит все книги
    private IStorage<Book> storage;

    // Массив книг удовлетворяющих фильтр (привязан к содержимому таблици)
    private ObservableList<Book> filteredBooks = FXCollections.observableArrayList();

    // Фильтры книг
    private BookFilters filters = new BookFilters();

    private WindowCreateBook windowAddBook;
    private WindowCreateBook windowEditBook;
    private Alerts alerts = new Alerts();

    // Граничные значение параметров книг
    // Привязываються к минимальны/максимальным значения фильтров (fxml-элементов)
    private final DoubleProperty minPrice = new SimpleDoubleProperty(0);
    private final DoubleProperty maxPrice = new SimpleDoubleProperty(100);
    private final IntegerProperty minPages = new SimpleIntegerProperty(0);
    private final IntegerProperty maxPages = new SimpleIntegerProperty(100);

    // Книга что редактируется в данный момент (проверка на null обязательна)
    private Book editingBook;

    // Функция закритыя окна
    private HandlerFunction onExitHandler;

    public Index() {

    }

    public Index(IStorage<Book> storage) {
        this.storage = storage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (storage == null) {
            alerts.show(alerts.getAlertErr(), "Can't load books");

            onExit();
            return;
        }

        updateFilterLimits();
        resetFilters();

        runFilter();
        initTable();
    }

    private void runFilter() {
        updateFilterLimits();
        updateFilters();
        updateFilteredBooks();
    }

    private void updateFilters() {
        filters.setNameFilter(fxFilterName.getText());
        filters.setAuthorFilter(fxFilterAuthor.getText());
        filters.setPublisherFilter(fxFilterPublisher.getText());

        filters.setPriceFromFilter(fxFilterPriceFrom.getValue());
        filters.setPriceToFilter(fxFilterPriceTo.getValue());

        filters.setPagesFromFilter((int)fxFilterPagesFrom.getValue());
        filters.setPagesToFilter((int)fxFilterPagesTo.getValue());

        filters.setDateFromFilter(fxFilterDateFrom.getValue());
        filters.setDateToFilter(fxFilterDateTo.getValue());
    }

    private void updateFilteredBooks() {
        filteredBooks.clear();
        filteredBooks.addAll(storage.getArrOfData(book -> filters.check(book)));
    }

    private void updateFilterLimits() {
        var books = storage.getArrOfData();
        if (books.length == 0) return;

        double _minPrice = Arrays.stream(books).min(Comparator.comparingDouble(Book::getPrice)).get().getPrice() - 10;
        double _maxPrice = Arrays.stream(books).max(Comparator.comparingDouble(Book::getPrice)).get().getPrice() + 10;

        int _minPages = Arrays.stream(books).min(Comparator.comparingInt(Book::getPages)).get().getPages() - 10;
        int _maxPages = Arrays.stream(books).max(Comparator.comparingInt(Book::getPages)).get().getPages() + 10;

        if (_minPrice < 0) _minPrice = 0;
        if (_minPages < 0) _minPages = 0;

        minPrice.set(_minPrice);
        minPages.set(_minPages);

        maxPrice.set(_maxPrice);
        maxPages.set(_maxPages);
    }

    /**
     * Сбрасывает фильтры (значения полей fxml)
     * Первый вызов должен быть после первого вызова метода initBinds()
     */
    private void resetFilters() {
        filters.reset();

        fxFilterName.clear();
        fxFilterAuthor.clear();
        fxFilterPublisher.clear();

        fxFilterPriceFrom.setValue(minPrice.getValue());
        fxFilterPriceTo.setValue(maxPrice.getValue());

        fxFilterPagesFrom.setValue(minPages.getValue());
        fxFilterPagesTo.setValue(maxPages.getValue());

        fxFilterDateFrom.setValue(null);
        fxFilterDateTo.setValue(null);
    }

    private void showWindowAddBook() {
        initWindowAddBook();

        windowAddBook.getController().setDateField(LocalDate.now());
        windowAddBook.getWindow().show();
    }

    private void showWindowEditBook() {
        // Редактируется ли уже какая-то книга
        Book selected = fxBooksTable.getSelectionModel().getSelectedItem();
        boolean alreadyEditing = false;

        if (editingBook != null && editingBook == selected) {
            alreadyEditing = true;

        } else  {
            editingBook = selected;
        }

        if (editingBook == null) {
            alerts.show(alerts.getAlertInfo(), "No selected books");
            return;
        }

        initWindowEditBook();

        if (!alreadyEditing) {
            windowEditBook.getController().setValuesBy(editingBook);
        }

        windowEditBook.getWindow().show();
    }

    private void initWindowAddBook() {
        if (windowAddBook != null) return;

        try {
            windowAddBook = new WindowCreateBook("Add New Book");
            windowAddBook.getController().setOnSaveHandler(this::addNewBook);

        } catch (IOException err) {
            alerts.show(alerts.getAlertErr(), "Can't open the window");
        }
    }

    private void initWindowEditBook() {
        if (windowEditBook != null) return;

        try {
            windowEditBook = new WindowCreateBook("Edit The Book");
            windowEditBook.getController().setOnSaveHandler(this::editBook);

        } catch (IOException err) {
            alerts.show(alerts.getAlertErr(), "Can't open the window");
            System.out.println(err.toString());
        }
    }

    /**
     * Добавляет новую книгу в storage из windowAddBook
     */
    private void addNewBook() {
        Book newBook = windowAddBook.getController().create();

        if (newBook == null) {
            alerts.show(alerts.getAlertErr(), "Incorrect data");
        }

        try {
            storage.add(newBook);

        } catch ( IOException err) {
            alerts.show(alerts.getAlertErr(), "Something's wrong. Can't save the book");
            return;
        }

        windowAddBook.getController().reset();
        windowAddBook.getWindow().close();

        runFilter();

        fxBooksTable.getSelectionModel().select(newBook);
        fxBooksTable.scrollTo(newBook);
    }

    /**
     * Редактирует текущюю вибраную книгу
     * (на деле заменяет текущую выбранную книну на книгу созаную пользователем в windowEditBook)
     * Для правильной работы нужно убедиться, что за время, пока пользователь
     * Вводил данные книги выбранная книга в таблице не поменялась
     * (Например, окно windowEditBook может быть APPLICATION_MODAL)
     */
    private void editBook() {
        int selectedIndex = fxBooksTable.getSelectionModel().getSelectedIndex();

        if (editingBook == null) {
            alerts.show(alerts.getAlertErr(), "Something's wrong. Can't save the changes");
            return;
        }

        Book edited = windowEditBook.getController().create();

        if (edited == null) {
            alerts.show(alerts.getAlertErr(), "Incorrect data");
            return;
        }

        try {
            storage.replace(editingBook, edited);

        } catch (IOException err) {
            alerts.show(alerts.getAlertErr(), "Something's wrong. Can't save the changes");
        }

        windowEditBook.getController().reset();
        windowEditBook.getWindow().close();

        editingBook = null;

        runFilter();

        fxBooksTable.getSelectionModel().select(selectedIndex);
    }

    private void removeSelectedBook() {
        int selectedIndex = fxBooksTable.getSelectionModel().getSelectedIndex();

        Book selected = fxBooksTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            alerts.show(alerts.getAlertInfo(), "No selected books");
            return;
        }

        String mes = "Are you really want to remove '"+ selected.getName() +"'?";
        var res = (alerts.show(alerts.getAlertConfirm(), mes));

        if (!alerts.getAnswer(res)) {
            return;
        }

        try {
            storage.remove(selected);

        } catch (IOException err) {
            alerts.show(alerts.getAlertErr(), "Something's wrong. Can't remove the book");
        }

        runFilter();

        // Восстановить выделение книги в таблице (обязательно после runFilter)
        fxBooksTable.getSelectionModel().select(selectedIndex);
    }

    private void initTable() {
        fxBooksTableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        fxBooksTableColumnAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        fxBooksTableColumnPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        fxBooksTableColumnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        fxBooksTableColumnPages.setCellValueFactory(new PropertyValueFactory<>("pages"));
        fxBooksTableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        fxBooksTable.setItems(filteredBooks);
    }

    private void onAddBook() {
        showWindowAddBook();
    }

    private void onEditBook() {
        showWindowEditBook();
    }

    private void onRunFilter() {
        runFilter();
    }

    private void onRemoveBook() {
        removeSelectedBook();
    }

    private void onHelp() {
        alerts.show(alerts.getAlertInfo(), "Only You Can Help yourself!");
    }

    private void onAbout() {
        alerts.show(alerts.getAlertInfo(), "ELib - Your World Of Books!");
    }

    private void onExit() {
        onExitHandler.call();
    }

    private void onResetFilters() {
        resetFilters();
        runFilter();
    }

    public JFXTextField getFxFilterName() {
        return fxFilterName;
    }

    public void setFxFilterName(JFXTextField fxFilterName) {
        this.fxFilterName = fxFilterName;
    }

    public double getMinPrice() {
        return minPrice.get();
    }

    public DoubleProperty minPriceProperty() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice.set(minPrice);
    }

    public double getMaxPrice() {
        return maxPrice.get();
    }

    public DoubleProperty maxPriceProperty() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice.set(maxPrice);
    }

    public int getMinPages() {
        return minPages.get();
    }

    public IntegerProperty minPagesProperty() {
        return minPages;
    }

    public void setMinPages(int minPages) {
        this.minPages.set(minPages);
    }

    public int getMaxPages() {
        return maxPages.get();
    }

    public IntegerProperty maxPagesProperty() {
        return maxPages;
    }

    public void setMaxPages(int maxPages) {
        this.maxPages.set(maxPages);
    }

    public void setOnExit(HandlerFunction handler) {
        this.onExitHandler = handler;
    }

    public void onTest(MouseEvent mouseEvent) {
        System.out.println("test");
    }
}
