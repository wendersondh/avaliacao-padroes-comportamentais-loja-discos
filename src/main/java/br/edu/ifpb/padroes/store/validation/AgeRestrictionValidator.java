package br.edu.ifpb.padroes.store.validation;

import br.edu.ifpb.padroes.customer.Customer;
import br.edu.ifpb.padroes.music.AgeRestriction;
import br.edu.ifpb.padroes.music.Album;

import java.time.LocalDate;

public class AgeRestrictionValidator extends AbstractPurchaseValidator {

    @Override
    public boolean validate(Customer customer, Album album) {

        boolean isRestricted = album.getAgeRestriction().equals(AgeRestriction.PARENTAL_ADVISORY);
        boolean isUnder18 = customer.getDateOfBirth().isAfter(LocalDate.now().minusYears(18));

        if (isRestricted && isUnder18) {
            System.out.println("Validation failed: Age restriction");
            return false;
        }

        return next(customer, album);
    }
}
