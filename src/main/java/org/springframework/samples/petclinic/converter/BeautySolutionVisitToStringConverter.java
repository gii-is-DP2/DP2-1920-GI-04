package org.springframework.samples.petclinic.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.samples.petclinic.model.BeautySolutionVisit;
import org.springframework.stereotype.Component;

@Component
public class BeautySolutionVisitToStringConverter implements Converter<BeautySolutionVisit, String> {

    public String convert(BeautySolutionVisit source) {
        try {
            return String.valueOf(source.getId());
        } catch (SecurityException ex) {
            return null;
        }
    }
}
