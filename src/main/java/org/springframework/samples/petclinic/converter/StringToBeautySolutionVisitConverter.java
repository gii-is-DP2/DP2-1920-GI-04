package org.springframework.samples.petclinic.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.samples.petclinic.model.BeautySolutionVisit;
import org.springframework.samples.petclinic.service.BeautySolutionVisitService;
import org.springframework.stereotype.Component;

@Component
public class StringToBeautySolutionVisitConverter implements Converter<String, BeautySolutionVisit> {

    @Autowired
    private BeautySolutionVisitService beautySolutionVisitService;

    public BeautySolutionVisit convert(String source) {
        try {
        	Integer id = Integer.valueOf(source);
            return beautySolutionVisitService.find(id);
        } catch (Throwable ex) {
            return null;
        }
    }
}
