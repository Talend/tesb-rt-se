package service;

import javax.jws.WebService;

@WebService
public interface DoubleItPortType {
	public int doubleIt(int numberToDouble);
}
