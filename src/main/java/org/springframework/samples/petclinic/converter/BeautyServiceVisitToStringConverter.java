package org.springframework.samples.petclinic.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.samples.petclinic.model.BeautyServiceVisit;
import org.springframework.stereotype.Component;

@Component
public class BeautyServiceVisitToStringConverter implements Converter<BeautyServiceVisit, String> {

    public String convert(BeautyServiceVisit source) {
        try {
            return String.valueOf(source.getId());
        } catch (SecurityException ex) {
            return null;
        }
    }
}
