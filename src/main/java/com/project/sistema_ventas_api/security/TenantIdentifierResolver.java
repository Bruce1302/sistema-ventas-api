package com.project.sistema_ventas_api.security;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String>, HibernatePropertiesCustomizer {

    @Override
    public String resolveCurrentTenantIdentifier()
    {
        String tenantId = TenantContext.getCurrentTenant();
        // Si no hay tienda en el contexto (ej. durante el Login), devolvemos un valor neutro
        return (tenantId != null) ? tenantId : "SYSTEM";
    }

    @Override
    public boolean validateExistingCurrentSessions()
    {
        return true;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        // Le registramos este resolutor directamente al motor de Hibernate
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }


}
