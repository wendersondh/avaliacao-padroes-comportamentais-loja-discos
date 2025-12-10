package br.edu.ifpb.padroes.store.discount;

import br.edu.ifpb.padroes.customer.CustomerType;
import br.edu.ifpb.padroes.music.Album;

public class PopPunkVipDiscountStrategy implements DiscountStrategy{
    @Override
    public double calculate(Album album, CustomerType customerType) {
        if (album.getGenre().equalsIgnoreCase("Pop Punk")
                && customerType.equals(CustomerType.VIP)) {
            return album.getPrice() * 0.05;
        }
        return 0;
    }
}
