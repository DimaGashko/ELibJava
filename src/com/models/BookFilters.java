package com.models;

import com.models.Book;
import java.time.LocalDate;

/**
 * Класс фильтров книг
 */
public class BookFilters {
    // Классы-обертки, так как необходимо,
    // Что бы было доступным значение null

    private String nameFilter = null;
    private String authorFilter = null;
    private String publisherFilter = null;
    private Double priceFromFilter = null;
    private Double priceToFilter = null;
    private Integer pagesFromFilter = null;
    private Integer pagesToFilter = null;
    private LocalDate dateFromFilter = null;
    private LocalDate dateToFilter = null;

    public BookFilters() {

    }

    /**
     * Проверяет удовлетворяет ли переданная книга данному фильтру
     * @param book проверяемая книга
     * @return удовлетворяет ли книга фильтру
     */
    public boolean check(Book book) {
        return (
                   checkStringFilter(nameFilter, book.getName())
                && checkStringFilter(authorFilter, book.getAuthor())
                && checkStringFilter(publisherFilter, book.getPublisher())

                && checkNumberFilter(priceFromFilter, priceToFilter, book.getPrice())
                && checkNumberFilter(pagesFromFilter, pagesToFilter, book.getPages())

                && checkDateFilter(dateFromFilter, dateToFilter, book.getDate())
        );
    }

    /**
     * Восстанавливает значение фильтров в исходного состояние
     */
    public void reset() {
        nameFilter = null;
        authorFilter = null;
        publisherFilter = null;
        priceFromFilter = null;
        priceToFilter = null;
        pagesFromFilter = null;
        pagesToFilter = null;
        dateFromFilter = null;
        dateToFilter = null;
    }

    /**
     * Проверяет удовлетворяет ли строковый параметр фильтру
     * Если и from и to равены null, то считается, что параметр может быть любым
     * Если from равен null то считается подходящим все до to
     * Если to равен null то считается подходящим все после from
     * @param filter фильтр
     * @param param проверяемое значение
     * @return удовлетворяет ли параметр фильтру
     */
    private boolean checkStringFilter(String filter, String param) {
        if (filter == null || filter.isEmpty()) return true;

        filter = filter.toLowerCase().trim();
        param = param.toLowerCase().trim();

        return param.contains(filter);
    }

    /**
     * Проверяет удовлетворяет ли числовой параметр фильтру
     * Если и from и to равены null, то считается, что параметр может быть любым
     * Если from равен null то считается подходящим все до to
     * Если to равен null то считается подходящим все после from
     * @param from минимальное значение
     * @param to максимальное значение
     * @param param проверяемое значение
     * @return удовлетворяет ли параметр фильтру
     */
    private boolean checkNumberFilter(Double from, Double to, Double param) {
        if (from == null && to == null) return true;

        if (to == null) return param >= from;
        if (from == null) return param <= to;

        return (from <= param) && (to >= param);
    }

    /**
     * Проверяет удовлетворяет ли числовой параметр фильтру
     * Если и from и to равены null, то считается, что параметр может быть любым
     * Если from равен null то считается подходящим все до to
     * Если to равен null то считается подходящим все после from
     * @param from минимальное значение
     * @param to максимальное значение
     * @param param проверяемое значение
     * @return удовлетворяет ли параметр фильтру
     */
    private boolean checkNumberFilter(Integer from, Integer to, Integer param) {
        return checkNumberFilter(
                (from != null) ? from.doubleValue() : null,
                (to != null) ? to.doubleValue() : null,
                (param != null) ? param.doubleValue() : null
        );
    }

    /**
     * Проверяет удовлетворяет ли фильтра-дата параметр фильтру
     * Если и from и to равены null, то считается, что параметр может быть любым
     * Если from равен null то считается подходящим все до to
     * Если to равен null то считается подходящим все после from
     * @param from минимальное значение
     * @param to максимальное значение
     * @param param проверяемое значение
     * @return удовлетворяет ли параметр фильтру
     */
    private boolean checkDateFilter(LocalDate from, LocalDate to, LocalDate param) {
        if (from == null && to == null) return true;

        if (to == null) return param.compareTo(from) >= 0;
        if (from == null) return param.compareTo(to) <= 0;

        return (from.compareTo(param) <= 0) && (to.compareTo(param) >= 0);
    }

    public String getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
    }

    public String getAuthorFilter() {
        return authorFilter;
    }

    public void setAuthorFilter(String authorFilter) {
        this.authorFilter = authorFilter;
    }

    public String getPublisherFilter() {
        return publisherFilter;
    }

    public void setPublisherFilter(String publisherFilter) {
        this.publisherFilter = publisherFilter;
    }

    public Double getPriceFromFilter() {
        return priceFromFilter;
    }

    public void setPriceFromFilter(Double priceFromFilter) {
        this.priceFromFilter = priceFromFilter;
    }

    public Double getPriceToFilter() {
        return priceToFilter;
    }

    public void setPriceToFilter(Double priceToFilter) {
        this.priceToFilter = priceToFilter;
    }

    public Integer getPagesFromFilter() {
        return pagesFromFilter;
    }

    public void setPagesFromFilter(Integer pagesFromFilter) {
        this.pagesFromFilter = pagesFromFilter;
    }

    public Integer getPagesToFilter() {
        return pagesToFilter;
    }

    public void setPagesToFilter(Integer pagesToFilter) {
        this.pagesToFilter = pagesToFilter;
    }

    public LocalDate getDateFromFilter() {
        return dateFromFilter;
    }

    public void setDateFromFilter(LocalDate dateFromFilter) {
        this.dateFromFilter = dateFromFilter;
    }

    public LocalDate getDateToFilter() {
        return dateToFilter;
    }

    public void setDateToFilter(LocalDate dateToFilter) {
        this.dateToFilter = dateToFilter;
    }

    @Override
    public String toString() {
        return nameFilter + ";"
                + authorFilter + ";"
                + publisherFilter + ";"
                + priceFromFilter + ";"
                + priceToFilter + ";"
                + pagesFromFilter + ";"
                + pagesToFilter + ";"
                + dateFromFilter + ";"
                + dateToFilter;
    }
}
