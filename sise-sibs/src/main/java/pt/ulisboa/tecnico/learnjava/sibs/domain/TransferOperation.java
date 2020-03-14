package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class TransferOperation extends Operation {
	private final String sourceIban;
	private final String targetIban;
	private String state; // nova variavel para registrar o state
	Services services; // chamar classe Services

	public TransferOperation(String sourceIban, String targetIban, int value) throws OperationException {
		super(Operation.OPERATION_TRANSFER, value);

		if (invalidString(sourceIban) || invalidString(targetIban)) {
			throw new OperationException();
		}
		
		this.state = "Registered"; // acrescentado o state dentro do construtor
		this.sourceIban = sourceIban;
		this.targetIban = targetIban;
		this.services = new Services(); // iniciar services para poder chamar o metodo withdraw dos services
	}

	private boolean invalidString(String name) {
		return name == null || name.length() == 0;
	}

	@Override
	public int commission() {
		return (int) Math.round(super.commission() + getValue() * 0.05);
	}

	public String getSourceIban() {
		return this.sourceIban;
	}

	public String getTargetIban() {
		return this.targetIban;
	}
	
	public String getState() {
		return this.state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	
	// Acrescentado o metodo process
	public void process(String sourceIban, String targetIban, int value) throws AccountException {
		
		if(this.state.equals("Registered")) {
			this.services.withdraw(sourceIban, value);
			setState("Withdrawn");
		}
		else if(this.state.equals("Withdrawn")) {
			
			if(this.services.sameBanks(sourceIban, targetIban)) {
				this.services.deposit(targetIban, value);
				setState("Completed");
			}else{
				int comission = commission();
				this.services.withdraw(sourceIban, comission);
				this.services.deposit(targetIban, value);
				setState("Deposited");
			}
			
		}else {
			setState("Completed");
		}
	}
}
