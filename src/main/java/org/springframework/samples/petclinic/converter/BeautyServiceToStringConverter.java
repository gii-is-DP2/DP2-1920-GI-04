package org.springframework.samples.petclinic.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.samples.petclinic.model.BeautyService;
import org.springframework.stereotype.Component;

@Component
public class BeautyServiceToStringConverter implements Converter<BeautyService, String> {

    public String convert(BeautyService source) {
        try {
            return String.valueOf(source.getId());
        } catch (SecurityException ex) {
            return null;
        }
    }
}
