package se.jsquad.business;

public interface StartupOpenBankService {
	void initiateDatabase();

	void closeDatabase();

	void refreshJpaCache();
}
