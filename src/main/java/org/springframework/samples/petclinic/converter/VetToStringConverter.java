package org.springframework.samples.petclinic.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.stereotype.Component;

@Component
public class VetToStringConverter implements Converter<Vet, String> {

    public String convert(Vet source) {
        try {
            return String.valueOf(source.getId());
        } catch (SecurityException ex) {
            return null;
        }
    }
}
