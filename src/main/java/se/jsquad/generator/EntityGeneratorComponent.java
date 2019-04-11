package se.jsquad.generator;

import se.jsquad.entity.Client;

import java.util.List;

public interface EntityGeneratorComponent {
    List<Client> generateClientList();
}
