package br.edu.ifpb.padroes.store.discount;

import br.edu.ifpb.padroes.customer.CustomerType;
import br.edu.ifpb.padroes.music.Album;

public class ClientTypeDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculate(Album album, CustomerType customerType) {
        return switch (customerType) {
            case VIP -> album.getPrice() * 0.20;
            case PREMIUM -> album.getPrice() * 0.15;
            case REGULAR -> album.getPrice() * 0.05;
            default -> 0;
        };
    }
}
