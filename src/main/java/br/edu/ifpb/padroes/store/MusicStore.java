package br.edu.ifpb.padroes.store;

import br.edu.ifpb.padroes.customer.Customer;
import br.edu.ifpb.padroes.customer.CustomerType;
import br.edu.ifpb.padroes.music.AgeRestriction;
import br.edu.ifpb.padroes.music.Album;
import br.edu.ifpb.padroes.music.MediaType;
import br.edu.ifpb.padroes.store.validation.AgeRestrictionValidator;
import br.edu.ifpb.padroes.store.validation.CreditValidator;
import br.edu.ifpb.padroes.store.validation.PurchaseValidator;
import br.edu.ifpb.padroes.store.validation.StockValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MusicStore {

    private List<Album> inventory = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();

    public void addMusic(Album album) {
        inventory.add(album);
        System.out.println("Added: " + album.getTitle());
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public List<Album> searchMusic(SearchType searchType, String searchTerm) {
        List<Album> results = new ArrayList<>();

        if (searchType.equals(SearchType.TITLE)) {
            for (Album album : inventory) {
                if (album.getTitle().toLowerCase().contains(searchTerm.toLowerCase())) {
                    results.add(album);
                }
            }
        } else if (searchType.equals(SearchType.ARTIST)) {
            for (Album album : inventory) {
                if (album.getArtist().toLowerCase().contains(searchTerm.toLowerCase())) {
                    results.add(album);
                }
            }
        } else if (searchType.equals(SearchType.GENRE)) {
            for (Album album : inventory) {
                if (album.getGenre().toLowerCase().contains(searchTerm.toLowerCase())) {
                    results.add(album);
                }
            }
        } else if (searchType.equals(SearchType.TYPE)) {
            for (Album album : inventory) {
                if (album.getType().name().equalsIgnoreCase(searchTerm)) {
                    results.add(album);
                }
            }
        }

        return results;
    }

    public double calculateDiscount(Album album, CustomerType customerType) {
        double discount = 0;

        if (customerType.equals(CustomerType.VIP)) {
            discount = album.getPrice() * 0.20;
        } else if (customerType.equals(CustomerType.PREMIUM)) {
            discount = album.getPrice() * 0.15;
        } else if (customerType.equals(CustomerType.REGULAR)) {
            discount = album.getPrice() * 0.05;
        }

        // Additional discounts
        if (album.getType().equals(MediaType.VINYL) && album.getReleaseDate().getYear() < 1980) {
            discount += album.getPrice() * 0.10;
        }

        if (album.getGenre().equalsIgnoreCase("Pop Punk") && customerType.equals(CustomerType.VIP)) {
            discount += album.getPrice() * 0.05;
        }

        return discount;
    }

    public void purchaseMusic(Customer customer, Album album) {
        if (validatePurchase(customer, album)) {
            double discount = calculateDiscount(album, customer.getType());
            double finalPrice = album.getPrice() - discount;

            System.out.println("Purchase: " + album.getFormattedName() + " by " + customer.getName());
            System.out.println("Original price: $" + album.getPrice());
            System.out.println("Discount: $" + discount);
            System.out.println("Final price: $" + finalPrice);

            album.decreaseStock();
            customer.addPurchase(album);

            for (Customer c : customers) {
                if (c.isInterestedIn(album.getGenre()) && !c.equals(customer)) {
                    System.out.println("Notifying " + c.getName() + " about popular " + album.getGenre() + " purchase");
                }
            }
        } else {
            System.out.println("Out of stock!");
        }
    }

    public boolean validatePurchase(Customer customer, Album album) {
        PurchaseValidator validator = buildValidationChain();
        return validator.validate(customer, album);
    }

    private PurchaseValidator buildValidationChain() {
        PurchaseValidator stock = new StockValidator();
        PurchaseValidator credit = new CreditValidator();
        PurchaseValidator age = new AgeRestrictionValidator();

        stock.setNext(credit);
        credit.setNext(age);

        return stock;
    }

    public List<Album> getInventory() {
        return inventory;
    }

}
