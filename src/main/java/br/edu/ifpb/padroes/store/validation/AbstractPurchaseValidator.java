package br.edu.ifpb.padroes.store.validation;

import br.edu.ifpb.padroes.customer.Customer;
import br.edu.ifpb.padroes.music.Album;

public abstract class AbstractPurchaseValidator implements PurchaseValidator {
    protected PurchaseValidator next;

    @Override
    public void setNext(PurchaseValidator next) {
        this.next = next;
    }

    protected boolean next(Customer customer, Album album) {
        if (next == null) return true;
        return next.validate(customer, album);
    }
}
