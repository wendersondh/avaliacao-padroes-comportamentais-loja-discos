package br.edu.ifpb.padroes.store.discount;

import br.edu.ifpb.padroes.customer.CustomerType;
import br.edu.ifpb.padroes.music.Album;

public interface DiscountStrategy {
    double calculate(Album album, CustomerType customerType);
}
