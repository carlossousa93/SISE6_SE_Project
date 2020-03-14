package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class TransferMethodTest {
//	private static final String ADDRESS = "Ave.";
//	private static final String PHONE_NUMBER = "987654321";
//	private static final String NIF = "123456789";
//	private static final String LAST_NAME = "Silva";
//	private static final String FIRST_NAME = "António";

	private Sibs sibsMock;
	private Bank sourceBank;
	private Bank targetBank;
	private Services services;

	@Before
	public void setUp() throws BankException, AccountException, ClientException {
		this.services = new Services();
		this.sourceBank = new Bank("CGD");
		this.targetBank = new Bank("BPI");
	}

	@Test
	public void successStandardTransfer() throws SibsException, AccountException, OperationException {
		Services mockService = mock(Services.class);
		this.sibsMock = new Sibs(100, mockService);
		String sourceIban = "AAA";
		String targetIban = "BBB";
		when(mockService.accountExistance(sourceIban)).thenReturn(true);
		when(mockService.accountExistance(targetIban)).thenReturn(true);
		when(mockService.sameBanks(sourceIban, targetIban)).thenReturn(true);
		this.sibsMock.transfer(sourceIban, targetIban, 100);
		verify(mockService).withdraw(sourceIban, 100);
		verify(mockService).deposit(targetIban, 100);

	}

	@Test
	public void successComissionTransfer() throws SibsException, AccountException, OperationException {
		Services mockService = mock(Services.class);
		this.sibsMock = new Sibs(100, mockService);
		String sourceIban = "AAA";
		String targetIban = "BBB";
		when(mockService.accountExistance(sourceIban)).thenReturn(true);
		when(mockService.accountExistance(targetIban)).thenReturn(true);
		when(mockService.sameBanks(sourceIban, targetIban)).thenReturn(false);
		this.sibsMock.transfer(sourceIban, targetIban, 100);
		verify(mockService, times(1)).withdraw(sourceIban, 106);
		verify(mockService, times(1)).deposit(targetIban, 100);
	}

	@Test
	public void failedTransfer() throws SibsException, AccountException, OperationException {
		Services mockService = mock(Services.class);
		this.sibsMock = new Sibs(100, mockService);
		String sourceIban = "AAA";
		String targetIban = "BBB";
		when(mockService.accountExistance(sourceIban)).thenReturn(true);
		when(mockService.accountExistance(targetIban)).thenReturn(false);
		try {
			this.sibsMock.transfer(sourceIban, targetIban, 100);
			fail();
		} catch (Exception e) {
			verify(mockService, never()).withdraw(sourceIban, 100);
			verify(mockService, never()).deposit(targetIban, 100);
		}
	}

	@Test
	public void failedTransferInactiveTargetAccount() throws SibsException, AccountException, OperationException {
		Services mockService = mock(Services.class);
		this.sibsMock = new Sibs(100, mockService);
		String sourceIban = "AAA";
		String targetIban = "BBB";
		when(mockService.accountExistance(sourceIban)).thenReturn(true);
		when(mockService.accountExistance(targetIban)).thenReturn(true);
		when(mockService.accountStatus(sourceIban)).thenReturn(true);
		when(mockService.accountStatus(targetIban)).thenReturn(false);
		try {
			this.sibsMock.transfer(sourceIban, targetIban, 100);
			fail();
		} catch (Exception e) {
			verify(mockService, never()).withdraw(sourceIban, 100);
			verify(mockService, never()).deposit(targetIban, 100);
		}
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
		this.sibsMock = null;
	}

}
