package com.edgar.analyzer;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CAAParser implements AutoCloseable {

    private final String fileLocation;
    private final BufferedReader rd;

    public CAAParser(String fileName) throws Exception {
        this.fileLocation = getClass().getClassLoader()
                .getResource(fileName).getPath();
        FileReader fileReader = new FileReader(this.fileLocation);
        this.rd = new BufferedReader(fileReader);
    }

    public String readFile() throws Exception {
        StringBuilder fileStr = new StringBuilder();
        String line;
        while ((line = this.rd.readLine()) != null) {
            fileStr.append(line);
        }
        return fileStr.toString();
    }

    public String fetchActual10kText(String entire10kText) {
        Pattern pattern = Pattern.compile("(?s)(?m)<TYPE>10-K.*?(</TEXT>)");
        Matcher matcher = pattern.matcher(entire10kText);

        List<String> filtered10kText = new ArrayList<>();
        while (matcher.find()) {
            filtered10kText.add(matcher.group());
        }
        return StringUtils.join(filtered10kText, " ");
    }

    public String replacement10kText(String filtered10kText) {
        return filtered10kText.replaceAll("((?i)<TYPE>).*?(?=<)", "");
    }

    public String tagRemoved10kText(String replaced10kText) {
        String replaceSequenceTag = replaced10kText.replaceAll("((?i)<SEQUENCE>).*?(?=<)", "");
        String replaceFilenameTag = replaceSequenceTag.replaceAll("((?i)<FILENAME>).*?(?=<)", "");
        String replaceDescriptionTag = replaceFilenameTag.replaceAll("((?i)<DESCRIPTION>).*?(?=<)", "");
        String replaceHeadTag = replaceDescriptionTag.replaceAll("(?s)(?i)<head>.*?</head>", "");
        String replaceTableTag = replaceHeadTag.replaceAll("(?s)(?i)<(table).*?(</table>)", "");
        return replaceTableTag;
    }

    public String itemTagRemoved10kText(String tagRemoved10kText) {
        return tagRemoved10kText.replaceAll("(?s)(?i)(?m)> +Item|>Item|^Item", ">Â°Item");
    }

    public String replaceAllHtmlTags(String itemTagRemoved10kText) {
        return itemTagRemoved10kText.replaceAll("(?s)<.*?>", " ");
    }

    public String replaceUnicodeStrings(String htmlTagsRemovedText) {
        return htmlTagsRemovedText.replaceAll("&(.{2,6});", " ");
    }

    public String removedGroupSpaces(String unicodeStringsReplacedText) {
        return unicodeStringsReplacedText.replaceAll("(?s) +", " ");
    }

    public String applyAlgorithm() throws Exception {
        String initialText = this.readFile();
        String fetchedText = this.fetchActual10kText(initialText);
        String replacedText = this.replacement10kText(fetchedText);
        String tagRemoved10kText = this.tagRemoved10kText(replacedText);
        String itemTagRemoved10kText = this.itemTagRemoved10kText(tagRemoved10kText);
        String htmlTagsRemovedText = this.replaceAllHtmlTags(itemTagRemoved10kText);
        String unicodeStringsReplacedText = this.replaceUnicodeStrings(htmlTagsRemovedText);
        return this.removedGroupSpaces(unicodeStringsReplacedText);
    }

    @Override
    public void close() throws Exception {
        this.rd.close();
    }

    public static void main(String[] args) throws Exception {
        CAAParser parser = new CAAParser("data.txt");
        System.out.println(parser.applyAlgorithm());
    }
}