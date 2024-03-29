/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nikhillo Class that parses a given file into a Document
 */
public class Parser {
	/**
	 * Static method to parse the given file into the Document object
	 * 
	 * @param fileName
	 *                  : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException
	 *                   In case any error occurs during parsing
	 */

	public static int errorCount = 0;
	public static int titleCount = 0, authorCount = 0, dateCount = 0, placeCount = 0, contentCount = 0;

	public static Document parse(String fileName) throws ParserException {
		String fileId = null, category = null, title = null, author = null, authorOrg = null, newsDate = null, place = null, content = null;

		try {
			int lastPointerPosition = 0;

			/* Read file's body into a single string */
			String fileBody = null;
			try {
				fileBody = new Scanner(new File(fileName)).useDelimiter("\\A").next();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/* File ID */
			int fileNamePosition = fileName.lastIndexOf("\\") >= 0 ? fileName.lastIndexOf("\\") + 1 : fileName.lastIndexOf("/") + 1;
			fileId = fileName.substring(fileNamePosition);

			/* Category ID */
			int categoryPosition = fileName.substring(0, fileNamePosition - 1).lastIndexOf("\\") >= 0 ? fileName.substring(0,
					fileNamePosition - 1).lastIndexOf("\\") + 1 : fileName.substring(0, fileNamePosition - 1)
					.lastIndexOf("/") + 1;
			category = fileName.substring(categoryPosition, fileNamePosition - 1);

			/* Title */
			Pattern pattern = Pattern.compile("[\\r\\n\\s]*");
			Matcher matcher = pattern.matcher(fileBody);
			if (matcher.find()) {
				lastPointerPosition += matcher.end();
				pattern = Pattern.compile(".*");
				matcher = pattern.matcher(fileBody.substring(lastPointerPosition));
				if (matcher.find()) {
					title = matcher.group();
					titleCount++;
					lastPointerPosition += matcher.end();
				}
			}

			/* Author */
			pattern = Pattern.compile("[\\r\\n\\s]*");
			matcher = pattern.matcher(fileBody.substring(lastPointerPosition));
			if (matcher.find()) {
				lastPointerPosition += matcher.end();
				pattern = Pattern.compile("<[aA][uU][tT][hH][oO][rR]>\\s*[bB][yY]\\s*");
				matcher = pattern.matcher(fileBody.substring(lastPointerPosition));
				if (matcher.find()) {
					lastPointerPosition += matcher.end();
					pattern = Pattern.compile("(.+?)</AUTHOR>\\s*");
					matcher = pattern.matcher(fileBody.substring(lastPointerPosition));

					if (matcher.find()) {
						author = matcher.group().substring(0, matcher.group().indexOf("<"));
						if (author.contains(",")) {
							authorOrg = author.substring(author.indexOf(",") + 1).trim();
							author = author.substring(0, author.indexOf(","));
						}
						authorCount++;
						lastPointerPosition += matcher.end();
					}
				}
			}

			/* Date */
			int dateStartPosition = -1, dateEndPosition = -1;
			pattern = Pattern.compile("[\\r\\n\\s]*");
			matcher = pattern.matcher(fileBody.substring(lastPointerPosition));
			if (matcher.find()) {
				lastPointerPosition += matcher.end();
				pattern = Pattern.compile("(jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(t(ember)?)?|oct(ober)?|nov(ember)?|dec(ember)?)\\s*[\\d]*");
				matcher = pattern.matcher(fileBody.substring(lastPointerPosition).toLowerCase());

				if (matcher.find()) {
					dateStartPosition = matcher.start() + lastPointerPosition;
					dateEndPosition = matcher.end() + lastPointerPosition;
					newsDate = fileBody.substring(dateStartPosition, dateEndPosition);
					dateCount++;

					/* Place */
					if (fileBody.substring(lastPointerPosition, dateStartPosition).lastIndexOf(",") >= 0) {
						int placeEndPosition = fileBody.substring(lastPointerPosition, dateStartPosition)
								.lastIndexOf(",") + lastPointerPosition;
						place = fileBody.substring(lastPointerPosition, placeEndPosition);
						placeCount++;
					}
					lastPointerPosition = dateEndPosition;
				}
			}

			/* Content */
			if (fileBody.substring(lastPointerPosition) != null) {
				content = fileBody.substring(lastPointerPosition).trim();
				content = (content.charAt(0) == '-') ? content.substring(1).trim() : content;
				contentCount++;
			}

			System.out.println("\nFile ID: " + fileId);
			System.out.println("Category: " + category);
			System.out.println("Title: " + title);
			System.out.println("Author: " + author);
			System.out.println("Author org: " + authorOrg);
			System.out.println("Date: " + newsDate);
			System.out.println("Place: " + place);
			System.out.println("Content: " + content);

			Document document = new Document();
			document.setField(FieldNames.TITLE, title);
			document.setField(FieldNames.AUTHOR, author);
			document.setField(FieldNames.NEWSDATE, newsDate);
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();

			errorCount++;
		}
		return null;
	}
}
