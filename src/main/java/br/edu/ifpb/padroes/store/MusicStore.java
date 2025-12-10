package br.edu.ifpb.padroes.store;

import br.edu.ifpb.padroes.customer.Customer;
import br.edu.ifpb.padroes.customer.CustomerType;
import br.edu.ifpb.padroes.music.AgeRestriction;
import br.edu.ifpb.padroes.music.Album;
import br.edu.ifpb.padroes.music.MediaType;
import br.edu.ifpb.padroes.store.discount.DiscountCalculator;
import br.edu.ifpb.padroes.store.validation.AgeRestrictionValidator;
import br.edu.ifpb.padroes.store.validation.CreditValidator;
import br.edu.ifpb.padroes.store.validation.PurchaseValidator;
import br.edu.ifpb.padroes.store.validation.StockValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Album> searchMusic(SearchType searchType, String term) {
        String lowerTerm = term.toLowerCase();

        return inventory.stream()
                .filter(album -> matchesSearch(album, searchType, lowerTerm))
                .collect(Collectors.toList());
    }

    private boolean matchesSearch(Album album, SearchType type, String term) {
        return switch (type) {
            case TITLE -> album.getTitle().toLowerCase().contains(term);
            case ARTIST -> album.getArtist().toLowerCase().contains(term);
            case GENRE -> album.getGenre().toLowerCase().contains(term);
            case TYPE -> album.getType().name().equalsIgnoreCase(term);
        };
    }

    public double calculateDiscount(Album album, CustomerType customerType) {
        DiscountCalculator calculator = new DiscountCalculator();
        return calculator.calculateDiscount(album, customerType);
    }

    public void purchaseMusic(Customer customer, Album album) {

        if (!validatePurchase(customer, album)) {
            System.out.println("Purchase denied.");
            return;
        }

        double discount = calculateDiscount(album, customer.getType());
        double finalPrice = album.getPrice() - discount;

        System.out.printf("""
                Purchase: %s by %s
                Original price: $%.2f
                Discount: $%.2f
                Final price: $%.2f
                
                """,
                album.getFormattedName(), customer.getName(),
                album.getPrice(), discount, finalPrice);

        album.decreaseStock();
        customer.addPurchase(album);

        notifyInterestedCustomers(customer, album);
    }

    private void notifyInterestedCustomers(Customer buyer, Album album) {
        customers.stream()
                .filter(c -> !c.equals(buyer))
                .filter(c -> c.isInterestedIn(album.getGenre()))
                .forEach(c -> System.out.println("Notifying " + c.getName() +
                        " about popular " + album.getGenre() + " purchase"));
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
