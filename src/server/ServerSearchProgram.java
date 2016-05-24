package server;

import java.io.IOException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ServerSearchProgram {
	/*
	 * Denna klass ska ta emot en sträng som representerar ett program. Hämta
	 * information från schema och returnera salen för nästa aktivitet.
	 */
	// URL adress där schemat finns.

	private String URLProgram = "http://schema.mah.se/setup/jsp/Schema."
			+ "jsp?startDatum=idag&intervallTyp=m&intervallAntal="
			+ "6&sprak=SV&sokMedAND=true&forklaringar=true&resurser=p.";
	private String URLKurs = "http://schema.mah.se/setup/jsp/Schema."
			+ "jsp?startDatum=idag&intervallTyp=m&intervallAntal="
			+ "6&sprak=SV&sokMedAND=true&forklaringar=true&resurser=k.";

	// metod som tar emot programid och returnerar rätt sal

	public String searchProgram(String query) throws IOException {

		URLProgram += query;

		String room = "";
		int row = 4;
		boolean roomFound = false;

		Document document;

		document = Jsoup.connect(URLProgram).get();

		String col = getColumn(document);

		// om inte rätt kolumn hittats så är det sökta id en kurs eller finns
		// inte.

		if (col.equals("notFound")) {
			room = searchCourse(query);
		}

		// loop som fortsätter tills salen är hittad.
		// Om inte första aktivitet har en specifik lokal går loopen vidare
		// till
		// nästa aktivitet.
		else {
			while (!roomFound) {

				Elements answers = document.select(".schemaTabell > tbody:nth-child(1) > tr:nth-child(" + row
						+ ") > td:" + col + " > a:nth-child(1)");

				if (answers != null) {

					for (Element answer : answers) {
						room = answer.text();
						roomFound = true;
					}
				}
				row++;
			}
		}

		return room;

	}

	public String searchCourse(String query) throws IOException {

		String room = "";
		URLKurs += query;

		Document document;
		try {
			document = Jsoup.connect(URLKurs).get();
		} catch (HttpStatusException e) { // om kursen inte finns returneras ett
											// felmeddelande
			return "Error";
		}

		String col = getColumn(document);
		if (col.equals("notFound")) {
			return "Error";
		}
		
		int row = 4;
		boolean roomFound = false;

		while (!roomFound) {

			Elements answers = document.select(
					".schemaTabell > tbody:nth-child(1) > tr:nth-child(" + row + ") > td:" + col + " > a:nth-child(1)");

			if (answers != null) {

				for (Element answer : answers) {
					room = answer.text();
					roomFound = true;
				}
			}
			row++;
		}

		return room;
	}

	// schemat för de olika programmen kan se olika ut. Denna metod hittar i
	// vilken kolumn som lokalid finns.

	public String getColumn(Document document) throws IOException {
		String correct = "";

		boolean found = false;
		int count = 1;
		long startTime = System.currentTimeMillis();

		while (!found && (System.currentTimeMillis() - startTime) < 1000) {
			Elements columns = document.select("tr.heading:nth-child(1) > th:nth-child(" + count + ")");
			for (Element column : columns) {

				if (column.text().equals("Lokal")) {
					correct = "nth-child(" + (count) + ")";
					found = true;
				}
				count++;
			}
		}

		if (!found) {
			return "notFound";
		}
		return correct;
	}

}
