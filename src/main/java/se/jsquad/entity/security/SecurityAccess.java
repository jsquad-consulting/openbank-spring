package se.jsquad.entity.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SECURITY")
public class SecurityAccess {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "SECURITY_CODE")
    private String securityCode;

    public Long getId() {
        return id;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }
}
