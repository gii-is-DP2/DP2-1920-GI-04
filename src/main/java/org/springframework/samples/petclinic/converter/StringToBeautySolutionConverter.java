package org.springframework.samples.petclinic.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.samples.petclinic.model.BeautySolution;
import org.springframework.samples.petclinic.service.BeautySolutionService;
import org.springframework.stereotype.Component;

@Component
public class StringToBeautySolutionConverter implements Converter<String, BeautySolution> {

    @Autowired
    private BeautySolutionService beautySolutionService;

    public BeautySolution convert(String source) {
        try {
        	Integer id = Integer.valueOf(source);
            return beautySolutionService.find(id);
        } catch (SecurityException ex) {
            return null;
        }
    }
}
