package org.springframework.samples.petclinic.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.samples.petclinic.model.BeautySolution;
import org.springframework.stereotype.Component;

@Component
public class BeautySolutionToStringConverter implements Converter<BeautySolution, String> {

    public String convert(BeautySolution source) {
        try {
            return String.valueOf(source.getId());
        } catch (SecurityException ex) {
            return null;
        }
    }
}
