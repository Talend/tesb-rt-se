package ch.zurich.caseservice.impl;

import javax.xml.ws.Holder;

import org.junit.Test;

import ch.zurich.incurancecase.caseservice.AddCaseFault_Exception;

public class CaseServiceTest {

	@Test
	public void addCaseTest() {
		CaseServiceImpl caseServiceImpl = new CaseServiceImpl();
		try {
			caseServiceImpl.addCase("1", "a", "10,99", "contractor", null,
					new Holder<String>(), new Holder<Boolean>());
		} catch (AddCaseFault_Exception e) {
			e.printStackTrace();
		}
	}
}
