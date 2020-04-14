package org.springframework.samples.petclinic.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.samples.petclinic.model.BeautyService;
import org.springframework.samples.petclinic.service.BeautyServiceService;
import org.springframework.stereotype.Component;

@Component
public class StringToBeautyServiceConverter implements Converter<String, BeautyService> {

    @Autowired
    private BeautyServiceService beautyServiceService;

    public BeautyService convert(String source) {
        try {
        	Integer id = Integer.valueOf(source);
            return beautyServiceService.find(id);
        } catch (SecurityException ex) {
            return null;
        }
    }
}
