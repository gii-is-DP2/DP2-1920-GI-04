package org.springframework.samples.petclinic.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.samples.petclinic.model.DiscountVoucher;
import org.springframework.stereotype.Component;

@Component
public class DiscountVoucherToStringConverter implements Converter<DiscountVoucher, String> {

    public String convert(DiscountVoucher source) {
        try {
            return String.valueOf(source.getId());
        } catch (SecurityException ex) {
            return null;
        }
    }
}
