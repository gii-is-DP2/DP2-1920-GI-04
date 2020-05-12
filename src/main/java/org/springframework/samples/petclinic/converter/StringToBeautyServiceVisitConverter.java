package org.springframework.samples.petclinic.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.samples.petclinic.model.BeautyServiceVisit;
import org.springframework.samples.petclinic.service.BeautyServiceVisitService;
import org.springframework.stereotype.Component;

@Component
public class StringToBeautyServiceVisitConverter implements Converter<String, BeautyServiceVisit> {

    @Autowired
    private BeautyServiceVisitService beautyServiceVisitService;

    public BeautyServiceVisit convert(String source) {
        try {
        	Integer id = Integer.valueOf(source);
            return beautyServiceVisitService.find(id);
        } catch (Throwable ex) {
            return null;
        }
    }
}
