package com.franchise_network.franchise.domain.exceptions;



import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class BusinessException extends ProcessorException {

    private final String paramValue;

    public BusinessException(TechnicalMessage technicalMessage, String paramValue) {
        super(technicalMessage.getMessage(), technicalMessage);
        this.paramValue = paramValue;
    }

    public BusinessException(TechnicalMessage technicalMessage) {
        this(technicalMessage, technicalMessage.getParam());
    }



}
