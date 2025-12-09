package br.edu.ifpb.padroes.store.validation;

import br.edu.ifpb.padroes.customer.Customer;
import br.edu.ifpb.padroes.music.Album;

public class CreditValidator extends AbstractPurchaseValidator {

    @Override
    public boolean validate(Customer customer, Album album) {
        if (customer.getCredit() < album.getPrice()) {
            System.out.println("Validation failed: Insufficient credit");
            return false;
        }
        return next(customer, album);
    }
}
