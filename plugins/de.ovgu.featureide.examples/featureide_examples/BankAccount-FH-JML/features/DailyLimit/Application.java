class Application {
	
	/*@ \consecutive_contract
	 @
	 @ ensures account.withdraw == 0;
	 @*/
	void nextDay() {
		original();
		account.withdraw = 0;
	}

}