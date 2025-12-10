package br.edu.ifpb.padroes.store;

import br.edu.ifpb.padroes.customer.Customer;
import br.edu.ifpb.padroes.customer.CustomerType;
import br.edu.ifpb.padroes.music.AgeRestriction;
import br.edu.ifpb.padroes.music.Album;
import br.edu.ifpb.padroes.music.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MusicStoreTest {

    private MusicStore store;

    @BeforeEach
    void setUp() {
        store = new MusicStore();

        store.addMusic(new Album("The Wall", "Pink Floyd", MediaType.VINYL, 200.00,
                LocalDate.of(1979, Month.NOVEMBER, 30), AgeRestriction.GENERAL, "Rock", 5));

        store.addMusic(new Album("Thriller", "Michael Jackson", MediaType.CD, 150.00,
                LocalDate.of(1982, Month.NOVEMBER, 30), AgeRestriction.GENERAL, "Pop", 5));

        store.addMusic(new Album("Dookie", "Green Day", MediaType.CD, 100.00,
                LocalDate.of(1994, Month.FEBRUARY, 1), AgeRestriction.GENERAL, "Pop Punk", 5));
    }

    @Test
    @DisplayName("Should be possible to purchase a music album")
    void testSuccessfulPurchase() {
        Customer customer = new Customer("John Doe", new ArrayList<>(), new ArrayList<>(), 150.00, CustomerType.REGULAR, LocalDate.of(1990, 1, 1));
        Album album = new Album("Reflektor", "Arcade Fire", MediaType.VINYL, 150.00, LocalDate.of(2013, Month.SEPTEMBER, 9), AgeRestriction.GENERAL, "Alternative", 10);

        store.addMusic(album);
        store.addCustomer(customer);

        store.purchaseMusic(customer, album);

        assertEquals(9, album.getStock(), "Stock should decrease by 1 after a successful purchase");
        assertEquals(1, customer.getPurchaseCount(), "Customer purchase count should increase by 1");
        assertTrue(customer.getPurchases().contains(album), "Album should be added to customer's purchases");
    }

    @Test
    @DisplayName("Should not be possible to purchase due to out of stock")
    void testPurchaseFailsDueToOutOfStock() {
        Customer customer = new Customer("Jane Doe", new ArrayList<>(), new ArrayList<>(), 100.00, CustomerType.VIP, LocalDate.of(1985, 2, 15));
        Album album = new Album("Mellon Collie and the Infinite Sadness", "Smashing Pumpkings", MediaType.VINYL, 1060.00, LocalDate.of(1995, Month.OCTOBER, 24), AgeRestriction.GENERAL, "Rock", 0);

        store.addMusic(album);
        store.addCustomer(customer);

        store.purchaseMusic(customer, album);

        assertEquals(0, album.getStock(), "Stock should remain unchanged as the album is out of stock");
        assertEquals(0, customer.getPurchaseCount(), "Customer purchase count should remain 0 as purchase did not occur");
        assertFalse(customer.getPurchases().contains(album), "Album should not be added to customer's purchases");
    }

    @Test
    @DisplayName("Should not be possible to purchase due to insufficient credit")
    void testPurchaseFailsDueToInsufficientCredit() {
        Customer customer = new Customer("Emma Smith", new ArrayList<>(), new ArrayList<>(), 10.00, CustomerType.REGULAR, LocalDate.of(1992, 11, 5));
        Album album = new Album("The Tortured Poets Department", "Taylor Swift", MediaType.CD, 90.00, LocalDate.of(2024, Month.APRIL, 19), AgeRestriction.GENERAL, "Pop", 5);

        store.addMusic(album);
        store.addCustomer(customer);

        store.purchaseMusic(customer, album);

        assertEquals(5, album.getStock(), "Stock should remain unchanged as the purchase did not occur");
        assertEquals(0, customer.getPurchaseCount(), "Customer purchase count should remain 0 as no purchase was made");
        assertFalse(customer.getPurchases().contains(album), "Album should not be added to customer's purchases");
    }

    @Test
    @DisplayName("Should not be possible to purchase due to age restriction")
    void testPurchaseFailsDueToAgeRestriction() {
        Customer customer = new Customer("Young Joe", new ArrayList<>(), new ArrayList<>(), 50.00, CustomerType.REGULAR, LocalDate.of(2010, 8, 20));
        Album album = new Album("Enema of the State", "Blink 182", MediaType.CD, 20.00, LocalDate.of(2009, 4, 15), AgeRestriction.PARENTAL_ADVISORY, "JAZZ", 3);

        store.addMusic(album);
        store.addCustomer(customer);

        store.purchaseMusic(customer, album);

        assertEquals(3, album.getStock(), "Stock should remain unchanged as the purchase did not occur due to age restriction");
        assertEquals(0, customer.getPurchaseCount(), "Customer purchase count should remain 0 as no purchase was made");
        assertFalse(customer.getPurchases().contains(album), "Album should not be added to customer's purchases");
    }

    @Test
    @DisplayName("Should be possible to purchase due to VIP discount")
    void testPurchaseAppliesVIPDiscount() {
        Customer customer = new Customer("VIP Johnny", new ArrayList<>(), new ArrayList<>(), 100.00, CustomerType.VIP, LocalDate.of(1980, 3, 25));
        Album album = new Album("Man on the Moon: The End of Days", "Kid Cudi", MediaType.CD, 50.00, LocalDate.of(1999, Month.JUNE, 1), AgeRestriction.PARENTAL_ADVISORY, "Pop Punk", 20);

        store.addMusic(album);
        store.addCustomer(customer);

        store.purchaseMusic(customer, album);

        double expectedDiscount = 50.00 * 0.25; // VIP discount (20%) + Pop Punk VIP bonus (5%)

        assertEquals(19, album.getStock(), "Stock should decrease by 1 after successful purchase");
        assertEquals(expectedDiscount, store.calculateDiscount(album, customer.getType()), 0.01, "Discount should match the calculated VIP discount");
        assertTrue(customer.getPurchases().contains(album), "Album should be added to customer's purchases");
    }

    @Test
    @DisplayName("Should be possible to purchase due to Premium discount")
    void testPurchaseAppliesOldVinylDiscount() {
        MusicStore store = new MusicStore();
        Customer customer = new Customer("Premium Carl", new ArrayList<>(), new ArrayList<>(), 120.00, CustomerType.PREMIUM, LocalDate.of(1970, 7, 10));
        Album album = new Album("The Rise and Fall of Ziggy Stardust and the Spiders from Mars", "David Bowie", MediaType.VINYL, 80.00, LocalDate.of(1972, Month.JUNE, 16), AgeRestriction.GENERAL, "Pop", 4);

        store.addMusic(album);
        store.addCustomer(customer);

        store.purchaseMusic(customer, album);

        double expectedDiscount = 80.00 * 0.25; // Premium discount (15%) + Vinyl bonus (10%)

        assertEquals(3, album.getStock(), "Stock should decrease by 1 after successful purchase");
        assertEquals(expectedDiscount, store.calculateDiscount(album, customer.getType()), 0.01, "Discount should match the calculated discount");
        assertTrue(customer.getPurchases().contains(album), "Album should be added to customer's purchases");
    }

    @Test
    @DisplayName("Should find album by title")
    void testSearchByTitle() {
        List<Album> results = store.searchMusic(SearchType.TITLE, "the wall");

        assertEquals(1, results.size());
        assertEquals("The Wall", results.get(0).getTitle());
    }

    @Test
    @DisplayName("Should find albums by artist")
    void testSearchByArtist() {
        List<Album> results = store.searchMusic(SearchType.ARTIST, "michael jackson");

        assertEquals(1, results.size());
        assertEquals("Thriller", results.get(0).getTitle());
    }

    @Test
    @DisplayName("Should find albums by genre")
    void testSearchByGenre() {
        List<Album> results = store.searchMusic(SearchType.GENRE, "pop punk");

        assertEquals(1, results.size());
        assertEquals("Dookie", results.get(0).getTitle());
    }

    @Test
    @DisplayName("Should find albums by media type")
    void testSearchByType() {
        List<Album> results = store.searchMusic(SearchType.TYPE, "VINYL");

        assertEquals(1, results.size());
        assertEquals("The Wall", results.get(0).getTitle());
    }

}