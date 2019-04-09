package se.jsquad.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CLIENT")
@NamedQuery(name = Client.PERSON_IDENTIFICATION, query = "SELECT c FROM Client c WHERE c.person.personIdentification " +
        "= :" + Client.PERSON_IDENTIFICSTION_PARAM)
public class Client {
    public static final String PERSON_IDENTIFICATION = "PERSON_IDENTIFICATION";
    public static final String PERSON_IDENTIFICSTION_PARAM = "PERSON_IDENTIFICATION_PARAM";

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @OneToOne(mappedBy = "client",
            cascade = {CascadeType.ALL})
    private Person person;

    public Long getId() {
        return id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
