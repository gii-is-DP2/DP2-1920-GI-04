package org.springframework.samples.petclinic.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.stereotype.Component;

@Component
public class PetToStringConverter implements Converter<Pet, String> {

    public String convert(Pet source) {
        try {
            return String.valueOf(source.getId());
        } catch (SecurityException ex) {
            return null;
        }
    }
}
