
public class BoxScoreModel {

	String teamName;
	
	int scoreQ1;
	int scoreQ2;
	int scoreQ3;
	int scoreQ4;
	double meanProjectedQ4;
	double varianceFromMean;
	
	
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public int getScoreQ1() {
		return scoreQ1;
	}
	public void setScoreQ1(int scoreQ1) {
		this.scoreQ1 = scoreQ1;
	}
	public int getScoreQ2() {
		return scoreQ2;
	}
	public void setScoreQ2(int scoreQ2) {
		this.scoreQ2 = scoreQ2;
	}
	public int getScoreQ3() {
		return scoreQ3;
	}
	public void setScoreQ3(int scoreQ3) {
		this.scoreQ3 = scoreQ3;
	}
	public int getScoreQ4() {
		return scoreQ4;
	}
	public void setScoreQ4(int scoreQ4) {
		this.scoreQ4 = scoreQ4;
	}
	public double getMeanProjectedQ4() {
		return meanProjectedQ4;
	}
	public void setMeanProjectedQ4(int meanProjectedQ4) {
		this.meanProjectedQ4 = meanProjectedQ4;
	}
	public double getVarianceFromMean() {
		return varianceFromMean;
	}
	public void setVarianceFromMean(int varianceFromMean) {
		this.varianceFromMean = varianceFromMean;
	}
	
}