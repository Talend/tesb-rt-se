package ch.zurich.caseservice.impl;

import javax.xml.ws.Holder;

import org.junit.Test;

import ch.zurich.incurancecase.caseservice.AddCaseFault_Exception;

public class CaseServiceTest {

	@Test
	public void addCaseTest() throws AddCaseFault_Exception {
		CaseServiceImpl caseServiceImpl = new CaseServiceImpl();
		caseServiceImpl.addCase("1", "a", "10,99", "contractor", null,
			new Holder<String>(), new Holder<Boolean>());
	}
}
