package pt.ulisboa.tecnico.learnjava.bank.services;

import pt.ulisboa.tecnico.learnjava.bank.domain.Account;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;

public class Services {
	public void deposit(String iban, int amount) throws AccountException {
		Account account = getAccountByIban(iban);

		account.deposit(amount);
	}

	public void withdraw(String iban, int amount) throws AccountException {
		Account account = getAccountByIban(iban);

		account.withdraw(amount);
	}

	public Account getAccountByIban(String iban) {
		String code = iban.substring(0, 3);
		String accountId = iban.substring(3);

		Bank bank = Bank.getBankByCode(code);
		Account account = bank.getAccountByAccountId(accountId);

		return account;
	}

	public boolean accountExistance(String iban) {
		return (getAccountByIban(iban) != null);
	}

	public boolean accountStatus(String iban) {
		Account account = getAccountByIban(iban);
		return account.isInactive();
	}

	public boolean sameBanks(String sourceIban, String targetIban) {
		String sourceCode = sourceIban.substring(0, 3);
		String targetCode = targetIban.substring(0, 3);
		return (sourceCode.equals(targetCode));

	}

}
