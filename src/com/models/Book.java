package com.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

//TODO: исключить возможность появления разделителя parse/toString в полях

public class Book implements Serializable  {
    private final UUID id = UUID.randomUUID();
    private String name = "No name";
    private String author = "No author";
    private String publisher = "No publisher";
    private LocalDate date = LocalDate.of(0,1,1);
    private int pages = 0;
    private double price = 0;

    public Book() {

    }

    public Book(String name, String author, String publisher, int year, int pages, double price) {
        setName(name);
        setAuthor(author);
        setPublisher(publisher);
        setYear(year);
        setPages(pages);
        setPrice(price);
    }

    public Book(String name, String author, String publisher, LocalDate date, int pages, double price) {
        setName(name);
        setAuthor(author);
        setPublisher(publisher);
        setDate(date);
        setPages(pages);
        setPrice(price);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getYear() {
        return date.getYear();
    }

    public void setYear(int year) {
        date = LocalDate.of(year, date.getMonth(), date.getDayOfYear());
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return getName() + ";" +
                getAuthor() + ";" +
                getPublisher() + ";" +
                getDate() + ";" +
                getPages() + ";" +
                getPrice();
    }

    static public Book parse(String strBook) {
        String[] components = strBook.split(";");

        if (components.length < 6) {
            return null;
        }

        String name = components[0];
        String author = components[1];
        String publisher = components[2];

        LocalDate date = LocalDate.parse(components[3]);
        int pages = Integer.parseInt(components[4]);
        double price = Double.parseDouble(components[5]);

        return new Book(name, author, publisher, date, pages, price);
    }
}


/*









package com.labs.lab_s4_3;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

public class Book implements Comparable<Book>, Serializable {
    private int id;
    private String name;
    private String author;
    private String publisher;
    private LocalDate publishDate;
    private int pages;
    private double price;

    public Book(int id, String name, String author, String publisher, LocalDate publishDate, int pages, double price) {
        setId(id);
        setName(name);
        setAuthor(author);
        setPublisher(publisher);
        setPublishDate(publishDate);
        setPages(pages);
        setPrice(price);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publishDate=" + publishDate +
                ", pages=" + pages +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return id == book.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int compareTo(@NotNull Book o) {
        return Comparator.comparing(Book::getId, Integer::compareTo).compare(this, o);
    }
}










* */