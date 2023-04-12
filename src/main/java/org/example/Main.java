package org.example;


import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
//        BufferedReader reader = new BufferedReader(new FileReader("test2.txt"));
//        String line;
//
//        while((line = reader.readLine()) != null){
//            String[] numbers = line.split("\t");
//            Double[] data = new Double[numbers.length];
//
//            for(int i=0;i<data.length;i++){
//                data[i] = Double.parseDouble(numbers[i]);
//            }
//
//            Statistics statistics = new Statistics(Arrays.asList(data), false);
//
//            System.out.println(statistics.confidenceIntervalOfNPercent(95));
//        }

//        BufferedReader reader = new BufferedReader(new FileReader("test3.txt"));
//        String line;
//
//        List<Pair<Double, Double>> intervals = new ArrayList<>();
//        while((line = reader.readLine()) != null){
//            String[] datas = line.split("\t\t");
//
//            Statistics s1 = extractData(datas[0]);
//            Statistics s2 = extractData(datas[1]);
//
//            intervals.add(s1.calculateDiffWithConfidence(s2, 95));
//        }
//
//        reader.close();
//
//        int index = 0;
//        reader = new BufferedReader(new FileReader("test4.txt"));
//        while((line = reader.readLine()) != null){
//            String[] interval = line.split("\t");
//
//            double rounded1 = Math.round(intervals.get(index).getFirst() * 100) / 100.0;
//            double rounded2 = Math.round(intervals.get(index).getSecond() * 100) / 100.0;
//
//            if(Double.parseDouble(interval[0]) != rounded1 ||
//                    Double.parseDouble(interval[1]) != rounded2){
//                System.out.println("Something is wrong at line " + (index + 1));
//            }
//
//            index++;
//        }

//        BufferedReader reader = new BufferedReader(new FileReader("hyp1.txt"));
//        String line;
//
//        List<Double> prices = new ArrayList<>();
//        while((line = reader.readLine()) != null){
//            prices.add(Double.parseDouble(line));
//        }
//
//        Statistics statistics = new Statistics(prices, 15000.0);
//
//        if(statistics.test(113000, Statistics.TEST_TYPE.TWO_SIDED, 95)){
//            System.out.println("Reject!");
//        }
//        else{
//            System.out.println("Not enough evidence! :(");
//        }

//        BufferedReader reader = new BufferedReader(new FileReader("hyp2.txt"));
//        String line;
//
//        List<Double> percents = new ArrayList<>();
//
//        while((line = reader.readLine()) != null){
//            percents.add(Double.parseDouble(line));
//        }
//
//        System.out.print(new Statistics(percents, false).pValue(
//                40,
//                Statistics.TEST_TYPE.RIGHT_TAIL));
//
//        System.out.println(new Statistics(percents, false).test(
//                40,
//                Statistics.TEST_TYPE.RIGHT_TAIL,
//                95));

//        Statistics statistics = new Statistics(10.0, true, 58.0, 100);
//        Statistics statistics2 = new Statistics(6.0, true, 65.0, 70);
//
//        System.out.println(statistics.pValueWithDiff(statistics2, Statistics.TEST_TYPE.TWO_SIDED, -4));
//
//        ExcelReader reader = new ExcelReader("testing 2.xlsx");
//        double[] salaries = reader.getNumberListFromColumnInCell("Salary", 1);
//        double[] salaries2 = reader.getNumberListFromColumnInCell("Salary", 2);
//
//        List<Double> list1 = new ArrayList<>();
//        List<Double> list2 = new ArrayList<>();
//
//        for (double d : salaries){
//            list1.add(d);
//        }
//
//        for (double d : salaries2){
//            list2.add(d);
//        }
//        Statistics s1 = new Statistics(list1, false);
//        Statistics s2 = new Statistics(list2, false);
//
//        System.out.println(s1.pValueWithDiff(s2, Statistics.TEST_TYPE.TWO_SIDED, 0));
//        System.out.println(s1.testDiff(s2, 0, Statistics.TEST_TYPE.TWO_SIDED, 95));

        //testCorrelation();
    }

    static Statistics extractData(String line){
        String[] numbers = line.split("\t");
        Double[] data = new Double[numbers.length];

        for(int i=0;i<data.length;i++){
            data[i] = Double.parseDouble(numbers[i]);
        }

        Statistics statistics = new Statistics(Arrays.asList(data), false);

        return statistics;
    }

    static void testCorrelation() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("correlation.txt"));
        String line;

        List<Double> list1 = new ArrayList<>();
        List<Double> list2 = new ArrayList<>();

        while((line = reader.readLine()) != null){
            String[] data = line.split("\t");
            list1.add(Double.parseDouble(data[0]));
            list2.add(Double.parseDouble(data[1]));
        }

        Statistics s1 = new Statistics(list1, false);
        Statistics s2 = new Statistics(list2, false);

        try {
            System.out.println(s1.correlationWith(s2));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class Statistics{
    private final List<Double> dataset;
    private final Double populationDeviation;
    private Double cachedMean;
    private Double cachedStd;
    private Integer size;

    public enum TEST_TYPE {
        TWO_SIDED,
        LEFT_TAIL,
        RIGHT_TAIL
    }

    public Statistics(List<Double> dataset, Double populationDeviation) {
        this.dataset = dataset;
        this.populationDeviation = populationDeviation;

        Collections.sort(dataset);
    }

    public Statistics(List<Double> dataset, boolean isPopulation){
        this.dataset = dataset;

        if(isPopulation){
            populationDeviation = mDeviation(0);
        }
        else{
            populationDeviation = null;
        }
    }

    public Statistics(Double cachedStd, boolean isPopulation, Double cachedMean, int size){
        if(isPopulation){
            populationDeviation = cachedStd;
        }
        else{
            populationDeviation = null;
        }

        this.cachedMean = cachedMean;
        this.cachedStd = cachedStd;
        this.size = size;

        dataset = null;
    }

    public int size(){
        if(size != null){
            return size;
        }

        return dataset.size();
    }

    public double mean(){
        if(cachedMean == null){
            double mean = 0;
            for (Double aDouble : dataset) {
                mean += aDouble;
            }

            cachedMean = mean / dataset.size();
        }

        return cachedMean;
    }

    public double std(){
        if(cachedStd == null){
            cachedStd = Objects.requireNonNullElseGet(populationDeviation, () -> mDeviation(1));
        }

        return cachedStd;
    }

    public double skewScore(){
        double mean = mean();
        double median = mMedian();

        return 3 * (mean - median) / mDeviation(0);
    }

    public Pair<Double, Double> confidenceIntervalOfNPercent(double n){
        double percent = n / 100;
        double standardError = std() / Math.sqrt(size());

        double pInTheCorner = (1 - percent) / 2;
        double statistic;

        if(populationDeviation != null){
            statistic = Math.abs(new NormalDistribution().inverseCumulativeProbability(pInTheCorner));
        }
        else{
            statistic = Math.abs(new TDistribution(size()).inverseCumulativeProbability(pInTheCorner));
        }

        // middle n% of data.
        return new Pair<>(mean() - statistic * standardError, mean() + statistic * standardError);
    }

    public double pValue(double hypothesis, TEST_TYPE testType){
        double standardError = std() / Math.sqrt(size());
        double score = (mean() - hypothesis) / standardError;
        return mCalculatePValue(testType, score);
    }

    public double pValueWithDiff(Statistics statistics, TEST_TYPE testType, double hypothesis){

        double mean = mean() - statistics.mean();
        double standardError = mStandardErrorOfTwoSamplesDiff(statistics);
        double score = (mean - hypothesis) / standardError;

        return mCalculatePValue(testType, score);
    }

    public boolean test(double hypothesis, TEST_TYPE testType, double n){
        double percent = n / 100;
        double standardError = std() / Math.sqrt(size());

        AbstractRealDistribution distribution = mFetchDistribution();

        double pInTheCorner = (1 - percent) / 2;
        double bound = Math.abs(distribution.inverseCumulativeProbability(pInTheCorner));

        double score = (mean() - hypothesis) / standardError;

        return mTesting(score, bound, testType);
    }

    public boolean testDiff(Statistics statistics, double hypothesis, TEST_TYPE testType, double n){
        double percent = n / 100;
        double standardError = mStandardErrorOfTwoSamplesDiff(statistics);

        AbstractRealDistribution distribution = mFetchDistribution();

        double pInTheCorner = (1 - percent) / 2;
        double bound = Math.abs(distribution.inverseCumulativeProbability(pInTheCorner));
        double score = (mean() - statistics.mean() - hypothesis) / standardError;

        return mTesting(score, bound, testType);
    }

    public Pair<Double, Double> calculateDiffWithConfidence(Statistics statistics, double confidenceInPercent){
        double percent = confidenceInPercent / 100;
        double pInTheCorner = (1 - percent) / 2;

        double standardError = mStandardErrorOfTwoSamplesDiff(statistics);
        double mean = mean() - statistics.mean();

        if(populationDeviation != null && statistics.populationDeviation != null){
            double z = Math.abs(new NormalDistribution().inverseCumulativeProbability(pInTheCorner));

            return new Pair<>(mean - z * standardError, mean + z * standardError);
        }
        else{
            double t = Math.abs(new TDistribution(size() + statistics.size() - 2)
                    .inverseCumulativeProbability(pInTheCorner));

            return new Pair<>(mean - t * standardError, mean + t * standardError);
        }
    }
    public double correlationWith(Statistics statistics) throws Exception{
        if(statistics.size() != size()){
            throw new Exception("Two datasets does not have the same size!");
        }

        double cov = 0;
        double mean1 = mean();
        double mean2 = statistics.mean();

        List<Double> dataset2 = statistics.dataset;

        for(int i=0;i<size();i++){
            cov += (dataset.get(i) - mean1) * (dataset2.get(i) - mean2);
        }

        if(populationDeviation != null && statistics.populationDeviation != null){
            cov /= size();

            return cov / (populationDeviation * statistics.populationDeviation);
        }
        else{
            cov /= (size() - 1);

            return cov / (std() * statistics.std());
        }
    }

    private boolean mTesting(double statistic, double bound, TEST_TYPE testType){
        if(statistic < -bound && testType == TEST_TYPE.RIGHT_TAIL){
            return true; // reject
        }
        else if(statistic > bound && testType == TEST_TYPE.LEFT_TAIL){
            return true; // reject
        }
        else if(Math.abs(statistic) > bound && testType == TEST_TYPE.TWO_SIDED){
            return true; // reject
        }

        return false; // not enough statistical evidence to reject H0
    }

    private double mMedian(){
        if(dataset.size() % 2 == 0){
            return (dataset.get(dataset.size() / 2 - 1) + dataset.get(dataset.size() / 2)) / 2;
        }

        return dataset.get(dataset.size() / 2);
    }

    private double mDeviation(int sampleCoeff){
        double deviation = 0;
        double mean = mean();

        for(int i=0;i<dataset.size();i++){
            deviation += (dataset.get(i) - mean) * (dataset.get(i) - mean);
        }

        return Math.sqrt((deviation / (dataset.size() - sampleCoeff)));
    }

    private double mCalculatePValue(TEST_TYPE testType, double score){
        AbstractRealDistribution distribution = mFetchDistribution();

        if(testType == TEST_TYPE.LEFT_TAIL){
            return distribution.cumulativeProbability(score);
        }
        else if(testType == TEST_TYPE.RIGHT_TAIL){
            return 1 - distribution.cumulativeProbability(score);
        }
        else{
            return 2 * (1 - distribution.cumulativeProbability(Math.abs(score)));
        }
    }

    private double mStandardErrorOfTwoSamplesDiff(Statistics statistics){
        int n1 = size();
        int n2 = statistics.size();

        if(populationDeviation != null && statistics.populationDeviation != null){
            double var1 = populationDeviation * populationDeviation;
            double var2 = statistics.populationDeviation * statistics.populationDeviation;

            return Math.sqrt(var1 / n1 + var2 / n2);
        }
        else{
            double var1 = std();
            var1 *= var1;
            double var2 = statistics.std();
            var2 *= var2;

            // assuming they have the same population variance
            double pooledVar = (var1*(n1 - 1) + var2*(n2 - 1)) / (n1 + n2 - 2);
            return Math.sqrt(pooledVar / n1 + pooledVar / n2);
        }
    }

    private AbstractRealDistribution mFetchDistribution(){
        if(populationDeviation != null){
            return new NormalDistribution();
        }

        return new TDistribution(size());
    }
}

class ExcelReader{
    private final XSSFWorkbook workbook;

    ExcelReader(String path) throws IOException {
        File file = new File(path);

        FileInputStream fIn = new FileInputStream(file);
        workbook = new XSSFWorkbook(fIn);

        fIn.close();
    }
    public double[] getNumberListFromColumnInCell(String colName, int sheetNum) throws IllegalStateException {
        XSSFSheet sheet = workbook.getSheetAt(sheetNum);
        int colNum = mFindHeaderIn(sheet, colName);

        if(colNum == -1){
            return null;
        }

        double[] doubles = new double[sheet.getLastRowNum()];
        Arrays.fill(doubles, Double.MAX_VALUE);

        for(int i=1;i<sheet.getPhysicalNumberOfRows();i++){
            XSSFRow row = sheet.getRow(i);
            if(row == null || row.getCell(colNum) == null){
                continue;
            }

            doubles[i - 1] = row.getCell(colNum).getNumericCellValue();
        }

        int cutoff = -1;
        for(int i=doubles.length -1;i>=0;i--){
            if(doubles[i] != Double.MAX_VALUE){
                cutoff = i;
                break;
            }
        }

        return Arrays.copyOfRange(doubles, 0, cutoff + 1);
    }

    public String[] getStringListFromColumnInCell(String colName, int sheetNum) {
        XSSFSheet sheet = workbook.getSheetAt(sheetNum);
        int colNum = mFindHeaderIn(sheet, colName);

        if(colNum == -1){
            return null;
        }

        String[] strings = new String[sheet.getLastRowNum()];

        for(int i=1;i<sheet.getPhysicalNumberOfRows();i++){
            XSSFRow row = sheet.getRow(i);

            if(row == null || row.getCell(colNum) == null){
                continue;
            }

            strings[i - 1] = row.getCell(colNum).getStringCellValue();
        }

        int cutoff = -1;
        for(int i=strings.length -1;i>=0;i--){
            if(strings[i] != null){
                cutoff = i;
                break;
            }
        }

        return Arrays.copyOfRange(strings, 0, cutoff + 1);
    }

    public Object[] query(int sheetNum, String query, String colName){
        String[] queries;
        if(!query.contains("&")){
            queries = new String[1];
            queries[0] = query;
        }
        else{
            queries = query.split(" & ");
        }

        XSSFSheet sheet = workbook.getSheetAt(sheetNum);
        int numberOfRows = sheet.getPhysicalNumberOfRows();
        List<Integer> indices = new ArrayList<>(numberOfRows);

        for(int i=1;i<numberOfRows;i++){
            indices.add(i);
        }

        int colNum = mFindHeaderIn(sheet, colName);
        CellType colType  = mGetColumnTypeIn(sheet, colName);

        for(String q : queries){
            String[] components = q.split(" ");

            switch (components[0].toLowerCase()){
                case "first":
                    // Ex: First 3
                    indices = indices.subList(0, Integer.parseInt(components[1]));
                    break;
                case "from":
                    // Ex: From 3 to 4
                    indices = indices.subList(Integer.parseInt(components[1]) - 1, Integer.parseInt(components[1]));
                    break;
                case "with":
                    // Ex: with Salary > 10k

                    CellType refColType = mGetColumnTypeIn(sheet, components[1]);

                    if(refColType == CellType.NUMERIC){
                        indices = mOperatorQueryHandlerForNumberColumn(components[2], components[3], indices,
                                sheetNum, components[1]);
                    }
                    else if(refColType == CellType.STRING || refColType == CellType.BOOLEAN){
                        indices = mOperatorQueryHandlerForStringColumn(components[2], components[3], indices,
                                sheetNum, components[1]);
                    }
                    break;
                case "sort":
                    int sortByColNum = colNum;
                    CellType refColType2 = colType;

                    if(components.length == 2){
                        sortByColNum = mFindHeaderIn(sheet, components[1]);
                        refColType2 = mGetColumnTypeIn(sheet, components[1]);
                    }

                    List<Item> items = new ArrayList<>();

                    for(int i : indices){
                        try{
                            if(refColType2 == CellType.NUMERIC){
                                items.add(new Item(sheet.getRow(i).getCell(sortByColNum).getNumericCellValue(), i));
                            }
                            else{
                                items.add(new Item(sheet.getRow(i).getCell(sortByColNum).getStringCellValue(), i));
                            }
                        }
                        catch (Exception e){
                            break;
                        }
                    }

                    Collections.sort(items);
                    indices.clear();

                    for(Item item : items){
                        indices.add(item.position);
                    }
                    break;
                default:
                    // Ex: >= 35k;

                    if(colType == CellType.NUMERIC){
                        indices = mOperatorQueryHandlerForNumberColumn(components[0], components[1], indices,
                                sheetNum, colName);
                    }
                    else if(colType == CellType.STRING || colType == CellType.BOOLEAN){
                        indices = mOperatorQueryHandlerForStringColumn(components[0], components[1], indices,
                                sheetNum, colName);
                    }
            }
        }

        Object[] data = new Object[indices.size()];

        for(int i=0;i<indices.size();i++){
            int index = indices.get(i);

            if(colType == CellType.NUMERIC){
                data[i] = sheet.getRow(index).getCell(colNum).getNumericCellValue();
            }
            else{
                data[i] = sheet.getRow(index).getCell(colNum).getStringCellValue();
            }
        }

        return data;
    }

    private List<Integer> mOperatorQueryHandlerForStringColumn(String operator, String searchTerm,
                                                       List<Integer> indices, int sheetNum, String colName){
        XSSFSheet sheet = workbook.getSheetAt(sheetNum);
        int colNum = mFindHeaderIn(sheet, colName);

        List<Integer> ints = new ArrayList<>();

        for(int i : indices){
            Row row = sheet.getRow(i);

            if(row == null || row.getCell(colNum) == null){
                continue;
            }

            String value = row.getCell(colNum).getStringCellValue();

            if(mConditionMatched(operator, value.compareTo(searchTerm))){
                ints.add(i);
            }
        }

        return ints;
    }

    private List<Integer> mOperatorQueryHandlerForNumberColumn(String operator, String searchTerm,
                                                               List<Integer> indices, int sheetNum, String colName){
        XSSFSheet sheet = workbook.getSheetAt(sheetNum);
        int colNum = mFindHeaderIn(sheet, colName);

        List<Integer> ints = new ArrayList<>();

        for(int i : indices){
            Row row = sheet.getRow(i);

            if(row == null || row.getCell(colNum) == null){
                continue;
            }

            double value = row.getCell(colNum).getNumericCellValue();

            if(mConditionMatched(operator, (int)(value - Double.parseDouble(searchTerm)))){
                ints.add(i);
            }
        }

        return ints;
    }

    private boolean mConditionMatched(String operator, int compareResult){
        return (compareResult < 0 && operator.contains("<")) ||
                (compareResult == 0 && operator.contains("=")) ||
                (compareResult > 0 && operator.contains(">"));
    }

    private int mFindHeaderIn(XSSFSheet sheet, String headerName){
        XSSFRow headerRow = sheet.getRow(0);

        int colNum = -1;
        for(Cell cell : headerRow){
            String columnName = cell.getStringCellValue();

            if(columnName.equals(headerName)){
                colNum = cell.getColumnIndex();
                break;
            }
        }

        return colNum;
    }

    private CellType mGetColumnTypeIn(XSSFSheet sheet, String colName){
        int colNum = mFindHeaderIn(sheet, colName);

        return sheet.getRow(1).getCell(colNum).getCellType();
    }

    private static class Item implements Comparable<Item>{
        Object value;
        int position;

        Item(Object value, int position){
            this.value = value;
            this.position = position;
        }

        @Override
        public int compareTo(Item o) {
            if(value instanceof String){
                return ((String)value).compareTo((String) o.value);
            }
            else{
                return (int)((Double) value - (Double) o.value);
            }
        }
    }
}