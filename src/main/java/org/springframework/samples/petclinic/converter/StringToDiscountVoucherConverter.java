package org.springframework.samples.petclinic.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.samples.petclinic.model.DiscountVoucher;
import org.springframework.samples.petclinic.service.DiscountVoucherService;
import org.springframework.stereotype.Component;

@Component
public class StringToDiscountVoucherConverter implements Converter<String, DiscountVoucher> {

    @Autowired
    private DiscountVoucherService discountVoucherService;

    public DiscountVoucher convert(String source) {
        try {
        	Integer id = Integer.valueOf(source);
            return discountVoucherService.find(id);
        } catch (Throwable ex) {
            return null;
        }
    }
}
