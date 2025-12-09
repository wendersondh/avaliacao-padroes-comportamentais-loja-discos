package br.edu.ifpb.padroes.store.validation;

import br.edu.ifpb.padroes.customer.Customer;
import br.edu.ifpb.padroes.music.Album;

public interface PurchaseValidator {
    void setNext(PurchaseValidator next);
    boolean validate(Customer customer, Album album);
}
