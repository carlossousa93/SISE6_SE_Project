package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Operation;
import pt.ulisboa.tecnico.learnjava.sibs.domain.TransferOperation;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class TransferOperationProcessStateTest {
	private static final String ADDRESS = "Ave.";
	private static final String PHONE_NUMBER = "987654321";
	private static final String NIF = "123456789";
	private static final String LAST_NAME = "Silva";
	private static final String FIRST_NAME = "Ant√≥nio";

	private Bank sourceBank;
	private Bank targetBank;
	private Client sourceClient;
	private Client targetClient;
	private Services services;
	
	@Before
	public void setUp() throws BankException, ClientException {
		this.services = new Services();
		this.sourceBank = new Bank("CGD");
		this.targetBank = new Bank("BPI");
		this.sourceClient = new Client(this.sourceBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 33);
		this.targetClient = new Client(this.targetBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 22);
	}
	
	@Test
	public void processStateWithdraw() throws BankException, AccountException, ClientException, OperationException {
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
		
		TransferOperation operation = new TransferOperation(sourceIban, targetIban, 100);
		
		assertEquals(Operation.OPERATION_TRANSFER, operation.getType());
		assertEquals("Registered", operation.getState());
		
		operation.process(sourceIban, targetIban, operation.getValue());
		assertEquals(900, this.services.getAccountByIban(sourceIban).getBalance());
		assertEquals("Withdrawn", operation.getState());
	}
	
	@Test
	public void processStateWithdrawToDeposited() throws BankException, AccountException, ClientException, OperationException { // different banks
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
		
		TransferOperation operation = new TransferOperation(sourceIban, targetIban, 100);
		operation.process(sourceIban, targetIban, operation.getValue()); // registered to withdrawn
		operation.process(sourceIban, targetIban, operation.getValue()); // withdrawn to deposited
		assertEquals(1100,this.services.getAccountByIban(targetIban).getBalance());
		assertEquals("Deposited",operation.getState());
		
		int comission = operation.commission(); // bancos diferentes, exige retirada de comiss„o do sourceIban
		assertEquals(900-comission,this.services.getAccountByIban(sourceIban).getBalance());
	}
	
	@Test
	public void processStateDepositedToComplete() throws BankException, AccountException, ClientException, OperationException{
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
		
		TransferOperation operation = new TransferOperation(sourceIban, targetIban, 100);
		operation.process(sourceIban, targetIban, operation.getValue()); // registered to withdrawn
		operation.process(sourceIban, targetIban, operation.getValue()); // withdrawn to deposited
		operation.process(sourceIban, targetIban, operation.getValue()); // deposited to complete
		assertEquals("Completed",operation.getState());	
	}
	
	@Test
	public void processStateWithdrawnToComplete() throws ClientException, BankException, AccountException, OperationException {
		Client targetClientSameBank = new Client(this.sourceBank,"Cristiano","Ronaldo","123456711","962078576","Avenida de Turim",35);
		
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, targetClientSameBank, 1000, 0);
		
		TransferOperation operation = new TransferOperation(sourceIban, targetIban, 100);
		operation.process(sourceIban, targetIban, operation.getValue()); // registered to withdrawn
		operation.process(sourceIban, targetIban, operation.getValue()); // withdrawn to Completed
		assertEquals(900, this.services.getAccountByIban(sourceIban).getBalance());
		assertEquals(1100,this.services.getAccountByIban(targetIban).getBalance());
		assertEquals("Completed",operation.getState());	
	}
	
	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
