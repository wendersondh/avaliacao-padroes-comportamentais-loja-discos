package br.edu.ifpb.padroes.store.discount;

import br.edu.ifpb.padroes.customer.CustomerType;
import br.edu.ifpb.padroes.music.Album;

import java.util.List;

public class DiscountCalculator {
    private final List<DiscountStrategy> strategies;

    public DiscountCalculator() {
        this.strategies = List.of(
                new ClientTypeDiscountStrategy(),
                new VintageVinylDiscountStrategy(),
                new PopPunkVipDiscountStrategy()
        );
    }

    public double calculateDiscount(Album album, CustomerType customerType) {
        return strategies.stream()
                .mapToDouble(s -> s.calculate(album, customerType))
                .sum();
    }
}
