package model;

import java.util.ArrayList;
import java.util.List;

public class TopTermsStats {
	List<String> terms;
	List<Integer> freq;
	
	public TopTermsStats(List<String> terms, List<Integer> freq) {
		super();
		this.terms = terms;
		this.freq = freq;
	}
	public TopTermsStats() {
		this.terms = new ArrayList<String>();
		this.freq =  new ArrayList<Integer>();
	}
	public List<String> getTerms() {
		return terms;
	}
	public void setTerms(List<String> terms) {
		this.terms = terms;
	}
	public List<Integer> getFreq() {
		return freq;
	}
	public void setFreq(List<Integer> freq) {
		this.freq = freq;
	}
	
	
}
