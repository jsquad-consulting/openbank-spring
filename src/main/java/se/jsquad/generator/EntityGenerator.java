package se.jsquad.generator;

import se.jsquad.entity.Client;

import java.util.Set;

public interface EntityGenerator {
    Set<Client> generateClientSet();
}
