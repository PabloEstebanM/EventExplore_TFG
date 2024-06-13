package com.example.eventexplore_tfg.Data;

import java.io.Serializable;
import java.util.List;


/**
 * Represents an event with its details.
 *
 * @version 1.0
 * @autor Pablo Esteban Martín
 */
public class Event implements Serializable {
    private String id;
    private String name;
    private String place;
    private String description_Long;
    private String description_short;
    private String startDate;
    private String endDate;
    private double price;
    private int ticketsSoldNumber;
    private String companyName;
    private String contactEmail;
    private String urlTicket;
    private List<String> categories;

    //private List<Uri> photos

    /**
     * Constructor for creating an Event object.
     *
     * @param id               Event ID.
     * @param name             Event name.
     * @param place            Event location.
     * @param description_Long Long description of the event.
     * @param description_short Short description of the event.
     * @param startDate        Event start date.
     * @param endDate          Event end date.
     * @param price            Event price.
     * @param ticketsSoldNumber Number of tickets sold for the event.
     * @param companyName      Company name organizing the event.
     * @param contactEmail     Contact email for the event.
     * @param urlTicket        Ticket URL for the event.
     */
    public Event(String id, String name, String place, String description_Long, String description_short, String startDate, String endDate, double price, int ticketsSoldNumber, String companyName, String contactEmail, String urlTicket) {
        this.id = id;
        this.name = name;
        this.place = place;
        this.description_Long = description_Long;
        this.description_short = description_short;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.ticketsSoldNumber = ticketsSoldNumber;
        this.companyName = companyName;
        this.contactEmail = contactEmail;
        this.urlTicket = urlTicket;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription_Long() {
        return description_Long;
    }

    public void setDescription_Long(String description_Long) {
        this.description_Long = description_Long;
    }

    public String getDescription_short() {
        return description_short;
    }

    public void setDescription_short(String description_short) {
        this.description_short = description_short;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTicketsSoldNumber() {
        return ticketsSoldNumber;
    }

    public void setTicketsSoldNumber(int ticketsSoldNumber) {
        this.ticketsSoldNumber = ticketsSoldNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getUrlTicket() {
        return urlTicket;
    }

    public void setUrlTicket(String urlTicket) {
        this.urlTicket = urlTicket;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
