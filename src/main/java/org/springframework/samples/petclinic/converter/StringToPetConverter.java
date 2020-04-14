package org.springframework.samples.petclinic.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.stereotype.Component;

@Component
public class StringToPetConverter implements Converter<String, Pet> {

    @Autowired
    private PetService petService;

    public Pet convert(String source) {
        try {
        	Integer id = Integer.valueOf(source);
            return petService.findPetById(id);
        } catch (SecurityException ex) {
            return null;
        }
    }
}
