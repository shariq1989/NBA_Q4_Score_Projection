import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.jaunt.*;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class boxScoreFetcher {

	private static int avgVariance = 0;
	private static List<BoxScoreModel> scores = new ArrayList<BoxScoreModel>();
	private static DescriptiveStatistics stats = new DescriptiveStatistics();

	// fetch data from basketball-reference
	public static Elements getData(int month, int date, int year) {

		UserAgent userAgent = new UserAgent();

		// access page
		try {
			userAgent.visit("http://www.basketball-reference.com/boxscores/index.cgi?month=" + month + "&day=" + date + "&year=" + year);
		} catch (ResponseException e) {
			System.out.println(e.getMessage());
		}

		Elements elements = new Elements();
		// find tables containing box scores
		elements = userAgent.doc.findEach("<table class='medium_text'>").findEach("<table class='wide_table no_highlight'>").findEach("<td>");
		return elements;
	}

	// convert raw data to comma separated team scores
	public static void parseData() throws ParseException {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = formatter.parse("2015-10-27");
		Date endDate = formatter.parse("2016-01-21");

		LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {

			System.out.println("Processing scores for: " + date.getMonthValue() + "-" + date.getDayOfMonth() + "-" + date.getYear());
			Elements resultSet = getData(date.getMonthValue(), date.getDayOfMonth(), date.getYear());
			List<String> parsedList = new ArrayList<String>();

			for (Element element : resultSet) {
				// remove unneeded rows
				if (element.innerText().equals("&nbsp;") || element.innerText().equals("1") || element.innerText().equals("2") || element.innerText().equals("3") || element.innerText().equals("4")
						|| element.innerText().equals("T")) {
				} else {
					// add meaningful data to a String list
					parsedList.add(element.innerText());
				}
			}

			// booleans for state tracking
			boolean teamFound = false;
			boolean q1Found = false;
			boolean q2Found = false;
			boolean q3Found = false;

			BoxScoreModel newTeam = new BoxScoreModel();

			String[] nbaTeams = { "Hawks", "Celtics", "Bobcats", "Bulls", "Cavaliers", "Mavericks", "Nuggets", "Pistons", "Warriors", "Rockets", "Pacers", "Clippers", "Lakers", "Grizzlies", "Heat",
					"Bucks", "Timberwolves", "Nets", "Hornets", "Knicks", "Thunder", "Magic", "Sixers", "Suns", "Trail Blazers", "Kings", "Spurs", "Raptors", "Jazz", "Wizards" };

			// loop that filters and organizes scores
			for (String element : parsedList) {
				if (Arrays.asList(nbaTeams).contains(element)) {
					teamFound = true;
					newTeam.setTeamName(element);
				} else if (teamFound == true && q1Found == false) {
					newTeam.setScoreQ1(Integer.parseInt(element));
					q1Found = true;
				} else if (q1Found == true && q2Found == false) {
					newTeam.setScoreQ2(Integer.parseInt(element));
					q2Found = true;
				} else if (q2Found == true && q3Found == false) {
					newTeam.setScoreQ3(Integer.parseInt(element));
					q3Found = true;
				} else if (q3Found == true) {
					newTeam.setScoreQ4(Integer.parseInt(element));
					teamFound = false;
					q1Found = false;
					q2Found = false;
					q3Found = false;
					scores.add(newTeam);
					newTeam = new BoxScoreModel();
				}
			}
		}
		// Fourth quarter scores are forecasted here
		projectQ4();
		// printData();
		// bulls celtics
		takeRequest(23, 26, 33, 34, 30, 26);
		// jazz nets
		takeRequest(22, 26, 37, 23, 17, 17);
		// clippers knicks
		takeRequest(31, 24, 31, 24, 21, 19);

	}

	// Solely used for printing to console
	public static void printData() {
		for (BoxScoreModel score : scores) {
			System.out.println(score.teamName);
			System.out.println(score.scoreQ1 + ", " + score.scoreQ2 + ", " + score.scoreQ3 + ", " + score.scoreQ4 + ", Projected Q4: " + score.meanProjectedQ4 + ", Variance: "
					+ score.varianceFromMean);
		}
		System.out.println("Standard Deviation " + stats.getStandardDeviation());
		System.out.println("Max " + stats.getMin());
		System.out.println("3 Standard Deviation " + stats.getPercentile(.2));
		System.out.println("2 Standard Deviation " + stats.getPercentile(2.3));
		System.out.println("1 Standard Deviation " + stats.getPercentile(15.9));
		System.out.println("Mean " + stats.getMean());
		System.out.println("1 Standard Deviation " + stats.getPercentile(84.1));
		System.out.println("2 Standard Deviation " + stats.getPercentile(97.7));
		System.out.println("3 Standard Deviation " + stats.getPercentile(99.8));
		System.out.println("Max " + stats.getMax());
		// System.out.println(Arrays.toString(stats.getSortedValues()));

		// for graphing data
		/**
		 * List<Integer> intList = new ArrayList<Integer>(); for (int i = 0; i <
		 * stats.getSortedValues().length; i++) { intList.add((int)
		 * stats.getSortedValues()[i]); }
		 * 
		 * int oldVal = 0; for (int val : intList) { int frequency =
		 * Collections.frequency(intList, val); if (oldVal != val) {
		 * System.out.println(frequency); } oldVal = val; }
		 **/
	}

	public static void projectQ4() {
		for (BoxScoreModel score : scores) {
			score.meanProjectedQ4 = (score.scoreQ1 + score.scoreQ2 + score.scoreQ3) / 3;
			score.varianceFromMean = (score.meanProjectedQ4 - score.scoreQ4);
			stats.addValue(score.varianceFromMean);
		}
	}

	public static void takeRequest(int t1Q1, int t1Q2, int t1Q3, int t2Q1, int t2Q2, int t2Q3) {
		BoxScoreModel scoreRequestT1 = new BoxScoreModel();
		BoxScoreModel scoreRequestT2 = new BoxScoreModel();

		scoreRequestT1.scoreQ1 = t1Q1;
		scoreRequestT1.scoreQ2 = t1Q2;
		scoreRequestT1.scoreQ3 = t1Q3;
		scoreRequestT1.meanProjectedQ4 = (scoreRequestT1.scoreQ1 + scoreRequestT1.scoreQ2 + scoreRequestT1.scoreQ3) / 3;
		System.out.println("Mean Projected T1: " + scoreRequestT1.meanProjectedQ4);

		scoreRequestT2.scoreQ1 = t2Q1;
		scoreRequestT2.scoreQ2 = t2Q2;
		scoreRequestT2.scoreQ3 = t2Q3;
		scoreRequestT2.meanProjectedQ4 = (scoreRequestT2.scoreQ1 + scoreRequestT2.scoreQ2 + scoreRequestT2.scoreQ3) / 3;
		System.out.println("Mean Projected T2: " + scoreRequestT2.meanProjectedQ4);

		double projectedTotal = (scoreRequestT1.scoreQ1 + scoreRequestT1.scoreQ2 + scoreRequestT1.scoreQ3 + scoreRequestT1.meanProjectedQ4)
				+ (scoreRequestT2.scoreQ1 + scoreRequestT2.scoreQ2 + scoreRequestT2.scoreQ3 + scoreRequestT2.meanProjectedQ4);

		System.out.println("Projected total: " + projectedTotal);
		System.out.println("Mean Adusted Projection " + (projectedTotal + stats.getMean()));
		System.out.println("1 SV Range: " + (stats.getPercentile(15.9) + projectedTotal) + " - " + (stats.getPercentile(84.1) + projectedTotal));
		System.out.println("2 SV Range: " + (stats.getPercentile(2.3) + projectedTotal) + " - " + (stats.getPercentile(97.7) + projectedTotal));

	}

	public static void main(String[] args) {
		try {
			parseData();
		} catch (ParseException e) {
			System.out.print(e.getMessage());
		}
	}

	public static List<BoxScoreModel> getScores() {
		return scores;
	}

	public static void setScores(List<BoxScoreModel> scores) {
		boxScoreFetcher.scores = scores;
	}

	public static int getAvgVariance() {
		return avgVariance;
	}

	public static void setAvgVariance(int avgVariance) {
		boxScoreFetcher.avgVariance = avgVariance;
	}

}