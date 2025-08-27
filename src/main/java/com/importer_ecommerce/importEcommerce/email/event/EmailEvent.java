package com.importer_ecommerce.importEcommerce.email.event;

import lombok.Data;

@Data
public abstract class EmailEvent {
    private String to;
    private String subject;
    private String templateName;
    private Object templateData;
    
    public EmailEvent(String to, String subject, String templateName, Object templateData) {
        this.to = to;
        this.subject = subject;
        this.templateName = templateName;
        this.templateData = templateData;
    }
}

