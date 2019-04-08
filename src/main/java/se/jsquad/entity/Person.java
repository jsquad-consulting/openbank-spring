package se.jsquad.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PERSON")
public class Person {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "FIRSTNAME")
    private String firstName;

    @Column(name = "PERSONIDENTIFICATION")
    private String personIdentification;

    @OneToOne
    @JoinColumn(name = "CLIENT_FK")
    private Client client;

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPersonIdentification() {
        return personIdentification;
    }

    public void setPersonIdentification(String personIdentification) {
        this.personIdentification = personIdentification;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
