package org.springframework.samples.petclinic.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.stereotype.Component;

@Component
public class StringToOwnerConverter implements Converter<String, Owner> {

    @Autowired
    private OwnerService ownerService;

    public Owner convert(String source) {
        try {
        	Integer id = Integer.valueOf(source);
            return ownerService.findOwnerById(id);
        } catch (SecurityException ex) {
            return null;
        }
    }
}
