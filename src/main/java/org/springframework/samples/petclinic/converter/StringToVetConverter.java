package org.springframework.samples.petclinic.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.service.VetService;
import org.springframework.stereotype.Component;

@Component
public class StringToVetConverter implements Converter<String, Vet> {

    @Autowired
    private VetService vetService;

    public Vet convert(String source) {
        try {
        	Integer id = Integer.valueOf(source);
            return vetService.find(id);
        } catch (SecurityException ex) {
            return null;
        }
    }
}
